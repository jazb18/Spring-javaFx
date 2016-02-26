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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
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
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatCifraRepartidoraData;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcProcesoCifraRepartidoraService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author juchida
 */
public class ProcesosCifraRepartidoraController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage, stage;
    private Scene scene;

    private static final String EMPTY_STRING = "";
    private ObservableList<CatCifraRepartidoraData> tableViewConsultaData = FXCollections.observableArrayList();

    List<CatCifraRepartidoraData> listCifraRepartidora = new ArrayList<>();

    @Autowired
    ApplicationContext context;

    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane paneSeleccion;
    @FXML
    private TableView<CatCifraRepartidoraData> tableViewConsultCR;
    @FXML
    private TableView<CatCifraRepartidoraData> tableViewConsultCRCMU;
    @FXML
    private TableView<CatCifraRepartidoraData> tableViewConsultCRResolucion;
    @FXML
    private TableView<CatCifraRepartidoraData> tableViewConsultCRResultado;
    @FXML
    private Label lblTituloCifraRepartidora;
    @FXML
    private TextField txtCifraRepartidora;
    @FXML
    private TextField txtHorasProceso;
    @FXML
    private TextField txtTotalEscaños;
    @FXML
    private TextField txtEstado;
    @FXML
    private TextField txtAvance;
    @FXML
    private CheckBox chkForzarCalculo;
    @FXML
    private TextField txtResumen1CR;
    @FXML
    private TextField txtResumen2CR;
    @FXML
    private TextField txtResumen1CRCMU;
    @FXML
    private TextField txtResumen2CRCMU;
    @FXML
    private TextField txtResumen1CRResolucion;
    @FXML
    private TextField txtResumen2CRResolucion;
    @FXML
    private TextField txtResumen1CRResultado;
    @FXML
    private TextField txtResumen2CRResultado;
    @FXML
    private ToggleGroup rgInicialFinal;
    @FXML
    private RadioButton rbInicial;
    @FXML
    private RadioButton rbFinal;
    @FXML
    private Tab tabCR;
    @FXML
    private Tab tabCRCMU;
    @FXML
    private Tab tabCRResolucion;
    @FXML
    private Tab tabCRResultado;
    @FXML
    private Button btnConsolida;
    @FXML
    private Button btnProcesaCR;
    @FXML
    private Button btnResolucion;

    private boolean isInicial;
    private boolean isFinal;
    private boolean isConsultado;
    private String tipoCR;
    private String nombreReporte = null;
    private Integer totalVotosValidosCR;
    private String estadoDistritoElectoral;
    private float porcentaje;

    private Stage stageMessageBox;

    @Autowired
    RcProcesoCifraRepartidoraService rcProcesoCifraRepartidoraService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void disableRadioGroupInicial() {
        rbInicial.setDisable(true);
        rbFinal.setSelected(true);
        isFinal = true;
        isInicial = false;
    }

    public void ableRadioGroupInicial() {
        rbInicial.setDisable(false);
        rbInicial.setSelected(true);
        isFinal = false;
        isInicial = true;
    }

    public void init() {
        System.out.println("@@@@[CARGA DEL INICIO DEL CONTROLER]@@@@");
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
        subBarraStage(stage, ConstantReportes.nameTituloProcesoCifraRepartidora, 2);

        try {
            /*Carga de los Filtros de Busqueda*/
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            lblTituloCifraRepartidora.setText(ConstantReportes.nameTituloProcesoCifraRepartidora);
            //btnResolucion.setDisable(true);
            //Esconder los textos de resumen 2
            txtResumen2CR.setVisible(false);
            txtResumen2CRCMU.setVisible(false);
            txtResumen2CRResolucion.setVisible(false);
            txtResumen2CRResultado.setVisible(false);
            //Desabilitar tabs
            tabCRCMU.setDisable(true);
            tabCRResolucion.setDisable(true);
            tabCRResultado.setDisable(true);
            rbInicial.setSelected(true);
            isInicial = true;
            isFinal = false;

            rgInicialFinal.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    if (rgInicialFinal.getSelectedToggle() != null) {
                        if (rgInicialFinal.getSelectedToggle() == rbInicial) {
                            isInicial = true;
                            isFinal = false;
                        }
                        if (rgInicialFinal.getSelectedToggle() == rbFinal) {
                            isInicial = false;
                            isFinal = true;
                        }
                    }
                }
            });
            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            paneSeleccion.getChildren().clear();
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //TODO CHANGE
            List<String> estados = Arrays.asList(ConstantReportes.ONPE_AMBITOS_TODOS,
                    ConstantReportes.ESTADO_SIN_PROCESAR + " " + "SIN PROCESAR",
                    ConstantReportes.ESTADO_ANORMAL + " " + "ANORMAL",
                    ConstantReportes.ESTADO_SIN_VOTOS_VALIDOS + " " + "SIN VOTOS VALIDOS",
                    ConstantReportes.ESTADO_SIN_CUPOS + " " + "SIN CUPOS",
                    ConstantReportes.ESTADO_EMPATE + " " + "EMPATE",
                    ConstantReportes.ESTADO_NORMAL + " " + "NORMAL",
                    ConstantReportes.ESTADO_GANADORXSORTEO + " " + "GANADOR X SORTEO");
            critSelecCtrl.setListaEstado(estados);

            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setShowDistritoElectoral(true);
            critSelecCtrl.setShowEstado(true);
            critSelecCtrl.setTipoModulo(ConstantReportes.PROCESOS_CIFRA_REPARTIDORA);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADO, critSelecCtrl.getCmbEstado(), null, 0, 2}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);

            paneSeleccion.getChildren().add(critSelecCtrl);

            cargarTabla(tableViewConsultCR);
            cargarTabla(tableViewConsultCRCMU);
            cargarTabla(tableViewConsultCRResolucion);
            cargarTabla(tableViewConsultCRResultado);

        } catch (Exception ex) {
            System.out.println("Excepcion General " + ex);
            ex.printStackTrace();
        }

    }

    /*Metodo para le generacion automatica de la tabla de consulta vacia*/
    public void cargarTabla(TableView<CatCifraRepartidoraData> tableViewConsulta) {
        TableColumn<CatCifraRepartidoraData, String> tableColumOrganizacionPolitica = new TableColumn();
        TableColumn<CatCifraRepartidoraData, String> tableColumVotosValidos = new TableColumn();
        TableColumn<CatCifraRepartidoraData, String> tableColumVotosCifraRepartidora = new TableColumn();
        TableColumn<CatCifraRepartidoraData, String> tableColumCociente = new TableColumn();
        TableColumn<CatCifraRepartidoraData, String> tableColumNroRepresentates = new TableColumn();
        TableColumn<CatCifraRepartidoraData, String> tableColumObservaciones = new TableColumn();

        tableColumOrganizacionPolitica.setText(ConstantReportes.nameColumnProcesosOrganizacionPolitica);
        tableColumOrganizacionPolitica.setCellValueFactory(new PropertyValueFactory("organizacionPolitica"));
        tableColumOrganizacionPolitica.setMinWidth(200);

        tableColumVotosValidos.setText(ConstantReportes.nameColumnProcesosVotosValidos);
        tableColumVotosValidos.setCellValueFactory(new PropertyValueFactory("votosValidos"));
        tableColumVotosValidos.setMinWidth(200);

        tableColumVotosCifraRepartidora.setText(ConstantReportes.nameColumnProcesosVotosCifraRepartidora);
        tableColumVotosCifraRepartidora.setCellValueFactory(new PropertyValueFactory("votosCifraRepartidora"));
        tableColumVotosCifraRepartidora.setMinWidth(200);

        tableColumCociente.setText(ConstantReportes.nameColumnProcesosCociente);
        tableColumCociente.setCellValueFactory(new PropertyValueFactory("cociente"));
        tableColumCociente.setMinWidth(200);

        tableColumNroRepresentates.setText(ConstantReportes.nameColumnProcesosRepresentantes);
        tableColumNroRepresentates.setCellValueFactory(new PropertyValueFactory("nroRepresentantes"));
        tableColumNroRepresentates.setMinWidth(200);

        tableColumObservaciones.setText(ConstantReportes.nameColumnProcesosObservaciones);
        tableColumObservaciones.setCellValueFactory(new PropertyValueFactory("columnObservaciones"));
        tableColumObservaciones.setMinWidth(200);

        tableViewConsulta.getColumns().addAll(tableColumOrganizacionPolitica, tableColumVotosValidos,
                tableColumVotosCifraRepartidora, tableColumCociente, tableColumNroRepresentates, tableColumObservaciones);
    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
    }

    @FXML
    private void btnConsolidarOnAction() {
        double avance;
        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
        String codUsuario = DatosUsuario.getMstrCodigoUsuario();
        String estacion = DatosUsuario.getMstrEstacionUsuario();

        avance = rcProcesoCifraRepartidoraService.consolidaVotosXTipoEleccion(codTipoEleccion, codDistritoElectoral, codUsuario, estacion);

        String mensaje = "La Consolidación de Votos de realizó correctamente.";
        String title = ConstantCommon.titleInformacion;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
    }

    @FXML
    private void btnProcesarCROnAction() {
        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
        String codUsuario = DatosUsuario.getMstrCodigoUsuario();
        String estacion = DatosUsuario.getMstrEstacionUsuario();
        int muestraUsuarios = 1;
        int forzarCalculo = chkForzarCalculo.isSelected() ? 1 : 0;
        rcProcesoCifraRepartidoraService.procesaCifraReapartidora(codTipoEleccion, codDistritoElectoral, codUsuario, estacion, muestraUsuarios, forzarCalculo);

        String mensaje = "El Proceso de Cifra Repartidora de realizó correctamente.";
        String title = ConstantCommon.titleInformacion;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
    }

    @FXML
    private void btnResolucionOnAction() {
        //inicializarResolucion();
        String empateP;
        String empateC;
        String ESTADO_DE_EMPATE = "4";
        String ESTADO_DE_GANADOR_X_SORTEO = "6";
        if (!(estadoDistritoElectoral.equals(ESTADO_DE_EMPATE) || estadoDistritoElectoral.equals(ESTADO_DE_GANADOR_X_SORTEO))) {
            String mensaje = "No existe empate para ningún caso, por lo tanto no podemos proceder con una resolución.";
            String title = ConstantCommon.titleInformacion;
            EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
            };
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
        }
        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
        if (codTipoEleccion.equals(ConstantReportes.TIPOELECCION_CONGRESAL)) {
            if (!codDistritoElectoral.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
                inicializarResolucion();
            } else {
                String mensaje = "Debe definir un Distrito Electoral.";
                String title = ConstantCommon.titleInformacion;
                EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                    stageMessageBox.close();
                };
                stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
            }
        }
        if (codTipoEleccion.equals(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO)) {
            if (!codDistritoElectoral.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
                inicializarResolucion();
            } else {
                String mensaje = "Debe definir un Distrito Electoral.";
                String title = ConstantCommon.titleInformacion;
                EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                    stageMessageBox.close();
                };
                stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
            }
        }
    }

    private void inicializarResolucion() {
        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();

        ProcesosCifraRepResolAgrupController procesosCifraRepResolAgrupController = context.getBean(ProcesosCifraRepResolAgrupController.class);
        procesosCifraRepResolAgrupController.setDistritoElectoral(codDistritoElectoral);
        procesosCifraRepResolAgrupController.setTipoEleccion(codTipoEleccion);
        procesosCifraRepResolAgrupController.setTitulo("Dist. Electoral: " + codDistritoElectoral);
        procesosCifraRepResolAgrupController.setEstadoDistritoElectoral(txtEstado.getText());
        procesosCifraRepResolAgrupController.setEscanosDisponibles(txtTotalEscaños.getText());
        procesosCifraRepResolAgrupController.setMainStage(mainStage);
        procesosCifraRepResolAgrupController.init();
    }

    @FXML
    private void btnImprimirOnAction() {
        if (isConsultado) {
            imprimirConsulta();
        } else {
            String mensaje = "Debe realizar la Consulta para Imprimir";
            String title = ConstantCommon.titleInformacion;
            EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
            };
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
        }
    }

    private boolean validar() {
        String mensaje = "";
        boolean validar = false;

        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
        if (codTipoEleccion.equals(EMPTY_STRING)) {
            mensaje = "Debe elegir el tipo de eleccion a consultar";
            String title = ConstantCommon.titleInformacion;
            EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
            };
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.ERROR, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
        } else if (critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedIndex() < 0) {
            mensaje = "Debe elegir el distrito electoral a consultar";
            String title = ConstantCommon.titleInformacion;
            EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
            };
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.ERROR, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
        } else if (codTipoEleccion.equals(ConstantReportes.TIPOELECCION_CONGRESAL)
                && codDistritoElectoral.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
            String title = ConstantCommon.titleInformacion;
            String name = "REPORTE Y CONSULTAS";
            mensaje = "A seleccionado un tipo de consulta que no puede ser mostrado por pantalla. \n"
                    + "¿Desea generar un reporte multiple y ver la vista previa?";
            EventHandler eventYes = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
                imprimirConsulta();
            };
            //Muestra el MessageBox
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.YESNO, name, title, mensaje, null, null, eventYes, null);
        } else {
            validar = true;
        }

        return validar;
    }

    private void imprimirConsulta() {
        try {
            if (!listCifraRepartidora.isEmpty()) {
                switch (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec()) {
                    case "11":
                        nombreReporte = "RPT04050101";
                        break;
                    case "12":
                        nombreReporte = "RPT04050101";
                        break;
                }
                InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                        ConstantReportes.pathReportConsult + nombreReporte + ".jrxml");
                String title = ConstantReportes.nameTituloProcesoCifraRepartidora;

                Map<String, Object> parametros = new HashMap<>();
                cargaParametros(parametros);

                JasperReportUtil jp = new JasperReportUtil();

                StackPane sp = jp.reportShow(parametros, listCifraRepartidora, file, null);

                AppController appController = new AppController();
                appController.subBarraStageReport(mainStage, sp, title, 2);
            } else {
                mostrarMensaje();
            }
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoInformacionOficialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarMensaje() {
        String mensaje = "No se encontró información para esta consulta.";
        String title = ConstantCommon.titleInformacion;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.ERROR, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
    }

    private String obtenerTipoCR() {
        return (isInicial && !isFinal) ? ConstantReportes.CIFRA_REPARTIDORA_INI : ConstantReportes.CIFRA_REPARTIDORA_FIN;
    }

    @FXML
    private void btnConsultarOnAction() {
        if (tabCR.isSelected()) {

            ParametrosInformacionOficial paramInfOfi = getParamInformacionOficial();
            tableViewConsultaData.removeAll(tableViewConsultaData);
            listCifraRepartidora.clear();

            tipoCR = obtenerTipoCR();

            porcentaje = rcProcesoCifraRepartidoraService.obternerPorcentaje(paramInfOfi.getTipoElec(), "", paramInfOfi.getCodDistElectoral());
            txtAvance.setText(Float.toString(porcentaje));

            listCifraRepartidora = rcProcesoCifraRepartidoraService.obtenerCifraRepartidora(paramInfOfi.getTipoElec(), paramInfOfi.getCodDistElectoral(),
                    paramInfOfi.getEstado(), tipoCR);
            if (validar()) {

                if (listCifraRepartidora.isEmpty()) {
                    mostrarMensaje();
                    return;
                }
                tableViewConsultCR.setEditable(true);
                tableViewConsultCR.setItems(gerCifraRepartidoraData(listCifraRepartidora));
                isConsultado = true;
                if (listCifraRepartidora.get(0).getFactorCR() != null) {
                    txtCifraRepartidora.setText(listCifraRepartidora.get(0).getFactorCR().toString());
                }
                if (listCifraRepartidora.get(0).getFechaProcCR() != null) {
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

                    String reportDate = df.format(listCifraRepartidora.get(0).getFechaProcCR());

                    txtHorasProceso.setText(reportDate);
                }
                if (listCifraRepartidora.get(0).getEstadoCR() != null) {
                    txtEstado.setText(listCifraRepartidora.get(0).getEstadoCR());
                }
                if (listCifraRepartidora.get(0).getNroEscanos() != null) {
                    txtTotalEscaños.setText(listCifraRepartidora.get(0).getNroEscanos().toString());
                }
                estadoDistritoElectoral = listCifraRepartidora.get(0).getEstadoCRDistritoElectoral();
                txtResumen1CR.setText(totalVotosValidosCR.toString());
            }
        }
        if (tabCRCMU.isSelected()) {
            System.out.println("tabCRCMU isSelected: " + tabCRCMU.isSelected() + "isDisabled: " + tabCRCMU.isDisabled() + "isDisable: " + tabCRCMU.isDisable());
        }
        if (tabCRResolucion.isSelected()) {
            System.out.println("tabCRResolucion isSelected: " + tabCRResolucion.isSelected() + "isDisabled: " + tabCRResolucion.isDisabled() + "isDisable: " + tabCRResolucion.isDisable());
        }
        if (tabCRResultado.isSelected()) {
            System.out.println("tabCRResultado isSelected: " + tabCRResultado.isSelected() + "isDisabled: " + tabCRResultado.isDisabled() + "isDisable: " + tabCRResultado.isDisable());
        }
    }

    private ScrollBar findScrollBar(TableView<?> table, Orientation orientation) {
        Set<Node> set = table.lookupAll(".scroll-bar");
        for (Node node : set) {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation() == orientation) {
                return bar;
            }
        }
        return null;
    }

    private ObservableList<CatCifraRepartidoraData> gerCifraRepartidoraData(List<CatCifraRepartidoraData> listCifraRepartidoraData) {
        totalVotosValidosCR = 0;
        for (CatCifraRepartidoraData beanCifraRepartidoraData : listCifraRepartidoraData) {
            totalVotosValidosCR += beanCifraRepartidoraData.getVotosValidos();
            tableViewConsultaData.add(beanCifraRepartidoraData);
        }
        return tableViewConsultaData;
    }

    private ParametrosInformacionOficial getParamInformacionOficial() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();

        String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        //String descTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion();
        paramIO.setTipoElec(codTipoEleccion);

        String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
