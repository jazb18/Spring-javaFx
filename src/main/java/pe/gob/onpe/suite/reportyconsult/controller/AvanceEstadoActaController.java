/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
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
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatReporteAvanceEstado;
import pe.gob.onpe.suite.domain.CatUnidadSoporte;
import pe.gob.onpe.suite.reportyconsult.service.RCAvanceEstadoActaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class AvanceEstadoActaController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;
    Timer timer;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane paneSeleccion;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    RCAvanceEstadoActaService rcAvanceEstadoActaService;

    @FXML
    private RadioButton radioButtonCentroC, radioButtonOdpe;

    @FXML
    private ToggleGroup consultaToggleGrp;

    @FXML
    public TableView<CatUnidadSoporte> TableCentroComputo, TableODPE;

    @FXML
    private TableColumn<CatUnidadSoporte, String> codigoC, descripcionC, TipoC, ultimaModifC, totalActaC, actaLotizadaC, actaProcesC, totalActaProcC, actaContabC, totalActaContC, actaObservC, totalActaObservC, codigoO, descripcionO, ultimaModifO, totalActasO, ActaProcesadaO, totalActasProcO, actaContabO, totalActaContabO, actaObservadasO, totalActaObservadasO;

    @FXML
    private TextField resulTotalActaC, resulTotalActaO, resulActaLotizada, resulActaProces, resulTotalActaProc, resultActaContab, resulTotalActaCont, resultActaObserv, resulTotalActaObserv, porProcesar, procesadas, contabilizadas, loadTime;

    ObservableList<CatUnidadSoporte> data;

    String titulo = ConstantReportes.VACIO;

    private String name = ConstantCommon.titleReporCons;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    List<CatUnidadSoporte> listaUnidadSoporte = new ArrayList<>();
    List<CatUnidadSoporte> listaCCUnidadSoporte;
    List<CatUnidadSoporte> listaObjeto = new ArrayList<>();
    private boolean inicialize = false;

    public void init() {

        try {

            stage = new Stage();
            stage.initOwner(mainStage);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.sizeToScene();

            AnchorPane anchorPaneAux = new AnchorPane();
            anchorPaneAux.setId("AnchorPane");
            this.getView().getStyleClass().add("bodyComun");
            anchorPaneAux.getChildren().add(this.getView());

            scene = new Scene(anchorPaneAux);
            scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
            subBarraStage(stage, ConstantReportes.nameTituloAvanceEstadoActaCC, 2);

            inicialize = true;
            init_all_objects("CC");
            radioButtonEvent();
            radioButtonCentroC.setSelected(true);
            filtroKeyEvent();
            cmdBotones_Click(0);
            consultaToggleGrp.setUserData(name);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    public void radioButtonEvent() {
        radioButtonCentroC.setUserData("CC");
        radioButtonOdpe.setUserData("ODPE");
        consultaToggleGrp.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (consultaToggleGrp.getSelectedToggle().getUserData().equals("CC")) {
                    inicialize = true;
                    init_all_objects("CC");
                }
                else
                    init_all_objects("");
            }
        });
    }
    
    public void init_all_objects(String paramSeleccion){
        //if (consultaToggleGrp.getSelectedToggle().getUserData().equals("CC")) {
        if (paramSeleccion.equals("CC")) {
            listaCCUnidadSoporte = new ArrayList<>();
            limpiarObjetos();
            paneSeleccion.getChildren().clear();
            listaUnidadSoporte = rcAvanceEstadoActaService.getUnidadSoporte("");
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setSelector(Boolean.TRUE);
            TableCentroComputo.setVisible(Boolean.TRUE);
            resulActaLotizada.setVisible(Boolean.TRUE);
            resulTotalActaC.setVisible(Boolean.TRUE);
            resulTotalActaO.setVisible(Boolean.FALSE);
            critSelecCtrl.setTipoModulo(ConstantReportes.AVANCE_ESTADO_ACTA_X_CENTROCOMPUTO);
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendientes", "Completas"));
            critSelecCtrl.setListaUnidadSoporte(listaUnidadSoporte);
            critSelecCtrl.setListaCCxUnidadSoporte(listaCCUnidadSoporte);
            critSelecCtrl.setShowUnidadSoporte(Boolean.TRUE);
            critSelecCtrl.setShowCCUnidadSoporte(Boolean.TRUE);
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowEstado(Boolean.TRUE);

            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();
            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_UNIDADSOPORTE, critSelecCtrl.getCmbUnidadSoporte(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCCxUnidadSoporte(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADO, critSelecCtrl.getCmbEstado(), null, 0, 3}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
            TableODPE.setVisible(false);
            inicialize = false;

        } else {
            listaCCUnidadSoporte = new ArrayList<>();
            limpiarObjetos();
            paneSeleccion.getChildren().clear();
            listaUnidadSoporte = rcAvanceEstadoActaService.getUnidadSoporte(ConstantReportes.VACIO);
            TableODPE.setVisible(Boolean.TRUE);
            resulTotalActaO.setVisible(Boolean.TRUE);
            resulActaLotizada.setVisible(Boolean.FALSE);
            resulTotalActaC.setVisible(Boolean.FALSE);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.AVANCE_ESTADO_ACTA_X_CENTROCOMPUTO);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendientes", "Completas"));
            critSelecCtrl.setListaUnidadSoporte(listaUnidadSoporte);
            critSelecCtrl.setListaCCxUnidadSoporte(listaCCUnidadSoporte);
            critSelecCtrl.setShowUnidadSoporte(Boolean.TRUE);
            critSelecCtrl.setShowODPEUnidadSoporte(Boolean.TRUE);
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowEstado(Boolean.TRUE);
            TableCentroComputo.setVisible(Boolean.FALSE);

            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();
            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_UNIDADSOPORTE, critSelecCtrl.getCmbUnidadSoporte(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbODPExUnidadSoporte(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADO, critSelecCtrl.getCmbEstado(), null, 0, 3}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
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
    private void btnActualizarOnAction() {
        btnConsultarOnAction();
    }

    @FXML
    private void btnConsultarOnAction() {
        try {
            limpiarObjetos();
            String codigo = ConstantReportes.VACIO;
            String codOdpe = ConstantReportes.VACIO;
            String centroComp = ConstantReportes.VACIO;
            String CENTROCOMPUTO = DatosUsuario.getMstrCentroComputo();
            String ODPE = DatosUsuario.getMstrODPE();
            String tipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String codunidadSoporte = critSelecCtrl.getCmbUnidadSoporte().getSelectionModel().getSelectedItem().getCodigo();
            String Estado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();
            String CodigoEstado = "";

            if (critSelecCtrl.getCmbODPExUnidadSoporte().getSelectionModel().getSelectedItem() != null) {
                codOdpe = critSelecCtrl.getCmbODPExUnidadSoporte().getSelectionModel().getSelectedItem().getCodigo();
            }
            if (critSelecCtrl.getCmbCCxUnidadSoporte().getSelectionModel().getSelectedItem() != null) {
                centroComp = critSelecCtrl.getCmbCCxUnidadSoporte().getSelectionModel().getSelectedItem().getCodigo();
            }

            int Num_Procesadas = 0;
            int Num_PorProcesar = 0;
            int Num_Computadas = 0;

            int Suma_Mesas_Habiles = 0;
            int Suma_Actas_Lotizadas = 0;
            int Suma_Actas_Procesadas = 0;
            int Suma_Actas_Computadas = 0;
            int Suma_Actas_Observadas = 0;

            double Porc_Suma_Actas_Procesadas = 0.0;
            double Porc_Suma_Actas_Computadas = 0.0;
            double Porc_Suma_Actas_Observadas = 0.0;
            
            DecimalFormat decimal_format;
            decimal_format = new DecimalFormat("0.000");

            switch (Estado) {
                case "Todos":
                    CodigoEstado = "";
                    break;

                case "Pendientes":
                    CodigoEstado = "0";
                    break;

                case "Completas":
                    CodigoEstado = "1";
                    break;
            }

            if (consultaToggleGrp.getSelectedToggle().getUserData().equals("CC")) {

                if (!CENTROCOMPUTO.equals(ConstantReportes.TODOS_CCOM_ENUM) && centroComp.equals(ConstantReportes.VACIO)) {
                    codigo = CENTROCOMPUTO;
                } else if (!centroComp.equals(ConstantReportes.VACIO)) {
                    codigo = centroComp;
                } else {
                    codigo = ConstantReportes.VACIO;
                }

                listaObjeto = rcAvanceEstadoActaService.ObtenerAvanceEstadoActaXCC(codunidadSoporte, codigo, tipoEleccion);
                if (listaObjeto == null || listaObjeto.size() < 1) {
                    mostrarMensajeRPT(ConstantCommon.titleInformacion, "Lo Sentimos, No Existen Datos para Mostrar.");
                    return;
                }

                boolean editarListar = false;
                List<CatUnidadSoporte> listaEditada = new ArrayList<>();
                listaEditada.clear();

                if (!CodigoEstado.isEmpty()) {
                    editarListar = true;
                    for (CatUnidadSoporte obj : listaObjeto) {
                        if (obj.getFlag_comp_completa().equals(CodigoEstado) && obj.getFlag_proc_completa().equals(CodigoEstado)) {
                            listaEditada.add(obj);
                        }
                    }

                }

                if (editarListar) {
                    listaObjeto = listaEditada;
                }

                data = FXCollections.observableArrayList(listaObjeto);
                TableCentroComputo.setItems(data);

                codigoC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getCodigo()));
                descripcionC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getDescripcion()));
                TipoC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getC_tipo_cc()));
                ultimaModifC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getFecha_ult_modif()));
                totalActaC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getMesas_habiles()));
                actaLotizadaC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_lotizadas()));
                actaProcesC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_procesadas()));
                totalActaProcC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_procesadas()));
                actaContabC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_computadas()));
                totalActaContC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_computadas()));
                actaObservC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_observadas()));
                totalActaObservC.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_observadas()));
                TableCentroComputo.setRowFactory(new Callback<TableView<CatUnidadSoporte>, TableRow<CatUnidadSoporte>>() {

                    @Override
                    public TableRow<CatUnidadSoporte> call(TableView<CatUnidadSoporte> param) {
                        return new TableRow<CatUnidadSoporte>() {
                            {
                            }

                            @Override
                            protected void updateItem(CatUnidadSoporte item, boolean empty) {
                                super.updateItem(item, empty);
                                if (!empty) {
                                    if (item.getFlag_proc_completa().equals("0")) {
                                        setStyle("-fx-background-color: #FFBF81");//naranja
                                    }
                                    if (item.getFlag_proc_completa().equals("0") && item.getFlag_comp_completa().equals("0")) {
                                        setStyle("-fx-background-color: #FFBF81");//naranja
                                    } else {
                                        if (item.getFlag_comp_completa().equals("1")) {
                                            setStyle("-fx-background-color:  #95C47C"); //verde
                                        } else {
                                            setStyle("-fx-background-color: #FFFFBF"); ////amarillo
                                        }
                                    }

                                }

                            }

                        };
                    }
                });
                for (CatUnidadSoporte result : listaObjeto) {
                    if (result.getFlag_proc_completa().equals(ConstantReportes.COMPLETO)) {
                        Num_Procesadas += result.getFlag_proc_completa().length();
                    } else {
                        Num_PorProcesar += result.getFlag_proc_completa().length();
                    }
                    if (result.getFlag_comp_completa().equals(ConstantReportes.COMPLETO)) {
                        Num_Computadas += result.getFlag_comp_completa().length();
                    }
                    Suma_Mesas_Habiles += Integer.parseInt(result.getMesas_habiles());
                    Suma_Actas_Lotizadas += Integer.parseInt(result.getActas_lotizadas());
                    
                    Suma_Actas_Procesadas += Integer.parseInt(result.getActas_procesadas());
                    Suma_Actas_Computadas += Integer.parseInt(result.getActas_computadas());
                    Suma_Actas_Observadas += Integer.parseInt(result.getActas_observadas());
                }
                
                if (Suma_Mesas_Habiles != 0) {
                    Porc_Suma_Actas_Procesadas = ((double)Suma_Actas_Procesadas / (double)Suma_Mesas_Habiles) * 100;
                    Porc_Suma_Actas_Computadas = ((double)Suma_Actas_Computadas / (double)Suma_Mesas_Habiles) * 100;
                    Porc_Suma_Actas_Observadas = ((double)Suma_Actas_Observadas / (double)Suma_Mesas_Habiles) * 100;
                } else {
                    Porc_Suma_Actas_Procesadas = 0;
                    Porc_Suma_Actas_Computadas = 0;
                    Porc_Suma_Actas_Observadas = 0;
                }

                procesadas.setText(String.valueOf(Num_Procesadas));
                porProcesar.setText(String.valueOf(Num_PorProcesar));
                contabilizadas.setText(String.valueOf(Num_Computadas));
                resulTotalActaC.setText(String.valueOf(Suma_Mesas_Habiles));
                resulActaLotizada.setText(String.valueOf(Suma_Actas_Lotizadas));
                resulActaProces.setText(String.valueOf(Suma_Actas_Procesadas));
                resultActaContab.setText(String.valueOf(Suma_Actas_Computadas));
                resultActaObserv.setText(String.valueOf(Suma_Actas_Observadas));                        
                resulTotalActaProc.setText(decimal_format.format(Porc_Suma_Actas_Procesadas));
                resulTotalActaCont.setText(decimal_format.format(Porc_Suma_Actas_Computadas));
                resulTotalActaObserv.setText(decimal_format.format(Porc_Suma_Actas_Observadas));

            } else {

                if (!ODPE.equals(ConstantReportes.TODOS_ODPE_ENUM) && codOdpe.equals(ConstantReportes.VACIO)) {
                    codigo = ODPE;
                } else if (!codOdpe.equals(ConstantReportes.VACIO)) {
                    codigo = codOdpe;
                } else {
                    codigo = ConstantReportes.VACIO;
                }

                listaObjeto = rcAvanceEstadoActaService.ObtenerAvanceestadoactaxodpe(codunidadSoporte, codigo, tipoEleccion);
                if (listaObjeto == null || listaObjeto.size() < 1) {
                    mostrarMensajeRPT(ConstantCommon.titleInformacion, "Lo Sentimos, No Existen Datos para Mostrar.");
                    return;
                }

                boolean editarListar = false;
                List<CatUnidadSoporte> listaEditada = new ArrayList<>();
                listaEditada.clear();

                if (!CodigoEstado.isEmpty()) {
                    editarListar = true;
                    for (CatUnidadSoporte obj : listaObjeto) {
                        if (obj.getFlag_comp_completa().equals(CodigoEstado) && obj.getFlag_proc_completa().equals(CodigoEstado)) {
                            listaEditada.add(obj);
                        }
                    }

                }

                if (editarListar) {
                    listaObjeto = listaEditada;
                }

                data = FXCollections.observableArrayList(listaObjeto);
                TableODPE.setItems(data);

                codigoO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getCodigo()));
                descripcionO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getDescripcion()));
                ultimaModifO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getFecha_ult_modif()));
                totalActasO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getMesas_habiles()));
                ActaProcesadaO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_procesadas()));
                totalActasProcO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_procesadas()));
                actaContabO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_computadas()));
                totalActaContabO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_computadas()));
                actaObservadasO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getActas_observadas()));
                totalActaObservadasO.setCellValueFactory((TableColumn.CellDataFeatures<CatUnidadSoporte, String> param) -> new SimpleStringProperty(param.getValue().getPorc_observadas()));
                TableODPE.setRowFactory(new Callback<TableView<CatUnidadSoporte>, TableRow<CatUnidadSoporte>>() {

                    @Override
                    public TableRow<CatUnidadSoporte> call(TableView<CatUnidadSoporte> param) {
                        return new TableRow<CatUnidadSoporte>() {
                            {
                            }

                            @Override
                            protected void updateItem(CatUnidadSoporte item, boolean empty) {
                                super.updateItem(item, empty);
                                if (!empty) {
                                    if (item.getFlag_proc_completa().equals("0")) {
                                        setStyle("-fx-background-color: #FFBF81");//naranja
                                    }
                                    if (item.getFlag_proc_completa().equals("0") && item.getFlag_comp_completa().equals("0")) {
                                        setStyle("-fx-background-color: #FFBF81");//naranja
                                    } else {
                                        if (item.getFlag_comp_completa().equals("1")) {
                                            setStyle("-fx-background-color:  #95C47C"); //verde
                                        } else {
                                            setStyle("-fx-background-color: #FFFFBF"); ////amarillo
                                        }
                                    }

                                }

                            }

                        };
                    }
                });

                for (CatUnidadSoporte result : listaObjeto) {
                    if (result.getFlag_proc_completa().equals("1")) {
                        Num_Procesadas += result.getFlag_proc_completa().length();
                    } else {
                        Num_PorProcesar += result.getFlag_proc_completa().length();
                    }
                    if (result.getFlag_comp_completa().equals("1")) {
                        Num_Computadas += result.getFlag_comp_completa().length();
                    }
                    Suma_Mesas_Habiles += Integer.parseInt(result.getMesas_habiles());
                    Suma_Actas_Procesadas += Integer.parseInt(result.getActas_procesadas());
                    Suma_Actas_Computadas += Integer.parseInt(result.getActas_computadas());
                    Suma_Actas_Observadas += Integer.parseInt(result.getActas_observadas());
                }

                if (Suma_Mesas_Habiles != 0) {
                    Porc_Suma_Actas_Procesadas = ((double)Suma_Actas_Procesadas / (double)Suma_Mesas_Habiles) * 100;
                    Porc_Suma_Actas_Computadas = ((double)Suma_Actas_Computadas / (double)Suma_Mesas_Habiles) * 100;
                    Porc_Suma_Actas_Observadas = ((double)Suma_Actas_Observadas / (double)Suma_Mesas_Habiles) * 100;
                } else {
                    Porc_Suma_Actas_Procesadas = 0;
                    Porc_Suma_Actas_Computadas = 0;
                    Porc_Suma_Actas_Observadas = 0;
                }

                procesadas.setText(String.valueOf(Num_Procesadas));
                procesadas.setText(String.valueOf(Num_Procesadas));
                porProcesar.setText(String.valueOf(Num_PorProcesar));
                contabilizadas.setText(String.valueOf(Num_Computadas));
                resulTotalActaO.setText(String.valueOf(Suma_Mesas_Habiles));
                resulActaLotizada.setText(String.valueOf(Suma_Actas_Lotizadas));
                
                resulActaProces.setText(String.valueOf(Suma_Actas_Procesadas));
                resultActaContab.setText(String.valueOf(Suma_Actas_Computadas));
                resultActaObserv.setText(String.valueOf(Suma_Actas_Observadas));
                resulTotalActaProc.setText(decimal_format.format(Porc_Suma_Actas_Procesadas));
                resulTotalActaCont.setText(decimal_format.format(Porc_Suma_Actas_Computadas));
                resulTotalActaObserv.setText(decimal_format.format(Porc_Suma_Actas_Observadas));
            }

            /**
             * VALIDAR EL USO DEL DISTRITO ELECTORAL YA QUE NO ES IMPLEMENTADO,
             * PARA ESTA CASUISTICA.
             */
            if (consultaToggleGrp.getSelectedToggle().getUserData().equals("DE")) {
                rcAvanceEstadoActaService.ObtenerAvanceEstadoActaXDE(codunidadSoporte, codOdpe);
            }

            cmdBotones_Click(1);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    private void btnImprimirOnAction() throws Exception {

        try {

            if (data == null || data.size() < 1) {
                mostrarMensajeRPT(ConstantCommon.titleAviso, "Debe Consultar Antes, No Existen Datos para Mostrar");
                return;
            }

            JasperReportUtil jp = new JasperReportUtil();
            List<CatReporteAvanceEstado> listReport = new ArrayList<>();
            Map<String, Object> parametros = new HashMap<>();
            String nameReport = ConstantReportes.VACIO;
            String url = ConstantReportes.VACIO;
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            String unidadSoporte = critSelecCtrl.getCmbUnidadSoporte().getSelectionModel().getSelectedItem().getDescripcion();
            String estado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();
            String tipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String DescOdpe = ConstantReportes.VACIO;
            String centroComp = ConstantReportes.VACIO;

            if (critSelecCtrl.getCmbCCxUnidadSoporte().getSelectionModel().getSelectedItem() != null) {
                centroComp = critSelecCtrl.getCmbCCxUnidadSoporte().getSelectionModel().getSelectedItem().getDescripcion();
            }
            if (critSelecCtrl.getCmbODPExUnidadSoporte().getSelectionModel().getSelectedItem() != null) {
                DescOdpe = critSelecCtrl.getCmbODPExUnidadSoporte().getSelectionModel().getSelectedItem().getDescripcion();
            }

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("unidadSoporte", unidadSoporte);
            parametros.put("descEstados", estado);
            parametros.put("url_imagen", imagen);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("totalActasLotizadas", resulActaLotizada.getText());
            parametros.put("totalProcesadas", resulActaProces.getText());
            parametros.put("totalporcActaProc", resulTotalActaProc.getText());
            parametros.put("totalActasContab", resultActaContab.getText());
            parametros.put("totalPorcActaContab", resulTotalActaCont.getText());
            parametros.put("totalActaObserv", resultActaObserv.getText());
            parametros.put("totalPorcActaObserv", resulTotalActaObserv.getText());
            parametros.put("tipoEleccion", tipoEleccion);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            // OPCION DE REPORTE 
            if (consultaToggleGrp.getSelectedToggle().getUserData().equals("CC")) {
                nameReport = ConstantReportes.nameReporteAvanceEstadoActas1;
                url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                parametros.put("TipoCombo", ConstantReportes.FILTRO_CENTROCOMPUTO);
                parametros.put("DescriTipoCmb", centroComp);
                parametros.put("totalActas", resulTotalActaC.getText());
                parametros.put("reporte", nameReport);
                parametros.put("tituloSecundario", ConstantReportes.nameTituloAvanceEstadoActaCC);
            } else if (consultaToggleGrp.getSelectedToggle().getUserData().equals("ODPE")) {
                nameReport = ConstantReportes.nameReporteAvanceEstadoActas2;
                url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                parametros.put("TipoCombo", ConstantReportes.FILTRO_ODPE);
                parametros.put("DescriTipoCmb", DescOdpe);
                parametros.put("totalActas", resulTotalActaO.getText());
                parametros.put("reporte", nameReport);
                parametros.put("tituloSecundario", ConstantReportes.nameTituloTituloAvanceEstadoActaODPE);
            } else {
                //Reporte Creado para el caso de Distrito Electoral 
                nameReport = ConstantReportes.nameReporteAvanceEstadoActas3;
                url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
            }

            for (CatUnidadSoporte bean : data) {
                CatReporteAvanceEstado listaReporte = new CatReporteAvanceEstado();
                listaReporte.setCodigo(bean.getCodigo());
                listaReporte.setDescripcion(bean.getDescripcion());
                listaReporte.setFecha_ult_modif(bean.getFecha_ult_modif());
                listaReporte.setActas_computadas(bean.getActas_computadas());
                listaReporte.setActas_lotizadas(bean.getActas_lotizadas());
                listaReporte.setActas_observadas(bean.getActas_observadas());
                listaReporte.setActas_procesadas(bean.getActas_procesadas());
                listaReporte.setMesas_habiles(bean.getMesas_habiles());
                listaReporte.setPorc_computadas(bean.getPorc_computadas());
                listaReporte.setPorc_observadas(bean.getPorc_observadas());
                listaReporte.setPorc_procesadas(bean.getPorc_procesadas());
                listaReporte.setC_tipo_cc(bean.getC_tipo_cc());
                listReport.add(listaReporte);
            }
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(url);
            StackPane sp = jp.reportShow(parametros, listReport, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTituloAvanceEstadoActaCC;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnSalirOnAction() {
        try {
            stage.close();
            TableCentroComputo.getItems().clear();
            TableODPE.getItems().clear();
            limpiarObjetos();
            if (timer != null) {
                System.out.println("Cancelando el timer[1]");
                timer.cancel();
                timer.purge();
            }
            cmdBotones_Click(3);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    public void limpiarObjetos() {
        TableCentroComputo.getItems().clear();
        TableODPE.getItems().clear();
        procesadas.setText(ConstantReportes.VACIO);
        porProcesar.setText(ConstantReportes.VACIO);
        contabilizadas.setText(ConstantReportes.VACIO);
        resulTotalActaC.setText(ConstantReportes.VACIO);
        resulTotalActaO.setText(ConstantReportes.VACIO);
        resulActaLotizada.setText(ConstantReportes.VACIO);
        resulActaProces.setText(ConstantReportes.VACIO);
        resultActaContab.setText(ConstantReportes.VACIO);
        resultActaObserv.setText(ConstantReportes.VACIO);
        resulTotalActaProc.setText(ConstantReportes.VACIO);
        resulTotalActaCont.setText(ConstantReportes.VACIO);
        resulTotalActaObserv.setText(ConstantReportes.VACIO);
        listaObjeto.clear();
        if (data != null) {
            data.clear();
        }

//        critSelecCtrl.getCmbCCxUnidadSoporte().getItems().clear();
//        critSelecCtrl.getCmbODPExUnidadSoporte().getItems().clear();
    }

    public void ejecutarTarea(String minutes) {
        try {
            if (timer != null) {
                System.out.println("Cancelando el timer[1]");
                timer.cancel();
                timer.purge();
            }
        } catch (Exception ex) {
            Logger.getLogger(AvanceEstadoActaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * @method : El Tiempo se mide en nanosegundos.
         */
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
                    Logger.getLogger(AvanceEstadoActaController.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }, period, period);
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Avance Estado de Actas - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Avance Estado de Actas - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Avance Estado de Actas - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Avance Estado de Actas - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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

    public ToggleGroup getConsultaToggleGrp() {
        return consultaToggleGrp;
    }

    public void setConsultaToggleGrp(ToggleGroup consultaToggleGrp) {
        this.consultaToggleGrp = consultaToggleGrp;
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
