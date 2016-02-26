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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
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
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatInformacionOficial;
import pe.gob.onpe.suite.domain.CatInformacionOficialData;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcInformacionOficialService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class MonitoreoInformacionOficialController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage;
    private Scene scene;
    private Stage stage;
    private Stage stageMessageBox;

    TableView<CatInformacionOficialData> tableViewConsulta;

    TableColumn<CatInformacionOficialData, String> tableColumUbigeo = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumDepartamento = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumProvincia = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumDistrito = new TableColumn();
    //TableColumn<CatInformacionOficialData, String> tableColumEstado = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumElecHab = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumCiuVot = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumAusentismoCifras = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumParticiparon = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumPart = new TableColumn();
    TableColumn<CatInformacionOficialData, String> tableColumAusent = new TableColumn();

    private ObservableList<CatInformacionOficialData> tableViewConsultaData = FXCollections.observableArrayList();

    List<CatInformacionOficial> listInformofic = new ArrayList<>();

    @Autowired
    ApplicationContext context;
    @Autowired
    IComunService comunService;

    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane paneSeleccion;

    @FXML
    private Pane scrollPaneTableConsult;

    @FXML
    private Label labelHidden;

    @Autowired()
    RcInformacionOficialService rcInformacionOficialService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void init() {
        System.out.println("@@@@[CARGA DEL INICIO DEL CONTROLER]@@@@");
        stage = new Stage();
        stage.initOwner(mainStage);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(350);
        stage.setY(100);
        scene = new Scene(this.getView());
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

        stage.setScene(scene);
        stage.show();
        subBarraStage(stage, ConstantReportes.nameTituloMonitoreoInformacion, 2);
        labelHidden.setVisible(false);

        try {
            /*Carga de los Filtros de Busqueda*/
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            paneSeleccion.getChildren().clear();
            tableViewConsultaData.clear();
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendiente", "Recibido"));
            critSelecCtrl.setShowEstado(true);
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowDepartamento(true);
            critSelecCtrl.setShowProvincia(true);
            critSelecCtrl.setShowDistrito(true);
            critSelecCtrl.setShowODPE(true);
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setTipoModulo(ConstantReportes.MONITOREO_INFORMACION_OFICIAL);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), 200, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), 200, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), 200, 4, 3}
            });
            /*
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADO, critSelecCtrl.getCmbEstado(), null, 0, 4}
            });
            */
            critSelecCtrl.dibujarCombos(listaOpciones);

            paneSeleccion.getChildren().add(critSelecCtrl);

            cargarTabla();
            cmdBotones_Click(0);
        } catch (Exception ex) {
            System.out.println("Excepcion General " + ex);
            ex.printStackTrace();
        }

    }

    /*Metodo para le generacion automatica de la tabla de consulta vacia*/
    public void cargarTabla() {
        tableViewConsulta = new TableView();
        tableViewConsulta.setPrefWidth(1240);
        tableViewConsulta.setPrefHeight(452);
        tableColumUbigeo.setText(ConstantReportes.nameColumnMonitoreoUbigeo);
        tableColumUbigeo.setCellValueFactory(new PropertyValueFactory("ubigeo"));
        tableColumUbigeo.setMinWidth(200);

        tableColumDepartamento.setText(ConstantReportes.nameColumnMonitoreoDepartamentoss);
        tableColumDepartamento.setCellValueFactory(new PropertyValueFactory("departamentos"));
        tableColumDepartamento.setMinWidth(200);

        tableColumProvincia.setText(ConstantReportes.nameColumnMonitoreoProvincia);
        tableColumProvincia.setCellValueFactory(new PropertyValueFactory("provincia"));
        tableColumProvincia.setMinWidth(200);

        tableColumDistrito.setText(ConstantReportes.nameColumnMonitoreoDistrito);
        tableColumDistrito.setCellValueFactory(new PropertyValueFactory("distrito"));
        tableColumDistrito.setMinWidth(200);
        /*
        tableColumEstado.setText(ConstantReportes.nameColumnMonitoreoEstado);
        tableColumEstado.setCellValueFactory(new PropertyValueFactory("estado"));
        tableColumEstado.setMinWidth(200);
        */
        tableColumElecHab.setText(ConstantReportes.nameColumnMonitoreoElectHabiles);
        tableColumElecHab.setCellValueFactory(new PropertyValueFactory("elecHabiles"));
        tableColumElecHab.setMinWidth(200);

        tableColumCiuVot.setText(ConstantReportes.nameColumnMonitoreoCiudVotaron);
        tableColumCiuVot.setCellValueFactory(new PropertyValueFactory("ciudVotaron"));
        tableColumCiuVot.setMinWidth(200);
              
        tableColumAusentismoCifras.setText(ConstantReportes.nameColumnMonitoreoAusentismoCifras);
        tableColumAusentismoCifras.setCellValueFactory(new PropertyValueFactory("ausentismoCifras"));
        tableColumAusentismoCifras.setMinWidth(200);

        tableColumPart.setText(ConstantReportes.nameColumnMonitoreoPorctParticipa);
        tableColumPart.setCellValueFactory(new PropertyValueFactory("porcParticipacion"));
        tableColumPart.setMinWidth(200);

        tableColumAusent.setText(ConstantReportes.nameColumnMonitoreoPorctAusentismo);
        tableColumAusent.setCellValueFactory(new PropertyValueFactory("porcAusentismo"));
        tableColumAusent.setMinWidth(200);
        
        tableViewConsulta.getColumns().addAll(tableColumUbigeo, tableColumDepartamento, tableColumProvincia,
                tableColumDistrito, tableColumElecHab,
                tableColumCiuVot, tableColumAusentismoCifras, tableColumPart, tableColumAusent);

        scrollPaneTableConsult.getChildren().add(tableViewConsulta);

    }

    @FXML
    private void btnSalirOnAction() {
        String logg = "[MonitoreoInformacionOficialController : btnSalirOnAction ]";
        System.out.println(logg + " Cerrando el modulo ");
        cmdBotones_Click(3);
        stage.close();
    }

    @FXML
    private void btnImprimirOnAction() {
        try {
            System.out.println("Cantidad de registros en la tabla :" + tableViewConsultaData.size());
            if (tableViewConsultaData.size() == 0) {

                EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                    stageMessageBox.close();
                    stage.requestFocus();
                };
                stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, ConstantCommon.titleValidacion, ConstantReportes.MSJ_NODATA, null, null, null, eventOK);
                
                return;
            }
            labelHidden.setVisible(false);
            labelHidden.setText("");
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantReportes.pathReportConsult + ConstantReportes.nameReporteMonitoreoInformacion + ".jrxml");
            String title = ConstantReportes.nameTituloMonitoreoInformacion;

            Map<String, Object> parametros = new HashMap<>();
            cargaParametros(parametros);

            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listInformofic, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoInformacionOficialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btnConsultarOnAction() {

        ParametrosInformacionOficial paramInfOfi = getParamInformacionOficial();
        tableViewConsultaData.removeAll(tableViewConsultaData);

        System.out.println("Parametros oficiales : " + paramInfOfi);
        listInformofic.clear();
        System.out.println("TIPO ELECCION : " + paramInfOfi.getTipoElec());
        System.out.println("COD COMPUTO   : " + paramInfOfi.getCodComputo());
        System.out.println("COD ODPE      : " + paramInfOfi.getCodOdpe());
        System.out.println("COD UBIGEO    : " + paramInfOfi.getCodUbigeo());
        System.out.println("ESTADO        : " + paramInfOfi.getEstado());
        listInformofic = rcInformacionOficialService.getMonitoreoInformacion(paramInfOfi.getTipoElec(), paramInfOfi.getCodComputo(),
                paramInfOfi.getCodOdpe(), paramInfOfi.getCodUbigeo(), paramInfOfi.getEstado());

        if (listInformofic.size() < 1) {

            EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                stageMessageBox.close();
                stage.requestFocus();
            };
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, ConstantCommon.titleValidacion, ConstantReportes.MSJ_NODATA, null, null, null, eventOK);
            
            tableViewConsulta.setPlaceholder(new Label(""));
            return;

        } else {
            labelHidden.setVisible(false);
            labelHidden.setText("");
        }

        tableViewConsulta.setEditable(true);
        tableViewConsulta.setItems(gerInformacionOficialData(listInformofic));
        cmdBotones_Click(1);

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

    /*Metodo que me retorna la lista de valores a mostrar en la tabla de consulta
     * AUTOR : kleon
     * INPUT : lista de tipo CatInformacionOficial
     * OUPUT : lista de tipo CatInformacionOficialData
     */
    private ObservableList<CatInformacionOficialData> gerInformacionOficialData(List<CatInformacionOficial> listInformacionOficial) {

        System.out.println("@@@@Cantidad de archivos recuperados : " + listInformacionOficial.size());

        /*Agregando los valores recuperados a la lista , para mostrar en la tabla */
        for (CatInformacionOficial beanCatInformacionOficial : listInformacionOficial) {
            tableViewConsultaData.add(new CatInformacionOficialData(beanCatInformacionOficial));

        }

        return tableViewConsultaData;
    }

    private ParametrosInformacionOficial getParamInformacionOficial() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();

        String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
        String codCentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        String estado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();
        String tipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();

        if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().equals("")) {
            String codDepartamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento();
            if (!codDepartamento.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDEPARTAMENTO).equals("")) {
                paramIO.setCodUbigeo(codDepartamento.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDEPARTAMENTO));

            }

        }

        if (!critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia().equals("")) {
            String codProvincia = critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia();
            if (!codProvincia.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODPROVINCIA).equals("")) {
                paramIO.setCodUbigeo(codProvincia.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODPROVINCIA));

            }

        }

        if (!critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito().equals("")) {
            String codDistrito = critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito();
            if (!codDistrito.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDISTRITO).equals("")) {
                paramIO.setCodUbigeo(codDistrito.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDISTRITO));

            }
        }

        paramIO.setEstado((estado.substring(0, ConstantReportes.LONGITUD_ESTADO)).toUpperCase());
        paramIO.setCodOdpe(codOdpe.substring(0, ConstantReportes.LONGITUD_COD_ODP));
        paramIO.setCodComputo(codCentroComputo.substring(0, ConstantReportes.LONGITUD_CENTROCOMPUTO));
        paramIO.setTipoElec(tipoEleccion.substring(0, ConstantReportes.LONGITUD_TIPO_ELECCION));

        return paramIO;
    }

    public void mostrarMensaje() {
        labelHidden.setVisible(true);
        labelHidden.setText("");
        labelHidden.setText(ConstantReportes.MSJ_NODATA);
        labelHidden.setTextAlignment(TextAlignment.CENTER);
        labelHidden.setTextFill(Color.web("#f51818"));
        /*Fade del texto de validacion de listados*/
        fadeIn.setNode(labelHidden);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);
        fadeIn.playFromStart();
    }

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(3000)
    );

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion + "\n" + critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());

            parametros.put("tituloSecundario", ConstantReportes.nameTituloMonitoreoInformacion);

            parametros.put("estado", critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem());
            parametros.put("depar", critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento() : critSelecCtrl.getCmbDepa().getPromptText());
            parametros.put("prov", critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getDescProvincia() : critSelecCtrl.getCmbProv().getPromptText());
            parametros.put("dist", critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getDescDistrito() : critSelecCtrl.getCmbDistrito().getPromptText());

            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());

            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("reporte", ConstantReportes.nameReporteMonitoreoInformacion);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
            return parametros;
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoInformacionOficialController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Monitoreo de Informacion Oficial- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
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
