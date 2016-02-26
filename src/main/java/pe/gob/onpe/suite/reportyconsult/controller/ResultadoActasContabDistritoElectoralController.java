/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatResultActasContabDistritoElectoral;
import pe.gob.onpe.suite.domain.CatResultadoActasContabilizadas;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasDEService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 *
 * @author kleon
 */
public class ResultadoActasContabDistritoElectoralController extends AppController implements Initializable {

    private Stage mainStage;
    private Stage stageMessageBox;
    private Scene scene;
    private Stage stage;

    private String[][] arrayCongresales = new String[2][38];
    private Float Ncandidatos = Float.parseFloat("0");
    GridPane gridpaneGeneral;

    private List<CatResultActasContabDistritoElectoral> listActasAvance;

    String msgError = "";

    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPaneTable;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public Pane panelSeleccion;

    //Labels
    @FXML
    private Label lblPorcActasProcesadas;
    @FXML
    private Label lblActasProcesadas;
    @FXML
    private Label lblVotosInpug;
    @FXML
    private Label lblErrorMaterial;
    @FXML
    private Label lblLegitibilidad;
    @FXML
    private Label lblIncompleta;
    @FXML
    private Label lblSolicitudNulidad;
    @FXML
    private Label lblPorcPorProcesar;
    @FXML
    private Label lblActasContbNorm;
    @FXML
    private Label lblActasSinDatos;
    @FXML
    private Label lblActasExtraviadas;
    @FXML
    private Label lblActasSiniestradas;
    @FXML
    private Label lblSinfFirmas;
    @FXML
    private Label lblConMasObservacion;
    @FXML
    private Label lblContabAnuladas;
    @FXML
    private Label lblActasMesasNoInst;
    @FXML
    private Label lblActasPorProcesar;
    @FXML
    private Label lblMesasAInstalar;
    @FXML
    private Label lblMesasInstaldas;
    @FXML
    private Label lblMesasNoInstaladas;
    @FXML
    private Label lblMesasPorInformar;
    @FXML
    private Label lblMesasAgrupadas;
    @FXML
    private Label lblGruposVotacion;

