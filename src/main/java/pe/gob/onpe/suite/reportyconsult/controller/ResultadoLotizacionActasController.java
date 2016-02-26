/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatLotesActas;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasDEService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 *
 * @author kleon
 */
public class ResultadoLotizacionActasController extends AppController implements Initializable {

    private Stage mainStage;
    private Stage stageMessageBox;
    private Scene scene;
    private Stage stage;

    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane panelSeleccion;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    @Autowired
    RcResultadoActasContabilizadasDEService rcResultadoActasContabilizadasDEService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        anchorPaneAux.getChildren().add(this.getView());

        scene = new Scene(anchorPaneAux);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

        stage.setScene(scene);
        stage.show();
        subBarraStage(stage, ConstantReportes.nameTituloResumenHistoricoActa, 2);

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
        beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
        BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
        beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

        panelSeleccion.getChildren().clear();
        critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
        critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
        critSelecCtrl.setShowTipoEleccion(true);
        critSelecCtrl.setShowCentroComputo(true);
        critSelecCtrl.setShowODPE(true);
        critSelecCtrl.setShowTipoLote(true);

        critSelecCtrl.setTipoModulo(ConstantReportes.RESULTADO_LOTIZACION_ACTAS);

        critSelecCtrl.setDefaultStyleCss();

