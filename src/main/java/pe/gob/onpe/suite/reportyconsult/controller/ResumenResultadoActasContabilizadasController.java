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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatResultAvanceActasContabilizadas;
import pe.gob.onpe.suite.domain.CatResultAvanceActasContabilizadasData;
import pe.gob.onpe.suite.domain.CatResultadoActasContabilizadas;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author kleon
 */
public class ResumenResultadoActasContabilizadasController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage;
    private Stage stageMessageBox;
    private Scene scene;
    private Stage stage;
    private String tipoReporte = "00"; // 00 Resultado de actas computadas

    CriterioSeleccionController critSelecCtrl;

    private Float Ncandidatos = Float.parseFloat("0");

    private ObservableList<CatResultAvanceActasContabilizadasData> tableViewConsultaData = FXCollections.observableArrayList();
    TableView<CatResultAvanceActasContabilizadasData> tableViewPresident;
    private List<CatResultAvanceActasContabilizadasData> listResultActas = new ArrayList<>();
    private List<CatResultAvanceActasContabilizadas> listActasAvance;
    private String[][] arrayNulosBlancos = new String[4][4];
    private String[][] arrayCongresales = new String[2][38];

    String msgError = "";
    boolean consultaPresindenciales = false;
    boolean consultaCongresales = false;
    boolean consultaParlamento = false;
    boolean consultaPresindentDetallado = false;
    boolean consultaCongresalDetallado = false;
    boolean consultaParlamentDetallado = false;
    boolean consultaSeleccionImprimir = false;

    GridPane gridpaneTotalVotos;
    GridPane gridpaneGeneral;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    @Autowired
    RcResultadoActasContabilizadasService rcResultadoActasContabilizadasService;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane anchorPaneTable;
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
    private Label labelHidden;
    @FXML
    private Tab tabResultados;
    @FXML
    private TabPane tabPanePrincipal;
    /*Loading */

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
        anchorPaneAux.getChildren().add(this.getView());

        scene = new Scene(anchorPaneAux);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

        stage.setScene(scene);
        stage.show();
        if (getTipoReporte().equals("00")) {
            subBarraStage(stage, ConstantReportes.nameTituloResultadoActas, 2);
        } else if (getTipoReporte().equals("01")) {
            subBarraStage(stage, ConstantReportes.nameTituloResumenAvanceActas, 2);
        } else {
            subBarraStage(stage, ConstantReportes.nameTituloResumenAvanceActas, 2);
        }

        try {
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            panelSeleccion.getChildren().clear();
            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            critSelecCtrl.setLayoutY(5);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowTipoConsulta(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.setShowProvincia(Boolean.TRUE);
            critSelecCtrl.setShowDistrito(Boolean.TRUE);
            critSelecCtrl.setShowDepartamento(Boolean.TRUE);
            critSelecCtrl.setShowtextFieldAvance(Boolean.TRUE);
            //de prueba
            critSelecCtrl.setShowEstado(Boolean.TRUE);
            critSelecCtrl.setTipoModulo(ConstantReportes.RESULTADO_ACTAS_CONTABILIZADAS);
            critSelecCtrl.setDefaultStyleCss();

            critSelecCtrl.cargarCombos();
            //Lista de los parametros de Filtros
            critSelecCtrl.getCmbCC().setLayoutX(600);
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
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), null, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), null, 4, 3}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);
            panelSeleccion.getChildren().add(critSelecCtrl);
            limpiarEsquemaRegistro("10");

        } catch (Exception ex) {
            System.out.println("[Init -ResumenResultadoActasContabilizadasController] Excepcion General :" + ex);
        }

    }

    @FXML
    private void btnConsultarOnAction() {

        /*Validando los filtros seleccionados*/
        ParametrosInformacionOficial paramInfo = getParametrosResultActasContab();
        labelHidden.setVisible(false);
        if (paramInfo == null && (!msgError.equals(""))) {
            mostrarMensaje(msgError);
//            mostrarMensaje();
            return; //cancelamos la consulta
        }
        tabPanePrincipal.getSelectionModel().select(tabResultados); // Seleccionamos el tab de resultados
        /*Obteniendo el tipo de consultas*/
        String tipoConsulta = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem();

        procesarConsulta(tipoConsulta, paramInfo);

    }

    @FXML
    private void btnImprimirOnAction() {
        try {
            consultaSeleccionImprimir = true;
            boolean imprimir = false;
            /*Obteniendo el tipo de consultas*/
            String tipoConsulta = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem();
            ParametrosInformacionOficial paramInfo = getParametrosResultActasContab();

            if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec().equals(ConstantReportes.TIPOELECCION_PRESIDENCIAL)) {
                switch (tipoConsulta) {
                    case "Resumido":
                        if (!consultaPresindenciales) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        }
                        imprimir = true;
                        break;
                    case "Detallado":
                        if (!consultaPresindentDetallado) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        } else {
                            imprimir = true;
                        }
                        break;
                }
                if (imprimir && (listActasAvance != null && listActasAvance.size() > 0)) {
                    String title = ConstantReportes.nameResultActasContab;
                    InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                            ConstantReportes.pathReportConsult + ConstantReportes.nameReporteResultadoActas + ".jrxml");

                    /*Cargando los parametros del reporte*/
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("reporte", "RPT04020201");
                    parametros.put("tipoReporte", "2");
                    cargaParametros(parametros);

                    imprimirReporte(listActasAvance, parametros, file, title);
                }

            } else if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec().equals(ConstantReportes.TIPOELECCION_CONGRESAL)) {
                switch (tipoConsulta) {
                    case "Resumido":
                        if (!consultaCongresales) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        }
                        imprimir = true;
                        break;
                    case "Detallado":
                        if (!consultaCongresalDetallado) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        } else {
                            imprimir = true;
                        }
                        break;
                }
                if (imprimir && (listActasAvance != null && listActasAvance.size() > 0)) {
                    String title = ConstantReportes.nameResultActasContab;
                    String nameReport = getNameReportByNcandidatos(Ncandidatos);
                    InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
                    /*Cargando los parametros del reporte*/
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("reporte", nameReport);
                    parametros.put("tipoReporte", "2");
                    cargaParametros(parametros);

                    imprimirReporte(listActasAvance, parametros, file, title);
                }
            } else if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec().equals(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO)) {
                switch (tipoConsulta) {
                    case "Resumido":
                        if (!consultaParlamento) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        }
                        imprimir = true;
                        break;
                    case "Detallado":
                        if (!consultaParlamentDetallado) {
                            procesarConsulta(tipoConsulta, paramInfo);
                        } else {
                            imprimir = true;
                        }
                        break;
                }
                if (imprimir && (listActasAvance != null && listActasAvance.size() > 0)) {
                    String title = ConstantReportes.nameResultActasContab;
                    InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                            ConstantReportes.pathReportConsult + "RPT04040201" + ".jrxml");
                    /*Cargando los parametros del reporte*/
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("reporte", "RPT04040201");
                    parametros.put("tipoReporte", "2");
                    cargaParametros(parametros);

                    imprimirReporte(listActasAvance, parametros, file, title);
                }
            }

            /*Registrando Auditoria*/
            comunService.registrarAuditoria(mainStage, ConstantReportes.MSGAUDITORIA_IMPRIMIRREPORTE_RESULTACTASCONTAB, COD_MOD_REPCON);
            consultaSeleccionImprimir = false;
        } catch (Exception ex) {
            System.out.println("Escepcion General :");
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnSalirOnAction() {
        if (listActasAvance != null) {
            listActasAvance.clear();
        }
        listResultActas.clear();
        tableViewConsultaData.clear();
        stage.close();
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
        switch (tipoEleccion) {
            case "10":
                consultaPresindenciales = false;
                consultaPresindentDetallado = false;
                limpiarResumenActas();
                tableViewConsultaData.clear();
                arrayNulosBlancos = new String[4][4];
                anchorPaneTable.getChildren().add(dibujarTablaPresidVicepres());
                anchorPaneTable.getChildren().add(dibujarGridPanePresidVicepres());
                break;
            case "11":
                consultaCongresales = false;
                consultaCongresalDetallado = false;
                limpiarResumenActas();
                arrayCongresales = new String[2][38];
                anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(38, 2));
                break;
            case "12":
                consultaParlamento = false;
                consultaParlamentDetallado = false;
                limpiarResumenActas();
                arrayCongresales = new String[2][17];
                anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(17, 2));
                break;
        }

    }

    private TableView dibujarTablaPresidVicepres() {
        tableViewPresident = new TableView<>();
        tableViewPresident.setLayoutX(10);
        tableViewPresident.setLayoutY(10);
        tableViewPresident.setPrefWidth(1020);
        tableViewPresident.setPrefHeight(278);
        TableColumn<CatResultAvanceActasContabilizadasData, String> tableOrgPolitica = new TableColumn();
        TableColumn<CatResultAvanceActasContabilizadasData, String> tableCantVotos = new TableColumn();
        TableColumn<CatResultAvanceActasContabilizadasData, String> tablePorcVotosValidos = new TableColumn();
        TableColumn<CatResultAvanceActasContabilizadasData, String> tablePorcVotosEmitidos = new TableColumn();

        tableOrgPolitica.setText("Organización Política");
        tableOrgPolitica.setCellValueFactory(new PropertyValueFactory("orgPolitica"));
        tableOrgPolitica.setPrefWidth(500);
        tableOrgPolitica.setStyle("-fx-alignment: CENTER_LEFT;");

        tableCantVotos.setText("Cantidad de  Votos");
        tableCantVotos.setCellValueFactory(new PropertyValueFactory("cantVotos"));
        tableCantVotos.setPrefWidth(160);
        tableCantVotos.setStyle("-fx-alignment: CENTER_RIGHT;");
        tablePorcVotosEmitidos.setId("resultado");

        tablePorcVotosValidos.setText("% de Votos  Validos");
        tablePorcVotosValidos.setCellValueFactory(new PropertyValueFactory("porcVotosValidos"));
        tablePorcVotosValidos.setPrefWidth(171);
        tablePorcVotosValidos.setStyle("-fx-alignment: CENTER_RIGHT;");
        tablePorcVotosEmitidos.setId("resultado");

        tablePorcVotosEmitidos.setText("% de Votos  Emitidos");
        tablePorcVotosEmitidos.setCellValueFactory(new PropertyValueFactory("porcVotosEmitidos"));
        tablePorcVotosEmitidos.setPrefWidth(173);
        tablePorcVotosEmitidos.setStyle("-fx-alignment: CENTER_RIGHT;");
        tablePorcVotosEmitidos.setId("resultado");

        tableViewPresident.getColumns().addAll(tableOrgPolitica, tableCantVotos, tablePorcVotosValidos, tablePorcVotosEmitidos);
        return tableViewPresident;
    }

    private GridPane dibujarGridPanePresidVicepres() {
        gridpaneTotalVotos = new GridPane();
        gridpaneTotalVotos.setLayoutX(10);
        gridpaneTotalVotos.setLayoutY(295);
        gridpaneTotalVotos.setPrefWidth(1012);
        gridpaneTotalVotos.setPrefHeight(105);
        gridpaneTotalVotos.setStyle("-fx-grid-lines-visible : false");

        for (int i = 0; i < 4; i++) {//COLUMNA
            for (int j = 0; j < 4; j++) {//FILA
                Label labelNew = new Label(arrayNulosBlancos[j][i] == null ? "" : arrayNulosBlancos[j][i]);
                StackPane pane = new StackPane();
                pane.setPrefHeight(25);
                labelNew.setPrefHeight(24);
                if (i == 0) {
                    pane.setPrefWidth(501);
                    labelNew.setPrefWidth(460);
                    if (j == 0) {
                        labelNew.setText("TOTAL DE VOTOS VALIDOS");
                        labelNew.getStyleClass().add("lbl_standar");
                        pane.getChildren().add(labelNew);
                        pane.setStyle("-fx-background-color: #E0E0E0; ");
                    } else if (j == 3) {
                        labelNew.setText("TOTAL DE VOTOS EMITIDOS");
                        labelNew.getStyleClass().add("lbl_standar");
                        pane.getChildren().add(labelNew);
                        pane.setStyle("-fx-background-color: #E0E0E0; ");
                    } else {
                        labelNew.getStyleClass().add("lbl_Gris_RRAE");
                        pane.getChildren().add(labelNew);
                    }
                    gridpaneTotalVotos.add(pane, i, j);

                } else {
                    labelNew.setAlignment(Pos.BASELINE_RIGHT);
                    labelNew.setPrefWidth(152);
                    if (j == 0 || j == 3) {
                        labelNew.getStyleClass().add("lbl_standar");
                        pane.setStyle("-fx-background-color: #E0E0E0; ");
                    }
                    pane.setPrefWidth(171);
                    labelNew.setPrefWidth(152);
                    labelNew.getStyleClass().add("lbl_Gris_RRAE");
                    pane.getChildren().add(labelNew);
                    gridpaneTotalVotos.add(pane, i, j);
                }
            }
        }

        return gridpaneTotalVotos;
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
                labelNew.setPrefHeight(30);
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
        String codUbigeo = "";

        if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento().equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
            codUbigeo = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDEPARTAMENTO);
            paramIO.setCodDepart(codUbigeo);
        } else if (codTipoEleccion.equals(ConstantReportes.TIPOELECCION_CONGRESAL)) {
            msgError = "Debe Seleccionar un departamento";
            return null;
        }

        if (!critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getDescProvincia().equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
            codUbigeo = critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia().substring(0, ConstantReportes.LONGITUD_UBIGEO_CODPROVINCIA);
            paramIO.setCodProvincia(codUbigeo);
        }
        if (!critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getDescDistrito().equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
            codUbigeo = critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito().substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDISTRITO);
            paramIO.setCodDistrito(codUbigeo);
        }

        paramIO.setCodOdpe(codOdpe.equals(ConstantReportes.TODOS_ODPE_ENUM) ? "" : codOdpe);
        paramIO.setCodComputo(codCentroComputo.equals(ConstantReportes.TODOS_CCOM_ENUM) ? "" : codCentroComputo);
        paramIO.setTipoElec(codTipoEleccion);
        paramIO.setCodUbigeo(codUbigeo);

        return paramIO;

    }

    private boolean esValidaConsulta(List<CatResultadoActasContabilizadas> listActasContab) {
        /*Calculando el porcentaje de actas contabilizadas*/

        if (getTipoReporte().equals("01")) {
            /*El tipo de reporte es Resumen de Actas contabilizadas*/
            return true;
        }
        boolean validador = false;
        Integer porcentaje = 0;
        if (listActasContab != null) {
            if (listActasContab.size() > 0) {
                for (CatResultadoActasContabilizadas iteratorList : listActasContab) {
                    if (!iteratorList.getActasInstalar().equals(0)) {
                        porcentaje = ((iteratorList.getActasComputadas() / iteratorList.getActasInstalar()) * 100);
                    }
                    break; //solo validamos la primera fila
                }
            }
        }
        if (porcentaje.equals(100)) {
            validador = true;
        }
        System.out.println("porcenteaje calculado : " + porcentaje);
        return validador;
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

    private void cargarResumenActas(List<CatResultAvanceActasContabilizadas> listActasAvance) {
        if (listActasAvance.size() > 0) {
            NumberFormat df = new DecimalFormat("#0.000"); // formato de texto para los decimales
            
            CatResultAvanceActasContabilizadas beanAvanceActas = listActasAvance.get(0);
            lblActasProcesadas.setText(beanAvanceActas.getActasProcesadas().toString());
            lblActasProcesadas.setStyle("-fx-font-weight: bold;");

            if (beanAvanceActas.getAinstalar() > 0) {
                Double lblPorcActasProc = (beanAvanceActas.getActasProcesadas() * 100.0) / beanAvanceActas.getAinstalar();

                Double lblPorcActasXProc = (beanAvanceActas.getPorProcesar() * 100.0) / beanAvanceActas.getAinstalar();

                Double lblPorcentaje = (((beanAvanceActas.getContabNormal()
                        + beanAvanceActas.getContabAnuladas() + beanAvanceActas.getMesasNoInstaladas()) * 100.0)
                        / beanAvanceActas.getAinstalar());
                critSelecCtrl.cargarTextfieldAvance("AL " + round(lblPorcentaje, 3) + " %"); //round(lblPorcentaje, 3) df.format(lblPorcentaje)
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

    private ObservableList<CatResultAvanceActasContabilizadasData> cargarDatosTabla(List<CatResultAvanceActasContabilizadasData> listResultadoActas) {

        if (listResultadoActas != null) {
            for (CatResultAvanceActasContabilizadasData beanIterate : listResultadoActas) {
                tableViewConsultaData.add(beanIterate);
            }
        } else {
            System.out.println("La lista para llenar la tabla es nula");
        }

        return tableViewConsultaData;
    }

    private void calculoVotosCongresalesParlamento(List<CatResultAvanceActasContabilizadas> listActasAvance, Integer columns) {
        if (listActasAvance != null) {
            if (listActasAvance.size() > 0) {
                try {
                    arrayCongresales = new String[listActasAvance.size()][columns];
                    int rows = 0;
                    for (CatResultAvanceActasContabilizadas iteratorList : listActasAvance) {
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

    private void calculoPorcActasPresindenciales(List<CatResultAvanceActasContabilizadas> listActasAvance) {
        Integer totalVotosVal = 0;
        Integer totalVotosEmit = 0;
        Integer totalVotosBlancos = 0;
        Integer totalVotosNulos = 0;
        listResultActas.clear(); //se limpia la lista
        if (listActasAvance.size() > 0) {
            /*CALCULANDO EL TOTAL DE VOTOS EMITIDOS Y VALIDOS*/
            for (CatResultAvanceActasContabilizadas iteratorList : listActasAvance) {
                if (iteratorList.getCodAgrupacion().equals(ConstantReportes.COD_VOTOS_BLANCOS)) {
                    totalVotosBlancos = totalVotosBlancos + iteratorList.getNumVotos();
                } else if (iteratorList.getCodAgrupacion().equals(ConstantReportes.COD_VOTOS_NULOS)) {
                    totalVotosNulos = totalVotosNulos + iteratorList.getNumVotos();
                } else {
                    totalVotosVal = totalVotosVal + iteratorList.getNumVotos();
                }

                totalVotosEmit = totalVotosEmit + iteratorList.getNumVotos();
            }
            /*CALCULANDO EL % DE VOTOS BLANCOS Y NULOS*/
            arrayNulosBlancos[0][0] = "TOTAL DE VOTOS VALIDOS";
            arrayNulosBlancos[0][1] = totalVotosVal.toString();
            arrayNulosBlancos[0][2] = String.valueOf(round(((totalVotosVal * 100.0) / totalVotosVal), 3)) + " %";
            arrayNulosBlancos[0][3] = String.valueOf(round(((totalVotosVal * 100.0) / totalVotosEmit), 3)) + " %";

            /*CALCULANDO EL % DE VOTOS VALIDOS Y EMITIDOS*/
            int rowsCount = 1;
            for (CatResultAvanceActasContabilizadas iteratorList : listActasAvance) {
                CatResultAvanceActasContabilizadasData beanResultAvance = new CatResultAvanceActasContabilizadasData();
                if (iteratorList.getCodAgrupacion().equals(ConstantReportes.COD_VOTOS_BLANCOS)) {

                    arrayNulosBlancos[rowsCount][0] = "VOTOS BLANCOS";
                    arrayNulosBlancos[rowsCount][1] = iteratorList.getNumVotos().toString();
                    arrayNulosBlancos[rowsCount][2] = "";
                    arrayNulosBlancos[rowsCount][3] = String.valueOf(round(((iteratorList.getNumVotos() * 100.0) / totalVotosEmit), 3));
                    rowsCount++;
                } else if (iteratorList.getCodAgrupacion().equals(ConstantReportes.COD_VOTOS_NULOS)) {

                    arrayNulosBlancos[rowsCount][0] = "VOTOS NULOS";
                    arrayNulosBlancos[rowsCount][1] = iteratorList.getNumVotos().toString();
                    arrayNulosBlancos[rowsCount][2] = "";
                    arrayNulosBlancos[rowsCount][3] = String.valueOf(round(((iteratorList.getNumVotos() * 100.0) / totalVotosEmit), 3));
                    rowsCount++;
                } else {
                    Double porcVotValido = round(((iteratorList.getNumVotos() * 100.0) / totalVotosVal), 3);
                    Double porcVotEmitido = round(((iteratorList.getNumVotos() * 100.0) / totalVotosEmit), 3);

                    beanResultAvance.setPorcVotosEmitidos(porcVotEmitido.toString() + " %");
                    beanResultAvance.setPorcVotosValidos(porcVotValido.toString() + " %");
                    beanResultAvance.setOrgPolitica(iteratorList.getDesAgrupacion());
                    beanResultAvance.setCantVotos(iteratorList.getNumVotos().toString());
                    listResultActas.add(beanResultAvance);
                }

            }

            /*CALCULANDO EL % DE VOTOS BLANCOS Y NULOS*/
            arrayNulosBlancos[3][0] = "TOTAL DE VOTOS EMITIDOS";
            arrayNulosBlancos[3][1] = totalVotosEmit.toString();
            arrayNulosBlancos[3][2] = "";
            arrayNulosBlancos[3][3] = String.valueOf(round(((totalVotosEmit * 100.0) / totalVotosEmit), 3));

        } else {
            System.out.println("La lista para llenar la tabla es nula");
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

    private void mostrarMensaje(String msg) {

        String title = ConstantCommon.titleValidacion;
        String message = msg;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, title, message, null, null, null, eventOK);
    }

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales + "\n" + critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());

            parametros.put("tituloEleccionSimple", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            if (getTipoReporte().equals("00")) {
                parametros.put("TituloRep", ConstantReportes.nameResultActasContab);
            } else if (getTipoReporte().equals("01")) {
                System.out.println("Avance de las actas :" + critSelecCtrl.getTextFieldAvance().getText());
                parametros.put("TituloRep", String.format(ConstantReportes.nameResumenActasContab, critSelecCtrl.getTextFieldAvance().getText()));
            }

            parametros.put("TituloEleccionCompleto", ConstantReportes.nameTituloMonitInfEleccionesGenerales);

            parametros.put("odpe", critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe());
            parametros.put("ccomputo", critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu());

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

    /*Obteniendo el nombre del reporte por el num de candidatos*/
    public String getNameReportByNcandidatos(Float Ncandidatos) {
        switch (Math.round(Ncandidatos)) {
            case 1:
                return "RPT04030201_1";
            case 2:
                return "RPT04030201_2";
            case 3:
                return "RPT04030201_3";
            case 4:
                return "RPT04030201_4";
            case 5:
                return "RPT04030201_5";
            case 6:
                return "RPT04030201_6";
            case 7:
                return "RPT04030201_7";
            case 9:
                return "RPT04030201_9";
            default:
                return "RPT04030201";
        }
    }

    private void imprimirReporte(List<CatResultAvanceActasContabilizadas> listActasAvance, Map<String, Object> parametros, InputStream file, String title) {
        try {
            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listActasAvance, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
        } catch (Exception ex) {
            System.out.println("Excepcion general :" + ex);
            ex.printStackTrace();
        }
    }

    private void procesarConsulta(String tipoConsulta, ParametrosInformacionOficial paramInfo) {
        inicializarConsulta();
        limpiarEsquemaRegistro(critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec());
        String storeProcedure = ConstantReportes.STOREP_AVANCEACTAS_COMP;
        switch (tipoConsulta) {
            case "Resumido":
                List<CatResultadoActasContabilizadas> listActas = rcResultadoActasContabilizadasService.getPorcActasComputadas(paramInfo.getCodComputo(),
                        paramInfo.getCodOdpe(),
                        paramInfo.getCodUbigeo(),
                        paramInfo.getTipoElec());

                if (esValidaConsulta(listActas)) { //solo para pruebas
                    String nivelAgrup = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem().substring(0, 1);
                    System.out.println("Nivel de Agrupacion :" + nivelAgrup);

                    if (paramInfo.getTipoElec().equals(ConstantReportes.TIPOELECCION_PRESIDENCIAL) || paramInfo.getTipoElec().equals(ConstantReportes.TIPOELECCION_CONGRESAL) || paramInfo.getTipoElec().equals(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO)) {
                        String departamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento();
                        if (!departamento.equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
                            if (departamento.equals("LIMA CENTRO") && nivelAgrup.equals("D")) {
                                storeProcedure = ConstantReportes.STOREP_AVANCEACTAS_COMPLIMAC;
                            } else if (departamento.equals("LIMA PROVINCIAS") && nivelAgrup.equals("D")) {
                                storeProcedure = ConstantReportes.STOREP_AVANCEACTAS_COMPLIMAP;
                            }
                        }
                    }
                    /*Relizando la consulta*/
                    listActasAvance= rcResultadoActasContabilizadasService.getAvanceActasComputadas(storeProcedure, paramInfo.getCodOdpe(),
                                    paramInfo.getCodComputo(), paramInfo.getTipoElec(),
                                    paramInfo.getCodUbigeo(), (nivelAgrup.equals("R") ? "2" : "1"), Ncandidatos);

                    System.out.println("Cantidad de archivos : " + listActasAvance.size());
                    if (listActasAvance.size() < 1) {
                        mostrarMensaje("No existe data para mostrar");
                        return;
                    }
                    /*Mostramos los datos en la pestaña de resumen de Actas*/
                    cargarResumenActas(listActasAvance);
                    /*Cargamos la data segun sea la eleccion*/
                    switch (paramInfo.getTipoElec()) {
                        case "10":
                            calculoPorcActasPresindenciales(listActasAvance); //Calculamos los porcentajes a mostrar en la pestaña resultados
                            tableViewPresident.setItems(cargarDatosTabla(listResultActas));
                            anchorPaneTable.getChildren().remove(gridpaneTotalVotos); //limpiamos el grid de resultados
                            anchorPaneTable.getChildren().add(dibujarGridPanePresidVicepres());
                            consultaPresindenciales = true;
                            break;
                        case "11":
                            calculoVotosCongresalesParlamento(listActasAvance, 38);//Caragamos los arrays con los resultados obtenidos
                            anchorPaneTable.getChildren().remove(gridpaneGeneral);
                            anchorPaneTable.getChildren().add(dibujarGridpaneCongresales((Math.round(Ncandidatos) + 2), listActasAvance.size()));
                            consultaCongresales = true;
                            break;
                        case "12":
                            calculoVotosCongresalesParlamento(listActasAvance, 17);//Caragamos los arrays con los resultados obtenidos
                            anchorPaneTable.getChildren().remove(gridpaneGeneral);
                            anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(17, listActasAvance.size()));
                            consultaParlamento = true;
                            break;

                    }
                    /*Registrando Auditoria*/
                    comunService.registrarAuditoria(mainStage, ConstantReportes.MSGAUDITORIA_CONSULTAREPORTE_RESULTACTASCONTAB, COD_MOD_REPCON);

                } else {
                    System.out.println("No se han computado las actas al 100%");
                    mostrarMensaje("No se han computado las actas al 100%");
                }
                break;
            case "Detallado":
                /*validando que se haya selecciona el filtro ubigeo*/
                if (paramInfo.getCodDepart() == null || paramInfo.getCodProvincia() == null || paramInfo.getCodDistrito() == null) {
                    String titles = ConstantCommon.titleReporCons;
                    String message = "¿Desea generar reporte multiple y ver la vista previa?";
                    final String storeProcedures = storeProcedure;
                    EventHandler eventYes = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                        System.out.println("eventYes :" + event);
                        stageMessageBox.close();
                        /**
                         * *************************START**************************
                         */
                        consultaDetallada(paramInfo, storeProcedures, true);
                        /**
                         * *************************END***************************
                         */

                    };
                    if (!consultaSeleccionImprimir) {
                        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.QUESTION, Messages.addButtons.YESNO, "Resumen de Actas Contabilizadas",
                                titles, message, null, null, eventYes, null);
                    }

                } else {
                    List<CatResultadoActasContabilizadas> listActasDetallado = rcResultadoActasContabilizadasService.getPorcActasComputadas(paramInfo.getCodComputo(),
                            paramInfo.getCodOdpe(),
                            paramInfo.getCodUbigeo(),
                            paramInfo.getTipoElec());

                    if (esValidaConsulta(listActasDetallado)) { //solo para pruebas
                        String nivelAgrup = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem().substring(0, 1);
                        System.out.println("Nivel de Agrupacion :" + nivelAgrup);

                        if (paramInfo.getTipoElec().equals("10") || paramInfo.getTipoElec().equals("11") || paramInfo.getTipoElec().equals("12")) {
                            String departamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento();
                            if (!departamento.equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
                                if (departamento.equals("LIMA CENTRO")) {
                                    storeProcedure = "sp_AvanceActasComputadasLimaC";
                                } else if (departamento.equals("LIMA PROVINCIAS")) {
                                    storeProcedure = "sp_AvanceActasComputadasLimaP";
                                }
                            }
                        }

                        listActasAvance
                                = rcResultadoActasContabilizadasService.getAvanceActasComputadas(storeProcedure, paramInfo.getCodOdpe(),
                                        paramInfo.getCodComputo(), paramInfo.getTipoElec(),
                                        paramInfo.getCodUbigeo(), (nivelAgrup.equals("R") ? "2" : "1"), Ncandidatos);
                        System.out.println("Cantidad de archivos : " + listActasAvance.size());

                        if (listActasAvance.size() < 1) {
                            mostrarMensaje("No existe data para mostrar");
                            return;
                        }

                        /*Mostramos los datos en la pestaña de resumen de Actas*/
                        cargarResumenActas(listActasAvance);
                        /*Cargamos la data segun sea la elccion*/
                        switch (paramInfo.getTipoElec()) {
                            case "10":
                                calculoPorcActasPresindenciales(listActasAvance);
                                tableViewPresident.setItems(cargarDatosTabla(listResultActas));
                                anchorPaneTable.getChildren().remove(gridpaneTotalVotos);
                                anchorPaneTable.getChildren().add(dibujarGridPanePresidVicepres());
                                consultaPresindentDetallado = true;
                                break;
                            case "11":
                                calculoVotosCongresalesParlamento(listActasAvance, 38);
                                anchorPaneTable.getChildren().remove(gridpaneGeneral);
                                anchorPaneTable.getChildren().add(dibujarGridpaneCongresales((Math.round(Ncandidatos) + 2), listActasAvance.size()));
                                consultaCongresalDetallado = true;
                                break;
                            case "12":
                                calculoVotosCongresalesParlamento(listActasAvance, 17);
                                anchorPaneTable.getChildren().remove(gridpaneGeneral);
                                anchorPaneTable.getChildren().add(dibujarGridpaneCongresales(17, listActasAvance.size()));
                                consultaParlamentDetallado = true;
                                break;

                        }
                        /*Registrando Auditoria*/
                        comunService.registrarAuditoria(mainStage, ConstantReportes.MSGAUDITORIA_CONSULTAREPORTE_RESULTACTASCONTAB, COD_MOD_REPCON);

                    } else {
                        System.out.println("No se han computado las actas al 100%");
                    }
                }
                break;

            default:
                break;
        }
    }

    public void consultaDetallada(ParametrosInformacionOficial paramInfo, String storeProcedure, boolean validateMsg) {
        if (validateMsg || consultaSeleccionImprimir) {
            System.out.println("[OK] Selecciono Mostrar el reporte de detalle ");
            String title = ConstantReportes.nameResultActasContab;
            try {
                String nivelAgrup = critSelecCtrl.getCmbTipoConsulta().getSelectionModel().getSelectedItem().substring(0, 1);
                listActasAvance
                        = rcResultadoActasContabilizadasService.getAvanceActasComputadas(storeProcedure, paramInfo.getCodOdpe(),
                                paramInfo.getCodComputo(), paramInfo.getTipoElec(),
                                paramInfo.getCodUbigeo(), (nivelAgrup.equals("R") ? "2" : "1"), Ncandidatos);

                String nameReport = "";
                switch (paramInfo.getTipoElec()) {
                    case "10":
                        nameReport = "RPT04020201";
                        consultaPresindentDetallado = true;
                        break;
                    case "11":
                        nameReport = getNameReportByNcandidatos(Ncandidatos);
                        consultaCongresalDetallado = true;
                        break;
                    case "12":
                        nameReport = "RPT04040201";
                        consultaParlamentDetallado = true;
                        break;

                }

                InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
                /*Cargando los parametros del reporte*/
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("reporte", nameReport);
                parametros.put("tipoReporte", "2");
                cargaParametros(parametros);

                imprimirReporte(listActasAvance, parametros, file, title);
                /*Registrando Auditoria*/
                comunService.registrarAuditoria(mainStage, ConstantReportes.MSGAUDITORIA_IMPRIMIRREPORTE_RESULTACTASCONTAB, COD_MOD_REPCON);

            } catch (Exception ex) {
                System.out.println("Excepcion General :" + ex);
                ex.printStackTrace();
            }

        } else {
            System.out.println("[Cancel] Cancelo la consulta detallada");
            return;
        }
    }

    private void inicializarConsulta() {
        consultaParlamentDetallado = false;
        consultaPresindentDetallado = false;
        consultaCongresalDetallado = false;
        consultaParlamento = false;
        consultaPresindenciales = false;
        consultaCongresales = false;
        tableViewConsultaData.clear();
    }

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(3000)
    );

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Float getNcandidatos() {
        return Ncandidatos;
    }

    public void setNcandidatos(Float Ncandidatos) {
        this.Ncandidatos = Ncandidatos;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