    @FXML
    private Tab tabResultados;
    @FXML
    private TabPane tabPanePrincipal;

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
        subBarraStage(stage, ConstantReportes.nameTituloResultadoActasContabDE, 2);
        try {
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            panelSeleccion.getChildren().clear();
            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowTipoConsulta(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.setShowDistritoElectoral(Boolean.TRUE);
            critSelecCtrl.setShowtextFieldAvance(Boolean.TRUE);
            critSelecCtrl.setShowEstado(Boolean.TRUE);
            critSelecCtrl.setTipoModulo(ConstantReportes.RESULTADO_ACTASCONTAB_DISTRITOELECT);
            critSelecCtrl.setDefaultStyleCss();
            critSelecCtrl.cargarCombos();
            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},
                new Object[]{"\t", critSelecCtrl.getTextFieldAvance(), null, 2, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 1},
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 2, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOREPORTE, critSelecCtrl.getCmbTipoConsulta(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), 200, 0, 3}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            panelSeleccion.getChildren().add(critSelecCtrl);
            limpiarEsquemaRegistro("10");
            cmdBotones_Click(0);

        } catch (Exception ex) {
            System.out.println("[Init -ResumenResultadoActasContabilizadasController] Excepcion General :" + ex);
        }
    }

    @FXML
    private void btnConsultarOnAction() {
        /*Validando los filtros seleccionados*/
        ParametrosInformacionOficial paramInfo = getParametrosResultActasContab();

        tabPanePrincipal.getSelectionModel().select(tabResultados); // Seleccionamos el tab de resultados
        /*Obteniendo el tipo de consultas*/
        String tipoConsulta = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem();

        procesarConsulta(tipoConsulta, paramInfo);
    }

    @FXML
    private void btnImprimirOnAction() {
        /*Validando los filtros seleccionados*/
        ParametrosInformacionOficial paramInfo = getParametrosResultActasContab();
        if (listActasAvance == null) {
            String tipoConsulta = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem();
            procesarConsulta(tipoConsulta, paramInfo);
        } else {
            System.out.println("La lista a imprimir no es nula");
        }
        String title = ConstantReportes.nameResultActasContab;
        String nameReport = getNameReportByNcandidatos(Ncandidatos);
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
        /*Cargando los parametros del reporte*/
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("reporte", nameReport);
        parametros.put("tipoReporte", "1");
        parametros.put("TituloRep", String.format(ConstantReportes.nameResultActasContabDE, obtenerPorcCompDistritoElectoral(paramInfo.getTipoElec(), paramInfo.getCodDistElectoral())));
        cargaParametros(parametros);

        imprimirReporte(listActasAvance, parametros, file, title);
        cmdBotones_Click(2);
    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
        cmdBotones_Click(3);
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

    public void limpiarEsquemaRegistro(String tipoEleccion) {
        anchorPaneTable.getChildren().clear();
        limpiarResumenActas();
        arrayCongresales = new String[2][38];
        anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(38, 2));
    }

    private void limpiarResumenActas() {
        lblActasProcesadas.setText("0");
        lblPorcActasProcesadas.setText("(0.000 %)");
        lblPorcPorProcesar.setText("(0.000 %)");
        lblActasContbNorm.setText("0");
        lblVotosInpug.setText("0");
        lblErrorMaterial.setText("0");
        lblLegitibilidad.setText("0");
        lblIncompleta.setText("0");
        lblActasExtraviadas.setText("0");
        lblActasSiniestradas.setText("0");
        lblSinfFirmas.setText("0");
        lblActasSinDatos.setText("0");
        lblSolicitudNulidad.setText("0");
        lblConMasObservacion.setText("0");
        lblContabAnuladas.setText("0");
        lblActasMesasNoInst.setText("0");
        lblActasPorProcesar.setText("0");
        lblMesasAInstalar.setText("0");
        lblMesasInstaldas.setText("0");
        lblMesasNoInstaladas.setText("0");
        lblMesasPorInformar.setText("0");
        lblMesasAgrupadas.setText("0");
        lblGruposVotacion.setText("0");
        lblPorcActasProcesadas.setStyle("-fx-font-weight: bold;");
        lblPorcPorProcesar.setStyle("-fx-font-weight: bold;");
        lblMesasAInstalar.setStyle("-fx-font-weight: bold;");
        lblActasPorProcesar.setStyle("-fx-font-weight: bold;");
        critSelecCtrl.cargarTextfieldAvance("AL 0.000 %");

    }

    private StackPane dibujarGridpaneCongresales(Integer columns, Integer rows) {
        ScrollPane scrollPaneCongres = new ScrollPane();
        StackPane scrollContainer = new StackPane(scrollPaneCongres);
        scrollContainer.setLayoutX(10);
        scrollContainer.setLayoutY(10);
        scrollPaneCongres.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        scrollPaneCongres.setPrefSize(1020, 385);
        scrollPaneCongres.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        gridpaneGeneral = new GridPane();
        scrollPaneCongres.setContent(gridpaneGeneral);
        for (int j = 0; j <= rows; j++) { //FILA
            for (int i = 0; i < columns; i++) { //COLUMNA
                Label labelNew = new Label("");
                StackPane pane = new StackPane();
                pane.getStyleClass().add("pane_color_grid");
                pane.setPrefHeight(50);
                labelNew.setPrefHeight(49);
                if (j == 0) {
                    if (i == 0) {
                        pane.setPrefWidth(501);
                        labelNew.getStyleClass().add("lbl_color_blue");
                        labelNew.setPrefWidth(500);
                        labelNew.setAlignment(Pos.CENTER);
                        labelNew.setText("Organización Política");
                        pane.getChildren().add(labelNew);
                        gridpaneGeneral.add(pane, i, j);
                    } else {
                        pane.setPrefWidth(90);
                        labelNew.setPrefWidth(89);
                        if (i == 1) {
                            labelNew.getStyleClass().add("lbl_color_blue");
                            labelNew.setAlignment(Pos.CENTER);
                            labelNew.setText("Total Votos");
                        } else {
                            labelNew.setAlignment(Pos.CENTER);
                            labelNew.getStyleClass().add("lbl_color_blue");
                            labelNew.setText((i - 1) + "");
                        }
                        pane.getChildren().add(labelNew);
                        gridpaneGeneral.add(pane, i, j);
                    }
                } else {
                    labelNew = new Label(arrayCongresales[j - 1][i] == null ? "" : arrayCongresales[j - 1][i]);
                    labelNew.getStyleClass().add("pane_color_label_grid");
                    if (i == 0) {
                        pane.setPrefWidth(501);
                        labelNew.setPrefWidth(500);
                    } else {
                        labelNew.setAlignment(Pos.CENTER);
                        pane.setPrefWidth(90);
                        labelNew.setPrefWidth(89);
                    }
                    pane.getChildren().add(labelNew);
                    gridpaneGeneral.add(pane, i, j);
                }

            }

        }

        return scrollContainer;
    }

    private ParametrosInformacionOficial getParametrosResultActasContab() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();

        String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
        String codCentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistElectoral = "";

        if (!critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getDistElec().equals(ConstantReportes.SELECCION_COMBO_TODOS)
                && critSelecCtrl.getCmbTipoElec().getValue().getTipoElec().equals(ConstantReportes.TIPOELECCION_CONGRESAL)) {
            codDistElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral().substring(0, ConstantReportes.LONGITUD_UBIGEO);
        } else if (codTipoEleccion.equals(ConstantReportes.TIPOELECCION_CONGRESAL)) {
            validador(2);
            return null;
        }

        paramIO.setCodOdpe(codOdpe.equals(ConstantReportes.TODOS_ODPE_ENUM) ? "" : codOdpe);
        paramIO.setCodComputo(codCentroComputo.equals(ConstantReportes.TODOS_CCOM_ENUM) ? "" : codCentroComputo);
        paramIO.setTipoElec(codTipoEleccion);
        paramIO.setCodDistElectoral(codDistElectoral);
        return paramIO;

    }

    private void validador(int nro) {
        switch (nro) {
            case 0:
                msgError = "No existe informacion para la consulta realizada";
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada");
                break;
            case 1:
                msgError = "Debe Consultar Antes de imprimir, No Existen Datos para Mostrar.";
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Consultar Antes de imprimir, No Existen Datos para Mostrar.");
                break;
            case 2:
                msgError = "Debe Seleccionar un Distrito Electoral";
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Seleccionar un Distrito Electoral");
                break;
        }
    }

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(3000)
    );

    private void procesarConsulta(String tipoConsulta, ParametrosInformacionOficial paramInfo) {
        limpiarEsquemaRegistro(ConstantReportes.TIPOELECCION_CONGRESAL);
        switch (tipoConsulta) {
            case "Resumido":

                String nivelAgrup = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem().substring(0, 1);
                System.out.println("Nivel de Agrupacion :" + nivelAgrup);

                /*Relizando la consulta*/
                listActasAvance
                        = rcResultadoActasContabilizadasDEService.getActasContabilizadasDE(paramInfo.getCodOdpe(), paramInfo.getCodComputo(),
                                paramInfo.getTipoElec(), paramInfo.getCodDistElectoral(), (nivelAgrup.equals("R") ? "2" : "1"));

                System.out.println("Cantidad de archivos : " + listActasAvance.size());
                /*Mostramos los datos en la pestaña de resumen de Actas*/
                cargarResumenActas(listActasAvance);
                /*Cargamos la data segun sea la eleccion*/
                switch (paramInfo.getTipoElec()) {
                    case "10":
                        break;
                    case "11":
                        calculoVotosCongresalesParlamento(listActasAvance, 38);//Caragamos los arrays con los resultados obtenidos
                        anchorPaneTable.getChildren().remove(gridpaneGeneral);
                        anchorPaneTable.getChildren().add(dibujarGridpaneCongresales((Math.round(Ncandidatos) + 2), listActasAvance.size()));
                        break;
                    case "12":
                        break;

                }
                /*Registrando Auditoria*/
                cmdBotones_Click(1);

                break;
            case "Detallado":
                //No se puede obtener reporte detallado, realize la consulta por distrito electoral

                String title = ConstantCommon.titleValidacion;
                String message = "En esta opcion usted no puede obtener un reporte detallado \nSeleccione un Distrito electoral y realize la consulta \nde forma resumida.";
                EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                    stageMessageBox.close();
                };
                stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, message, null, null, null, eventOK);
                cmdBotones_Click(1);
                break;

            default:
                break;
        }
    }

    private void cargarResumenActas(List<CatResultActasContabDistritoElectoral> listActasAvance) {
        if (listActasAvance.size() > 0) {
            DecimalFormat df = new DecimalFormat("#.00"); // formato de texto para los decimales

            CatResultActasContabDistritoElectoral beanAvanceActas = listActasAvance.get(0);
            lblActasProcesadas.setText(beanAvanceActas.getActasProcesadas().toString());
            lblActasProcesadas.setStyle("-fx-font-weight: bold;");

            if (beanAvanceActas.getAinstalar() > 0) {
                Double lblPorcActasProc = (beanAvanceActas.getActasProcesadas() * 100.0) / beanAvanceActas.getAinstalar();

                Double lblPorcActasXProc = (beanAvanceActas.getPorProcesar() * 100.0) / beanAvanceActas.getAinstalar();

                Double lblPorcentaje = (((beanAvanceActas.getContabNormal()
                        + beanAvanceActas.getContabAnuladas() + beanAvanceActas.getMesasNoInstaladas()) * 100.0)
                        / beanAvanceActas.getAinstalar());
                critSelecCtrl.cargarTextfieldAvance("AL " + round(lblPorcentaje, 3) + " %");
                Double PorcActasProc = ((beanAvanceActas.getAinstalar() - beanAvanceActas.getPorProcesar()) * 100.0) / beanAvanceActas.getAinstalar();
                Double PorcActasXProc = (beanAvanceActas.getPorProcesar() * 100.0) / beanAvanceActas.getAinstalar();
                Double Porcentaje = ((beanAvanceActas.getContabNormal() + beanAvanceActas.getContabInpugnadas() + beanAvanceActas.getContabAnuladas() + beanAvanceActas.getMesasNoInstaladas()) * 100.0) / beanAvanceActas.getAinstalar();

                Double PorcActasCompletad = Porcentaje;
                /*seteando los % a los lbl*/
                lblPorcActasProcesadas.setText("(" + String.valueOf(round(lblPorcActasProc, 3)) + "%)");
                lblPorcActasProcesadas.setStyle("-fx-font-weight: bold;");
                lblPorcPorProcesar.setText("(" + String.valueOf(round(lblPorcActasXProc, 3)) + "%)");
                lblPorcPorProcesar.setStyle("-fx-font-weight: bold;");

            }

            lblActasContbNorm.setText(beanAvanceActas.getContabNormal().toString());
            lblVotosInpug.setText(beanAvanceActas.getContabInpugnadas().toString());
            lblErrorMaterial.setText(beanAvanceActas.getErrorMaterial());
            lblLegitibilidad.setText(beanAvanceActas.getIlegible());
            lblIncompleta.setText(beanAvanceActas.getIncompleta());
            lblActasExtraviadas.setText(beanAvanceActas.getActExt());
            lblActasSiniestradas.setText(beanAvanceActas.getActSin());
            lblSinfFirmas.setText(beanAvanceActas.getSinFirma());
            lblActasSinDatos.setText(beanAvanceActas.getSinDatos());
            lblSolicitudNulidad.setText(beanAvanceActas.getSolicitudNulidad());
            lblConMasObservacion.setText(beanAvanceActas.getOtrasObserv());
            lblContabAnuladas.setText(beanAvanceActas.getContabAnuladas().toString());
            lblActasMesasNoInst.setText(beanAvanceActas.getMesasNoInstaladas().toString());
            lblActasPorProcesar.setText(beanAvanceActas.getPorProcesar().toString());
            lblActasPorProcesar.setStyle("-fx-font-weight: bold;");
            lblMesasAInstalar.setText(beanAvanceActas.getAinstalar().toString());
            lblMesasAInstalar.setStyle("-fx-font-weight: bold;");
            lblMesasInstaldas.setText(beanAvanceActas.getMesasInstaladas().toString());
            lblMesasNoInstaladas.setText(beanAvanceActas.getNoInstaladas());
            lblMesasPorInformar.setText(beanAvanceActas.getPorProcesar().toString());
            lblMesasAgrupadas.setText(beanAvanceActas.getFusionadas());
            lblGruposVotacion.setText(beanAvanceActas.getMesasHabiles());
        } else {
            System.out.println("No existen registros para la consulta");
        }
    }

    private void calculoVotosCongresalesParlamento(List<CatResultActasContabDistritoElectoral> listActasAvance, Integer columns) {
        if (listActasAvance != null) {
            if (listActasAvance.size() > 0) {
                try {
                    arrayCongresales = new String[listActasAvance.size()][columns];
                    int rows = 0;
                    for (CatResultActasContabDistritoElectoral iteratorList : listActasAvance) {
                        arrayCongresales[rows][0] = iteratorList.getDesAgrupacion();
                        arrayCongresales[rows][1] = iteratorList.getTotalVotos().toString();
                        for (int i = 2; i < columns; i++) {
                            String nomSetter = "getNumVotos" + (i - 1);
                            Class noparams[] = {};
                            Method setter = iteratorList.getClass().getMethod(nomSetter, noparams);
                            arrayCongresales[rows][i] = ((Integer) setter.invoke(iteratorList, null)).toString();
                        }
                        rows++;
                    }
                } catch (Exception ex) {
                    System.out.println("[calculoVotosCongresales] Excepcion General : " + ex);
                }
            }
        }
    }

    /*Metodo de redondeo de decimales*/
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void imprimirReporte(List<CatResultActasContabDistritoElectoral> listResumActascontbDE, Map<String, Object> parametros, InputStream file, String title) {
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

    public String getNameReportByNcandidatos(Float Ncandidatos) {
        switch (Math.round(Ncandidatos)) {
            case 1:
                return ConstantReportes.nameReporteResultadoActasContabDE1;
            case 2:
                return ConstantReportes.nameReporteResultadoActasContabDE2;
            case 3:
                return ConstantReportes.nameReporteResultadoActasContabDE3;
            case 4:
                return ConstantReportes.nameReporteResultadoActasContabDE4;
            case 5:
                return ConstantReportes.nameReporteResultadoActasContabDE5;
            case 6:
                return ConstantReportes.nameReporteResultadoActasContabDE6;
            case 7:
                return ConstantReportes.nameReporteResultadoActasContabDE7;
            case 9:
                return ConstantReportes.nameReporteResultadoActasContabDE9;
            default:
                return ConstantReportes.nameReporteResultadoActasContabDE;
        }
    }

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales + "\n" + critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());

            parametros.put("tituloEleccionSimple", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("TituloEleccionCompleto", ConstantReportes.nameTituloMonitInfEleccionesGenerales);

            parametros.put("odpe", critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe());
            parametros.put("ccomputo", critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu());

            parametros.put("sinvaloroficial", validarInfOficial());
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

    /*validando la fecha de eleccion para el msj de Valor Oficial*/
    private String validarInfOficial() {
        String msjRetorno = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            if (!DeclaracionComun.gstrFechaEleccion.equals("")) {
                Date dateEleccion = formatter.parse(DeclaracionComun.gstrFechaEleccion);
                if ((new Date()).after(dateEleccion)) {
                    msjRetorno = ConstantCommon.rptSinValorOficial;
                }
            } else {
                System.out.println("La Fecha de eleccion no ha sido inicializada");
                msjRetorno = ConstantCommon.rptSinValorOficial;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msjRetorno = "";
        }
        return msjRetorno = "";
    }

    private String obtenerPorcCompDistritoElectoral(String codTipoEleccion, String codDistElectoral) {
        String porcentaje = "0.000 %";
        try {

            List<CatResultadoActasContabilizadas> listActas = rcResultadoActasContabilizadasDEService.getPorcActasComputadas(codTipoEleccion, codDistElectoral);
            if (listActas != null) {
                if (listActas.size() > 0) {
                    for (CatResultadoActasContabilizadas iteratorList : listActas) {
                        if (!iteratorList.getActasInstalar().equals(0)) {
                            porcentaje = String.format("%.3f", ((iteratorList.getActasComputadas() * 100.0) / iteratorList.getActasInstalar())) + " %";
                        }
                        break; //solo validamos la primera fila
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
            ex.printStackTrace();
        }
        return porcentaje;
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resultado de Actas Contabilizadas por Distrito electoral - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resultado de Actas Contabilizadas por Distrito electoral - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resultado de Actas Contabilizadas por Distrito electoral - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resultado de Actas Contabilizadas por Distrito electoral- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
        }
    }

    public void limpiarPantalla() {
        listActasAvance = null;
        limpiarResumenActas();
        arrayCongresales = new String[2][38];
        anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(38, 2));
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Float getNcandidatos() {
        return Ncandidatos;
    }

    public void setNcandidatos(Float Ncandidatos) {
        this.Ncandidatos = Ncandidatos;
    }

}