        critSelecCtrl.cargarCombos();
//            Lista de los Filtros
        List<Object[]> listaOpciones = new ArrayList<>();
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 0}});
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 1}});
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 2}});
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_ENTRE_FECHAS, critSelecCtrl.getFirstCheckInDatePicker(), null, 0, 3},
            new Object[]{"", critSelecCtrl.getTextFieldTimeInit(), 80, 2, 3}
        });
        listaOpciones.add(new Object[]{
            new Object[]{"    ", critSelecCtrl.getLastCheckInDatePicker(), null, 0, 4},
            new Object[]{"", critSelecCtrl.getTextFieldTimeFin(), 80, 2, 4}
        });
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_TIPOLOTE, critSelecCtrl.getCmbTipoLote(), 300, 0, 5}});
        critSelecCtrl.dibujarCombos(listaOpciones);
        panelSeleccion.getChildren().add(critSelecCtrl);
        cmdBotones_Click(0);
    }

    @FXML
    private void btnImprimirOnAction() {
        //Consultando la ultima decha de la puesta a cero
        String fechaPuestacero = ultimaPuestaCero(DatosUsuario.getMstrCentroComputo());
        if (fechaPuestacero.equals("")) {
            String msg = "No se ha realizado la puesta a cero";
            mostrarMensaje(msg);
        } else {
            System.out.println("Formateando las fechas ingresadas");
            Calendar time = new GregorianCalendar();
            int second = time.get(Calendar.SECOND);
            LocalDate firstDate = critSelecCtrl.getFirstCheckInDatePicker().getValue();
            LocalDate lastDate = critSelecCtrl.getLastCheckInDatePicker().getValue();
            String[] arrayDateInicio = critSelecCtrl.getTextFieldTimeInit().getText().split(":");
            String[] arrayDateFin = critSelecCtrl.getTextFieldTimeFin().getText().split(":");
            String fechaInicio = firstDate.getYear() + formatoFecha(firstDate.getMonthValue(), 2) + formatoFecha(firstDate.getDayOfMonth(), 2)
                    + formatoFecha(Integer.parseInt(arrayDateInicio[0]), 2) + formatoFecha(Integer.parseInt(arrayDateInicio[1]), 2) + formatoFecha(second, 2);
            String fechaFin = lastDate.getYear() + formatoFecha(lastDate.getMonthValue(), 2) + formatoFecha(lastDate.getDayOfMonth(), 2)
                    + formatoFecha(Integer.parseInt(arrayDateFin[0]), 2) + formatoFecha(Integer.parseInt(arrayDateFin[1]), 2) + formatoFecha(second, 2);
            if (!enRango(fechaPuestacero, fechaInicio, fechaFin)) {
                String msg = "El rango de Fechas no coincide con la puesta a cero";
                mostrarMensaje(msg);
            } else {
                List<CatLotesActas> listLotesActas = procesarConsulta(getParamConsulta(), fechaInicio, fechaFin);
                if (listLotesActas != null) {
                    if (listLotesActas.size() > 0) {

                        String title = ConstantReportes.nameTituloReporteActasLotizadas;
                        String nameReport = getNamereport();
                        InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
                        /*Cargando los parametros del reporte*/
                        Map<String, Object> parametros = new HashMap<>();
                        parametros.put("TipoConsulta", critSelecCtrl.getCmbTipoLote().getValue().getDescLote());
                        
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        
                        String formatedfirstDate = outputFormat.format(java.sql.Date.valueOf(firstDate));
                        parametros.put("fechaInicio", formatedfirstDate + " " + arrayDateInicio[0] + ":" + arrayDateInicio[1]);
                        
                        String formatedlastDate = outputFormat.format(java.sql.Date.valueOf(lastDate));
                        parametros.put("fechaFin", formatedlastDate + " " + arrayDateFin[0] + ":" + arrayDateFin[1]);
                        
                        parametros.put("reporte", nameReport);
                        cargaParametros(parametros);
                        imprimirReporte(listLotesActas, parametros, file, title);
                        cmdBotones_Click(2);

                    } else {
                        mostrarMensaje(ConstantReportes.NO_EXISTE_INFORMACIONPARAMOSTRAR);
                    }
                } else {
                    mostrarMensaje(ConstantReportes.NO_EXISTE_INFORMACIONPARAMOSTRAR);
                }

            }
        }
    }

    @FXML
    private void btnSalirOnAction() {
        cmdBotones_Click(3);
        stage.close();
    }

    private String getNamereport() {
        String tipoLote = critSelecCtrl.getCmbTipoLote().getValue().getTipoLote();

        if (tipoLote.equals("AR") || tipoLote.equals("MN") || tipoLote.equals("AE")) {
            return ConstantReportes.nameReporteLotizacionActaAR;
        } else if (tipoLote.equals("DN") || tipoLote.equals("AD") || tipoLote.equals("DE")) {
            return ConstantReportes.nameReporteLotizacionActaDN;
        } else if (tipoLote.equals("ER")) {
            return ConstantReportes.nameReporteLotizacionActa;
        } else {
            return ConstantReportes.nameReporteLotizacionActa;
        }
    }

    private Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);

            parametros.put("nombreCorto", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("tituloRep", ConstantReportes.nameTituloReporteActasLotizadas);

            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
            return parametros;
        } catch (Exception ex) {
            Logger.getLogger(ResumenResultadoActasContabilizadasController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void imprimirReporte(List<CatLotesActas> listResumActascontbDE, Map<String, Object> parametros, InputStream file, String title) {
        try {
            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listResumActascontbDE, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
        } catch (Exception ex) {
            System.out.println("Excepcion general :" + ex);
            ex.printStackTrace();
        }
    }

    public void mostrarMensaje(String msg) {

        String title = ConstantCommon.titleValidacion;
        String message = msg;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, title, message, null, null, null, eventOK);

    }

    private String ultimaPuestaCero(String codCentroComputo) {
        String fechaRetorno = "";
        fechaRetorno = rcResultadoActasContabilizadasDEService.getUltimaFechaPuestaCero(codCentroComputo);
        fechaRetorno = (null == fechaRetorno ? "" : fechaRetorno);
        System.out.println("#fechaRetorno :" + fechaRetorno);
        return fechaRetorno;

    }

    private String formatoFecha(Integer Fecha, Integer length) {
        String strFecha = Fecha.toString();
        if (strFecha.length() < length) {
            return "0" + strFecha;
        } else {
            return strFecha;
        }
    }

    private boolean enRango(String fechaPuestacero, String fechaInicio, String fechaFin) {
        boolean benrango = false;
        Long longPuestaCero = Long.parseLong(fechaPuestacero);
        Long longFechaInicio = Long.parseLong(fechaInicio);
        Long longFechaFin = Long.parseLong(fechaFin);
        if (longPuestaCero >= longFechaInicio && longPuestaCero <= longFechaFin) {
            benrango = true;
        }
        return benrango;
    }

    private ParametrosInformacionOficial getParamConsulta() {
        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();
        String codeOdpe = critSelecCtrl.getCmbOdpes().getValue().getCodiOdpe();
        String codeCC = critSelecCtrl.getCmbCC().getValue().getCodigo();
        String codeTipoeleccion = critSelecCtrl.getCmbTipoElec().getValue().getTipoElec();
        String codeTipoLote = critSelecCtrl.getCmbTipoLote().getValue().getTipoLote();

        paramIO.setCodOdpe(codeOdpe);
        paramIO.setCodComputo(codeCC);
        paramIO.setTipoElec(codeTipoeleccion);
        paramIO.setTipoLote(codeTipoLote);

        return paramIO;
    }

    private List<CatLotesActas> procesarConsulta(ParametrosInformacionOficial parametros, String fechaInicio, String fechaFin) {
        List<CatLotesActas> listaLotesActas = null;
        listaLotesActas = rcResultadoActasContabilizadasDEService.getLotesActas(parametros, fechaInicio, fechaFin);
        System.out.println("Cantidad de registros obtenidos  :" + listaLotesActas.size());
        return listaLotesActas;
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resultado de Lotizacion de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resultado de Lotizacion de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resultado de Lotizacion de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resultado de Lotizacion de Actas- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
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