//        if (codDistritoElectoral.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
//            codDistritoElectoral = EMPTY_STRING;
//        }
        paramIO.setCodDistElectoral(codDistritoElectoral);

        //String descDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getDistElec();
        if (!critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem().equals("")) {
            String codEstado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();
            if (codEstado.equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
                paramIO.setEstado(EMPTY_STRING);
            } else {
                paramIO.setEstado(codEstado.substring(0, 1));
            }
        }

        return paramIO;
    }

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion + "\n" + critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("tituloSecundario", ConstantReportes.nameTituloProcesoCifraRepartidora);

            String nombreLargo = null;
            switch (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec()) {
                case "11":
                    nombreLargo = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion();
                    break;
                case "12":
                    nombreLargo = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion();
                    break;
            }
            parametros.put("nombreLargo", nombreLargo);

            String nombreCorto = null;
            switch (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec()) {
                case "11":
                    nombreCorto = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion();
                    break;
                case "12":
                    nombreCorto = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion();
                    break;
            }
            parametros.put("nombreCorto", nombreCorto);
            String nomnbreCifraRepartidora = null;
            switch (obtenerTipoCR()) {
                case "1":
                    nomnbreCifraRepartidora = "CIFRA REPARTIDORA INICIAL";
                    break;
                case "2":
                    nomnbreCifraRepartidora = "CIFRA REPARTIDORA FINAL";
                    break;
            }
            parametros.put("tipoCR", nomnbreCifraRepartidora);
            parametros.put("avance", porcentaje);

            parametros.put("estado", critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem());
            parametros.put("depar", critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento() : critSelecCtrl.getCmbDepa().getPromptText());
            parametros.put("prov", critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getDescProvincia() : critSelecCtrl.getCmbProv().getPromptText());
            parametros.put("dist", critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getDescDistrito() : critSelecCtrl.getCmbDistrito().getPromptText());

            parametros.put("sinvaloroficial", validarInfOficial());

            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("reporte", nombreReporte);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
            return parametros;
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoInformacionOficialController.class.getName()).log(Level.SEVERE, null, ex);
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
