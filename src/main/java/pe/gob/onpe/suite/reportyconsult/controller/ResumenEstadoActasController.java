/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatResumenEstadoActas;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.RepConsuControllerFactory;
import pe.gob.onpe.suite.reportyconsult.service.RcResumenEstadosActasService;
import pe.gob.onpe.suite.reportyconsult.thread.ReportThread;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ResumenEstadoActasController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage;
    private Scene scene;
    private Stage stage;
    /*Parametros de los calculos generales*/
    Integer totalAprocesar = 0;
    Integer totalPendientes = 0;
    Integer totalProcesadas = 0;
    Integer totalEnvioResolver = 0;
    Integer totalEnvioResueltas = 0;
    Integer totalGeneral = 0;
    boolean consultSelected = false;

    private ObservableList<CatResumenEstadoActas> tableViewConsultaData = FXCollections.observableArrayList();
    List<CatResumenEstadoActas> listResumenActas = new ArrayList<>();
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    public ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    ApplicationContext context;

    CriterioSeleccionController critSelecCtrl;

    Thread newThread;

    ReportThread reportThread;

    @Autowired()
    RcResumenEstadosActasService rcResumenEstadosActasService;

    @Autowired
    IComunService comunService;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btnConsultar;
    @FXML
    private Button buttonManual;

    @FXML
    private TextField txtMinutes;

    @FXML
    private AnchorPane paneSelection;

    @FXML
    private TableView tableViewConsulta;
    @FXML
    private TableColumn columnId;
    @FXML
    private TableColumn columnTipoEleccion;
    @FXML
    private TableColumn columnActasProcesar;
    @FXML
    private TableColumn columnActasPendientes;
    @FXML
    private TableColumn columnActasProcesadas;
    @FXML
    private TableColumn columnActasEnvioResolver;
    @FXML
    private TableColumn columnActasEnvioResueltas;
    @FXML
    private TableColumn columnActasTotal;
    @FXML
    private TextField txtTotalAProcesar;
    @FXML
    private TextField txtTotalPendientes;
    @FXML
    private TextField txtTotalProcesadas;
    @FXML
    private TextField txtTotalEnvioResolver;
    @FXML
    private TextField txtTotalEnvioResueltas;
    @FXML
    private TextField txtTotalGeneral;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void init() {
        stage = new Stage();
        stage.initOwner(mainStage);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.sizeToScene();

        AnchorPane anchorPaneAux = new AnchorPane();
        anchorPaneAux.setId("AnchorPane");
        anchorPaneAux.getStyleClass().add("bodyComun");
        anchorPaneAux.getChildren().addAll(this.getView());

        scene = new Scene(anchorPaneAux);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

        stage.setScene(scene);
        stage.show();
        subBarraStage(stage, ConstantReportes.nameTituloResumenEstadoActas, 2);

        try {
            /*Carga de los Filtros de Busqueda*/
            //critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            reportThread = context.getBean(ReportThread.class);
            txtMinutes.setText("5");
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setTipoModulo(ConstantReportes.RESUMEN_ESTADO_ACTAS);
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendiente", "Recibido"));
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.cargarCombos();

            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), 500, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), 500, 0, 1},});
            critSelecCtrl.dibujarCombos(listaOpciones);
            critSelecCtrl.setDefaultStyleCss();
            paneSelection.getChildren().add(critSelecCtrl);
            cargarColumnasTabla();
            filtroKeyEvent();
            cargarCeroTotales(listResumenActas);
            cmdBotones_Click(0);
        } catch (Exception ex) {
            System.out.println("Excepcion General " + ex);
            ex.printStackTrace();
        }

    }

    @FXML
    private void btnConsultarOnAction() {
        System.out.println(" Fecha de la consulta :" + new Date());
        listResumenActas.clear();
        tableViewConsultaData.removeAll(tableViewConsultaData);
        ParametrosInformacionOficial paramInfOfi = getParamInformacionOficial();
        listResumenActas = rcResumenEstadosActasService.getResumenEstadosActas(paramInfOfi.getCodComputo(), paramInfOfi.getCodOdpe(), "E");
        if (!(listResumenActas.size() > 0)) {
            mostrarMensajeRPT("RESUMEN ESTADO DE ACTA", "No existe informacion para la consulta realizada.");
            return;
        }
        cargarCeroTotales(listResumenActas);
        tableViewConsulta.setItems(cargarDatosTabla(listResumenActas));
        if (!consultSelected) {
            reportThread.setTime(Long.parseLong(txtMinutes.getText().toString()) * 60000);
            reportThread.setButton(btnConsultar);
            newThread = new Thread(reportThread);
            newThread.start();
        }
        consultSelected = true;
        cmdBotones_Click(1);

    }

    @FXML
    private void btnImprimirOnAction() {

        try {
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantReportes.pathReportConsult + ConstantReportes.nameReporteResumenEstadoActas + ".jrxml");
            String title = ConstantReportes.nameTituloResumenDeActas;
            Map<String, Object> parametros = new HashMap<>();
            cargaParametros(parametros);

            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listResumenActas, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
        }

    }

    @FXML
    private void btnSalirOnAction() throws InterruptedException {
        listResumenActas.clear();
        tableViewConsulta.getItems().removeAll(tableViewConsultaData);
        if (newThread != null) {
            newThread.interrupt();
        }
        stage.close();
        cmdBotones_Click(3);
    }

    @FXML
    private void buttonManualOnAction() {
        btnConsultarOnAction();
    }

    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            System.out.println("MINUTOS INGRESADOS : " + txtMinutes.getText());
            if (txtMinutes.getText().equals(ConstantReportes.VACIO) || txtMinutes.getText().equals(ConstantReportes.VALORCERO)) {
                txtMinutes.setText(ConstantReportes.TIMEDEFAULT);
            } else {
                if (consultSelected) {
                    System.out.println(" Fecha de inicio del Hilo :" + new Date());
                    if (!newThread.isInterrupted()) {
                        newThread.interrupt();
                        context = new AnnotationConfigApplicationContext(RepConsuControllerFactory.class);
                        reportThread = context.getBean(ReportThread.class);
                        reportThread.setTime(Long.parseLong(txtMinutes.getText().toString()) * 60000);
                        reportThread.setButton(btnConsultar);
                        newThread = new Thread(reportThread);
                        newThread.start();
                    } else {
                        System.out.println("Hilo interrumpido");
                    }
                } else {
                    System.out.println("No se ha realizado ninguna consulta");
                }
            }
        }
    }

    private void filtroKeyEvent() {

        txtMinutes.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                if (!ConstantReportes.NUMBER.contains(event.getCharacter())) {
                    event.consume();
                }
                if (txtMinutes.getText().length() >= ConstantReportes.MAXLENGTH) {
                    event.consume();
                }
            }
        });
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

    private ObservableList<CatResumenEstadoActas> cargarDatosTabla(List<CatResumenEstadoActas> listResumenActas) {

        if (listResumenActas != null) {
            for (CatResumenEstadoActas beanIterate : listResumenActas) {
                tableViewConsultaData.add(beanIterate);
            }
        } else {
            System.out.println("La lista para llenar la tabla es nula");
        }

        return tableViewConsultaData;
    }

    public void cargarColumnasTabla() {

        columnId.setCellValueFactory(new PropertyValueFactory("tipoEleccion"));
        columnTipoEleccion.setCellValueFactory(new PropertyValueFactory("nombreCortoEleccion"));
        columnActasProcesar.setCellValueFactory(new PropertyValueFactory("procesar"));
        columnActasPendientes.setCellValueFactory(new PropertyValueFactory("pendientes"));
        columnActasProcesadas.setCellValueFactory(new PropertyValueFactory("procesadas"));
        columnActasEnvioResolver.setCellValueFactory(new PropertyValueFactory("observadas"));
        columnActasEnvioResueltas.setCellValueFactory(new PropertyValueFactory("resueltas"));
        columnActasTotal.setCellValueFactory(new PropertyValueFactory("totalObsResult"));

    }

    private ParametrosInformacionOficial getParamInformacionOficial() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();

        String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
        String codCentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();

        System.out.println("ODPE :" + codOdpe);
        System.out.println("CENTRO DE COMPUTO :" + codCentroComputo);

        paramIO.setCodOdpe(codOdpe.substring(0, ConstantReportes.LONGITUD_COD_ODP));
        paramIO.setCodComputo(codCentroComputo.substring(0, ConstantReportes.LONGITUD_CENTROCOMPUTO));

        return paramIO;

    }

    private void cargarCeroTotales(List<CatResumenEstadoActas> listResumenActas) {
        totalAprocesar = 0;
        totalPendientes = 0;
        totalProcesadas = 0;
        totalEnvioResolver = 0;
        totalEnvioResueltas = 0;
        totalGeneral = 0;
        if (listResumenActas != null && listResumenActas.size() > 0) {
            System.out.println("Verificacion que la lista no es nula");
            for (CatResumenEstadoActas beanIterator : listResumenActas) {
                totalAprocesar = totalAprocesar + beanIterator.getProcesar();
                totalPendientes = totalPendientes + beanIterator.getPendientes();
                totalProcesadas = totalProcesadas + beanIterator.getProcesadas();
                totalEnvioResolver = totalEnvioResolver + beanIterator.getObservadas();
                totalEnvioResueltas = totalEnvioResueltas + beanIterator.getResueltas();
                totalGeneral = totalGeneral + beanIterator.getTotalObsResult();
            }
        }
        txtTotalAProcesar.setText(totalAprocesar.toString());
        txtTotalPendientes.setText(totalPendientes.toString());
        txtTotalProcesadas.setText(totalProcesadas.toString());
        txtTotalEnvioResolver.setText(totalEnvioResolver.toString());
        txtTotalEnvioResueltas.setText(totalEnvioResueltas.toString());
        txtTotalGeneral.setText(totalGeneral.toString());

    }

    private void cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {

            String descOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe();
            String descCompu = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloResumenDeActas);

            parametros.put("descOdpe", descOdpe);
            parametros.put("descCompu", descCompu);

            parametros.put("totalAprocesar", totalAprocesar);
            parametros.put("totalPendientes", totalPendientes);
            parametros.put("totalProcesadas", totalProcesadas);
            parametros.put("totalEnvioResolver", totalEnvioResolver);
            parametros.put("totalEnvioResueltas", totalEnvioResueltas);
            parametros.put("totalGeneral", totalGeneral);

            if (!DeclaracionComun.gstrFechaEleccion.equals("")) {
                Date dateEleccion = formatter.parse(DeclaracionComun.gstrFechaEleccion);
                if ((new Date()).before(dateEleccion)) {
                    parametros.put("sinvaloroficial", ConstantCommon.rptSinValorOficial);
                } else {
                    parametros.put("sinvaloroficial", "");
                }
            } else {
                System.out.println("La Fecha de eleccion no ha sido inicializada");
                parametros.put("sinvaloroficial", ConstantCommon.rptSinValorOficial);
            }
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("reporte", ConstantReportes.nameReporteMonitoreoInformacion);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
            ex.printStackTrace();

        }

    }

    public void startScheduledExecutor(int timeload) {

        try {
            scheduler.scheduleAtFixedRate(
                    new Runnable() {

                        int counter = 0;

                        @Override
                        public void run() {

                            counter++;
                            System.out.println("COUNTER SECONDS :" + counter);
                            if (counter <= timeload) {
                                Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {

                                        while (counter == timeload) {
                                            btnConsultarOnAction();
                                            counter = 0;
                                        }
                                    }
                                });
                            } else {
                                scheduler.shutdown();
                            }
                        }
                    }, 0, timeload, TimeUnit.MINUTES
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resumen Total por Centro de computo- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
        }
    }

    public void limpiarTabla() {
        listResumenActas.clear();
        tableViewConsultaData.removeAll(tableViewConsultaData);
        cargarCeroTotales(listResumenActas);

    }

    public Thread getNewThread() {
        return newThread;
    }

    public void setNewThread(Thread newThread) {
        this.newThread = newThread;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
