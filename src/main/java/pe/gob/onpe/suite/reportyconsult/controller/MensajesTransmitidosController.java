/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatCentroComputo;
import pe.gob.onpe.suite.domain.MensajeTransmitido;
import pe.gob.onpe.suite.domain.MensajeTransmitidoReport;
import pe.gob.onpe.suite.reportyconsult.service.RCMensajesTransmitidoService;
import pe.gob.onpe.suite.reportyconsult.service.RcMonitoreodeActaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class MensajesTransmitidosController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage, stage;
    private Scene scene;
    private String name = ConstantCommon.titleReporCons;
    Timer timer;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox cmbCentroComp, cmbEstado, cmbMensaje, cmbConexion, cmbTiposcc;
    @FXML
    private TextField loadTime;
    @FXML
    private Label sumaPendientes, sumaRealizada, sumaTotal;
    @FXML
    TableView<MensajeTransmitido> tablaPrincipal;
    @FXML
    private TableColumn<MensajeTransmitido, String> codigo, descripcion, enlace, estado, fenvio, frecepcion, tipo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Autowired
    RCMensajesTransmitidoService rcMensajesTransmitidoService;
    @Autowired
    RcMonitoreodeActaService rcMonitoreodeActaService;
    @Autowired
    IComunService comunService;

    HashMap<String, String> mapEstados = new HashMap();
    ObservableList<MensajeTransmitido> data;
    List<MensajeTransmitido> listaObjeto = new ArrayList<>();

    public void init() {
        try {

            stage = new Stage();
            stage.initOwner(mainStage);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setX(350);
            stage.setY(100);

            AnchorPane anchorPaneAux = new AnchorPane();
            anchorPaneAux.setId("AnchorPane");
            this.getView().getStyleClass().add("bodyComun");
            anchorPaneAux.getChildren().add(this.getView());

            scene = new Scene(anchorPaneAux);
            scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
            subBarraStage(stage, ConstantReportes.nameTituloPuestasaCero, 2);

            LlenarEstados();
            LlenarTipoMensajes();
            LlenarTiposcc();
            LlenarConexion();
            LlenarCenComputoControl();
            filtroKeyEvent();
            cmdBotones_Click(0);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
            e.printStackTrace();
        }

    }

    private void filtroKeyEvent() {

        loadTime.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!ConstantReportes.NUMBER.contains(event.getCharacter())) {
                event.consume();
            }
            if (loadTime.getText().length() >= ConstantReportes.MAXLENGTH) {
                event.consume();
            }
        });

        loadTime.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (loadTime.getText().equals(ConstantReportes.VACIO) || loadTime.getText().equals(ConstantReportes.VALORCERO)) {
                    loadTime.setText(ConstantReportes.TIMEDEFAULT);
                    System.out.println("Current time [BEFORE]:" + GregorianCalendar.getInstance().getTime());
                    ejecutarTarea(loadTime.getText());
                } else {
                    System.out.println("Current time [BEFORE]:" + GregorianCalendar.getInstance().getTime());
                    ejecutarTarea(loadTime.getText());
                }
            }
        });
    }

    @FXML
    public void btnConsultarOnAction() {
        try {
            System.out.println("Cantida de elementos de la tabla : " + tablaPrincipal.getItems().size());
            limpiador();
            String CodCC = AppController.getCodigoSeleccionadoCombo(cmbCentroComp).equals(ConstantReportes.TODOS_CCOM_ENUM) ? "" : AppController.getCodigoSeleccionadoCombo(cmbCentroComp);
            String CodTipo = cmbMensaje.getValue().toString().substring(0, 2);
            String cboTipo = cmbTiposcc.getValue().toString();
            String tipo1 = ConstantReportes.VACIO;
            String tipo2 = ConstantReportes.VACIO;

            if (null != cboTipo) {
                switch (cboTipo) {
                    case "T1":
                        tipo1 = "T1";
                        tipo2 = ConstantReportes.VACIO;
                        break;
                    case "T2":
                        tipo1 = "T2";
                        tipo2 = ConstantReportes.VACIO;
                        break;
                    case "T3":
                        tipo1 = "T3";
                        tipo2 = ConstantReportes.VACIO;
                        break;
                    case "T4":
                        tipo1 = "T4";
                        tipo2 = ConstantReportes.VACIO;
                        break;
                    case "T1 y T2":
                        tipo1 = "T1";
                        tipo2 = "T2";
                        break;
                    case "T3 y T4":
                        tipo1 = "T3";
                        tipo2 = "T4";
                        break;
                    default:
                        tipo1 = "Todos";
                        tipo2 = ConstantReportes.VACIO;
                        break;
                }
            }

            listaObjeto = rcMensajesTransmitidoService.getMensajesTransmitidos(CodCC, CodTipo, tipo1, tipo2);
            System.out.println("lista de objetos :" + listaObjeto.size());
            if (listaObjeto == null || listaObjeto.size() < 1) {
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Lo Sentimos, No Existen Datos para Mostrar.");
                return;
            }

            String strconexion = getConexion(cmbConexion.getSelectionModel().selectedIndexProperty().getValue());
            String strestado = getEstado(cmbEstado.getSelectionModel().selectedIndexProperty().getValue());
            boolean editarListar = false;
            int SumPendiente = 0;
            int SumRealizada = 0;
            int SumTotal = 0;

            List<MensajeTransmitido> listaEditada = new ArrayList<>();
            listaEditada.clear();

            for (MensajeTransmitido Objeto : listaObjeto) {

                if (!strconexion.equals(ConstantReportes.VACIO) && !strestado.equals(ConstantReportes.VACIO)) {
                    editarListar = true;
                    if (Objeto.getConexion().equals(strconexion) && Objeto.getCompleto().equals(strestado)) {
                        listaEditada.add(Objeto);
                    }
                } else if (!strconexion.equals(ConstantReportes.VACIO)) {
                    editarListar = true;
                    if (Objeto.getConexion().equals(strconexion)) {
                        listaEditada.add(Objeto);
                    }
                } else if (!strestado.equals(ConstantReportes.VACIO)) {
                    editarListar = true;
                    if (Objeto.getCompleto().equals(strestado)) {
                        listaEditada.add(Objeto);
                    }
                }
            }

            if (editarListar) {
                listaObjeto = listaEditada;
            }

            if (listaObjeto == null || listaObjeto.size() < 1) {
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Lo Sentimos, No Existen Datos para Mostrar.");
                return;
            }

            for (MensajeTransmitido listTot : listaObjeto) {
                if (listTot.getCompleto().equals(ConstantReportes.VALORCERO)) {
                    SumPendiente += listTot.getCompleto().length();
                } else {
                    SumRealizada += listTot.getCompleto().length();
                }
            }

            SumTotal = SumPendiente + SumRealizada;

            sumaPendientes.setText(String.valueOf(SumPendiente));
            sumaRealizada.setText(String.valueOf(SumRealizada));
            sumaTotal.setText(String.valueOf(SumTotal));

            data = FXCollections.observableArrayList(listaObjeto);
            tablaPrincipal.getItems().clear();
            tablaPrincipal.setItems(data);
            codigo.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getCodigo()));
            descripcion.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
            enlace.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getConexion().equals(ConstantReportes.COBRE) ? "Cobre" : "Satelital"));
            estado.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getCompleto().equals(ConstantReportes.PENDIENTE) ? "Pendiente" : "Completo"));
            fenvio.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getFechaEnvio()));
            frecepcion.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getFechaRecepcion()));
            tipo.setCellValueFactory((CellDataFeatures<MensajeTransmitido, String> p) -> new SimpleStringProperty(p.getValue().getC_tipo_cc()));

            tablaPrincipal.setRowFactory(new Callback<TableView<MensajeTransmitido>, TableRow<MensajeTransmitido>>() {
                @Override
                public TableRow<MensajeTransmitido> call(TableView<MensajeTransmitido> param) {
                    return new TableRow<MensajeTransmitido>() {
                        @Override
                        protected void updateItem(MensajeTransmitido item, boolean empty) {
                            super.updateItem(item, empty);
                            setStyle("-fx-background-color: #FFFFFF"); //limpiamos el estilo
                            if (!empty) {
                                if (item.getCompleto().equals("0")) {
                                    setStyle("-fx-background-color: #FFBF81");
                                }
                                if (item.getCompleto().equals("1")) {
                                    setStyle("-fx-background-color: #FFFFBF");
                                }
                            }
                        }
                    };
                }
            });

            cmdBotones_Click(1);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
            e.printStackTrace();
        }
    }

    public void LlenarEstados() {
        cargarCombos(2);
        mapEstados.put("0", "Todos");
        mapEstados.put("1", "Pendientes");
        mapEstados.put("2", "Completas");

        Iterator it1 = mapEstados.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry e = (Map.Entry) it1.next();
            cmbEstado.getItems().add(e.getValue());
        }
        cmbEstado.getSelectionModel().selectFirst();
    }

    public void LlenarTipoMensajes() {
        cargarCombos(3);
        HashMap<String, String> mapTipoMensaje = new HashMap();

        mapTipoMensaje.put("00 Puesta a Cero", "00 Puesta a Cero");

        Iterator it1 = mapTipoMensaje.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry e = (Map.Entry) it1.next();
            cmbMensaje.getItems().add(e.getValue());
        }
        cmbMensaje.getSelectionModel().selectFirst();
    }

    public void LlenarTiposcc() {
        cargarCombos(4);
        HashMap<String, String> mapTiposcc = new HashMap();

        mapTiposcc.put("0", "Todos");
        mapTiposcc.put("1", "T1");
        mapTiposcc.put("2", "T2");
        mapTiposcc.put("3", "T3");
        mapTiposcc.put("4", "T4");

        Iterator it = mapTiposcc.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            cmbTiposcc.getItems().add(e.getValue());
        }
        cmbTiposcc.getSelectionModel().selectFirst();

    }

    private void LlenarConexion() {
        cargarCombos(1);
        HashMap<String, String> mapconexion = new HashMap();

        mapconexion.put("0", "Todas");
        mapconexion.put("1", "Cobre");
        mapconexion.put("2", "Satelital");

        Iterator it = mapconexion.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            cmbConexion.getItems().add(e.getValue());

        }
        cmbConexion.getSelectionModel().selectFirst();
    }

    public void LlenarCenComputoControl() {

        try {
            cargarCombos(0);
            List<CatCentroComputo> CargaCenComputo = rcMensajesTransmitidoService.getCentroComputo(DatosUsuario.getMstrCentroComputo(), DatosUsuario.getMstrODPE());
            for (Iterator<CatCentroComputo> iterator = CargaCenComputo.iterator(); iterator.hasNext();) {
                CatCentroComputo next = iterator.next();

                cmbCentroComp.getItems().add(next.getCodigo() + " - " + next.getDescCompu());
            }
            cmbCentroComp.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cargarCombos(int numero) {
        switch (numero) {
            case 0:
                cmbCentroComp.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        actionCleaner_onAction(event);
                    }
                });
                break;

            case 1:
                cmbConexion.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        actionCleaner_onAction(event);
                    }
                });
                break;

            case 2:
                cmbEstado.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        actionCleaner_onAction(event);
                    }
                });
                break;
            case 3:
                cmbMensaje.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        actionCleaner_onAction(event);
                    }
                });
                break;
            case 4:
                cmbTiposcc.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        actionCleaner_onAction(event);
                    }
                });
                break;
        }

    }

    private String getConexion(int conexion) {

        String strcad = ConstantReportes.VACIO;
        switch (conexion) {

            case 0:
                strcad = ConstantReportes.VACIO;
                break;
            case 1:
                strcad = ConstantReportes.COBRE;
                break;
            case 2:
                strcad = ConstantReportes.SATELITAL;
                break;
        }
        return strcad;
    }

    private String getEstado(int estado) {

        String Completo = ConstantReportes.VACIO;
        switch (estado) {

            case 0:
                Completo = ConstantReportes.VACIO;
                break;
            case 1:
                Completo = ConstantReportes.PENDIENTE;
                break;
            case 2:
                Completo = ConstantReportes.COMPLETO;
                break;
        }
        return Completo;
    }

    private void limpiador() {

        sumaPendientes.setText(ConstantReportes.VACIO);
        sumaRealizada.setText(ConstantReportes.VACIO);
        sumaTotal.setText(ConstantReportes.VACIO);
        tablaPrincipal.getItems().clear();
        listaObjeto.clear();
        if (data != null) {
            data.clear();
        }

    }

    @FXML
    private void btnActualizarOnAction() {
        btnConsultarOnAction();
    }

    @FXML
    private void btnImprimirOnAction() {

        try {

            if (data == null || data.size() < 1) {
                mostrarMensajeRPT(ConstantCommon.titleAviso, "Debe Consultar Antes, No Existen Datos para Mostrar");
            } else {
                String DscCC = AppController.getDescripcionSeleccionadaCombo(cmbCentroComp);
                String DscTipo = cmbMensaje.getValue().toString().substring(2);
                String DscConexion = cmbConexion.getValue().toString();
                String DescEstado = cmbEstado.getValue().toString();

                String SumTotal;
                String StrSumCompletoCobre;
                String StrSumCompletoSatelital;
                String StrSumPendienteSatelital;
                String StrSumPendienteCobre;
                String StrSumTotCobre;
                String StrSumTotSat;
                String StrSumTotPendiente;
                String StrSumTotCompletos;

                int sumCompletoCobre = 0;
                int sumCompletoSatelital = 0;
                int SumPendienteSatelital = 0;
                int SumPendienteCobre = 0;
                int SumTotCobre = 0;
                int SumTotSat = 0;
                int SumTotPendiente = 0;
                int SumTotCompletos = 0;

                InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteMesajeTransmitidos + ".jrxml");
                InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
                JasperReportUtil jp = new JasperReportUtil();
                Map<String, Object> parametros = new HashMap<>();

                List<MensajeTransmitidoReport> listReport = new ArrayList<>();

                for (MensajeTransmitido bean : data) {
                    MensajeTransmitidoReport listReporte = new MensajeTransmitidoReport();

                    listReporte.setCodigoCC(bean.getCodigo());
                    listReporte.setNombreCC(bean.getDescripcion());
                    listReporte.setEnlace(bean.getConexion().equals(ConstantReportes.COBRE) ? "Cobre" : "Satelital");
                    listReporte.setEstado(bean.getCompleto().equals(ConstantReportes.PENDIENTE) ? "Pendiente" : "Completo");
                    listReporte.setfCero(bean.getFechaEnvio());
                    listReporte.setfRecep(bean.getFechaRecepcion());
                    listReporte.setTipo(bean.getC_tipo_cc());
                    listReport.add(listReporte);
                }
                for (MensajeTransmitido bean : data) {
                    if (bean.getConexion().equals(ConstantReportes.COBRE) && bean.getCompleto().equals(ConstantReportes.COMPLETO)) {
                        sumCompletoCobre += bean.getCompleto().length();
                    }
                    if (bean.getConexion().equals(ConstantReportes.COBRE) && bean.getCompleto().equals(ConstantReportes.PENDIENTE)) {
                        SumPendienteCobre += bean.getCompleto().length();
                    }
                    if (bean.getConexion().equals(ConstantReportes.SATELITAL) && bean.getCompleto().equals(ConstantReportes.COMPLETO)) {
                        sumCompletoSatelital += bean.getCompleto().length();
                    }
                    if (bean.getConexion().equals(ConstantReportes.SATELITAL) && bean.getCompleto().equals(ConstantReportes.PENDIENTE)) {
                        SumPendienteSatelital += bean.getCompleto().length();
                    }
                }

                SumTotal = Integer.toString(listReport.size());
                SumTotCobre = sumCompletoCobre + SumPendienteCobre;
                SumTotSat = sumCompletoSatelital + SumPendienteSatelital;
                SumTotCompletos = sumCompletoCobre + sumCompletoSatelital;
                SumTotPendiente = SumPendienteCobre + SumPendienteSatelital;

                StrSumCompletoCobre = Integer.toString(sumCompletoCobre);
                StrSumCompletoSatelital = Integer.toString(sumCompletoSatelital);
                StrSumPendienteCobre = Integer.toString(SumPendienteCobre);
                StrSumPendienteSatelital = Integer.toString(SumPendienteSatelital);

                StrSumTotCobre = Integer.toString(SumTotCobre);
                StrSumTotSat = Integer.toString(SumTotSat);
                StrSumTotCompletos = Integer.toString(SumTotCompletos);
                StrSumTotPendiente = Integer.toString(SumTotPendiente);

                parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);
                parametros.put("tituloSecundario", ConstantReportes.nameTituloPuestaCeroCentroComputo);
                parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
                parametros.put("version", DeclaracionComun.gstrVersionSuite);
                parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
                parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
                parametros.put("reporte", ConstantReportes.nameReporteMesajeTransmitidos);
                parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
                parametros.put("mensajePC", DscTipo);
                parametros.put("conexionPC", DscConexion);
                parametros.put("estadoPC", DescEstado);
                parametros.put("nombreCC", DscCC);
                parametros.put("url_imagen", imagen);
                parametros.put("pendienteC", StrSumPendienteCobre);
                parametros.put("PendienteS", StrSumPendienteSatelital);
                parametros.put("pendienteT", StrSumTotPendiente);
                parametros.put("completaC", StrSumCompletoCobre);
                parametros.put("completaS", StrSumCompletoSatelital);
                parametros.put("completaT", StrSumTotCompletos);
                parametros.put("totalC", StrSumTotCobre);
                parametros.put("totalS", StrSumTotSat);
                parametros.put("totalT", SumTotal);

                StackPane sp = jp.reportShow(parametros, listReport, file, null);
                AppController appController = new AppController();
                String title = ConstantReportes.nameTituloPuestasaCero;
                appController.subBarraStageReport(mainStage, sp, title, 2);
                cmdBotones_Click(3);
            }

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
            e.printStackTrace();
        }

    }

    @FXML
    private void btnSalirOnAction() {
        try {
            stage.close();
            tablaPrincipal.getItems().clear();
            cmbEstado.getItems().clear();
            cmbCentroComp.getItems().clear();
            cmbConexion.getItems().clear();
            cmbTiposcc.getItems().clear();
            cmbMensaje.getItems().clear();
            limpiador();
            if (timer != null) {
                System.out.println("Cancelando el timer[1]");
                timer.cancel();
                timer.purge();
            }
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    private void actionCleaner_onAction(ActionEvent event) {
        limpiador();
    }

    public void ejecutarTarea(String minutes) {
        try {
            if (timer != null) {
                System.out.println("Cancelando el timer[1]");
                timer.cancel();
                timer.purge();
            }
        } catch (Exception ex) {
            Logger.getLogger(MensajeTransmitido.class.getName()).log(Level.SEVERE, null, ex);
        }
        ejecutarTime(Integer.parseInt(minutes) * 60000);
    }

    private void ejecutarTime(Integer period) {
        System.out.println("periodo :" + period);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Current time [BEFORE]:" + GregorianCalendar.getInstance().getTime());
                    Platform.runLater(() -> {
                        btnActualizarOnAction();
                    });
                } catch (Exception e) {
                    Logger.getLogger(MensajeTransmitido.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }, period, period);
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Mensajes Transmitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Mensajes Transmitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Mensajes Transmitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Mensajes Transmitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
        }
    }

    public AnchorPane getView() {
        return anchorPane;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
