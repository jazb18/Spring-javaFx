/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.RcProbableCandidatoElecto;
import pe.gob.onpe.suite.domain.RcProbableCandidatoElectoReporte;
import pe.gob.onpe.suite.reportyconsult.service.RcProbablesCandidatosElectosService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ProbablesCandidatosElectosController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage, stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorPane;

    @Autowired
    ApplicationContext context;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    IComunService comunService;

    @FXML
    private Pane paneSeleccion;

    @Autowired
    RcProbablesCandidatosElectosService ctrlProbCandElectoService;

    @FXML
    TableView<RcProbableCandidatoElecto> tablaPrincipal;

    ObservableList<RcProbableCandidatoElecto> data;

    private String name = ConstantCommon.titleReporCons;

    @FXML
    private TableColumn<RcProbableCandidatoElecto, String> dElectoral, oPolitica, nCandidato, dIdentidad, vPreferencial, ordOriginal, ordObtenido, estado, cargo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

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
            subBarraStage(stage, ConstantReportes.nameTituloProbablesCandidatosElectos, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.PROBABLES_CANDIDATOS_ELECTOS);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowDistritoElectoral(Boolean.TRUE);
            critSelecCtrl.setShowDepartamento(Boolean.TRUE);
            critSelecCtrl.setShowEstadoDigitacion(Boolean.TRUE);

            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CARGO, critSelecCtrl.getCmbDepa(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_AGRUPACION_POLITICA, critSelecCtrl.getCmbEstadoDigitacion(), null, 0, 0},});

            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    public void btnConsultarOnAction() {

        try {
            String TipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String DistritoE = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
            String Cargo = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento();
            String OrgPolitica = critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getCodigo();
            if (DistritoE.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
                DistritoE = ConstantReportes.VACIO;
            }

            List<RcProbableCandidatoElecto> ListaObtenida = ctrlProbCandElectoService.obtenerConsulta(TipoEleccion, DistritoE, Cargo, OrgPolitica);

            if (ListaObtenida == null || ListaObtenida.size() < 1) {
                validador(1);
                return;
            }

            dElectoral.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getC_desc_dist_elec()));
            oPolitica.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getC_desc_agrupol()));
            nCandidato.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getNombre_electo()));
            dIdentidad.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getC_numero_dni()));
            vPreferencial.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getN_votos_validos()));
            ordOriginal.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getN_orden_cand()));
            ordObtenido.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getN_orden_obt()));
            estado.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> new SimpleStringProperty(param.getValue().getC_estado_cand()));
            cargo.setCellValueFactory((TableColumn.CellDataFeatures<RcProbableCandidatoElecto, String> param) -> {
                String Estados = param.getValue().getC_estado_cand();
                String Observacion = ConstantReportes.VACIO;
                if (Estados.equals(ConstantReportes.ESTADO_AGRUPOL) && TipoEleccion.equals(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO)) {
                    Observacion = param.getValue().getC_observacion();
                } else if (Estados.equals(ConstantReportes.ESTADO_AGRUPOL_PERDIO_SORTEO)) {
                    Observacion = ConstantReportes.VACIO;
                } else {
                    Observacion = param.getValue().getC_cargo_cand();
                }
                return new SimpleStringProperty(Observacion);
            });

            data = FXCollections.observableArrayList(ListaObtenida);
            tablaPrincipal.setItems(data);
            cmdBotones_Click(1);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    public void btnImprimirOnAction() throws Exception {
        try {
            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            List<RcProbableCandidatoElectoReporte> listReport = new ArrayList<>();
            String TipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteProbableCandidatoElecto + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe

            if (data == null || data.size() < 1) {
                validador(2);
                return;
            }
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloProbablesCandidatosElectos);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("tipoEleccion", TipoEleccion);
            parametros.put("reporte", ConstantReportes.nameReporteProbableCandidatoElecto);
            parametros.put("url_imagen", imagen);

            data.stream().forEach((RcProbableCandidatoElecto r) -> {
                //RPT040502
                RcProbableCandidatoElectoReporte reporte = new RcProbableCandidatoElectoReporte();

                reporte.setC_cargo_cand(r.getC_cargo_cand());
                reporte.setC_desc_agrupol(r.getC_desc_agrupol());
                reporte.setC_desc_dist_elec(r.getC_desc_dist_elec());
                reporte.setC_dist_elec(r.getC_desc_dist_elec());
                reporte.setC_estado_cand(r.getC_estado_cand());
                reporte.setC_numero_dni(r.getC_numero_dni());
                reporte.setC_observacion(r.getC_numero_dni());
                reporte.setEscanos_obt(r.getEscanos_obt());
                reporte.setN_orden_cand(r.getN_orden_cand());
                reporte.setN_orden_obt(r.getN_orden_obt());
                reporte.setN_votos_validos(r.getN_votos_validos());
                reporte.setNombre_electo(r.getNombre_electo());
                reporte.setVotos_agrup(r.getVotos_agrup());

                listReport.add(reporte);
            });

            StackPane sp = jp.reportShow(parametros, listReport, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTituloProbablesCandidatosElectos;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    public void btnSalirOnAction() {
        try {
            paneSeleccion.getChildren().clear();
            stage.close();
            cmdBotones_Click(3);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    private void validador(int nro) {
        switch (nro) {
            case 1:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe información para la consulta realizada.!");
                break;
            case 2:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Consultar Antes de imprimir, No Existen Datos para Imprimir.");
                break;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Probables Candidatos Electos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Probables Candidatos Electos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Probables Candidatos Electos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Probables Candidatos Electos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
