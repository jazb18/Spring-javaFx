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
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.TiempoDigitacionActa;
import pe.gob.onpe.suite.reportyconsult.service.RcEstadisticaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author JZafra
 */
public class ActasResueltasController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage, stage;
    private Scene scene;

    @Autowired
    IComunService comunService;
    @Autowired
    ApplicationContext context;
    @Autowired
    RcEstadisticaService estadisticaService;

    private String name = ConstantCommon.titleReporCons;
    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane paneSeleccion;

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
            subBarraStage(stage, ConstantReportes.nameActaResueltas, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.ACTA_REDIGITADA);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 0}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
        } catch (Exception e) {
        }
    }

    public void btnImprimirOnAction() throws Exception {
        String descComputo = ConstantReportes.VACIO;
        String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteActaReprocesada + ".jrxml");
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
        JasperReportUtil jp = new JasperReportUtil();
        Map<String, Object> parametros = new HashMap<>();
        List<TiempoDigitacionActa> listReport = new ArrayList<>();
        if (!critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu().equals(ConstantReportes.VACIO)) {
            descComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();
        }
        List<TiempoDigitacionActa> service = estadisticaService.get_actasresueltas(codTipoElec, codComputo);
        System.out.println("service : " + service);

        listReport = service;

        parametros.put("c_desc_compu", descComputo);
        parametros.put("version", DeclaracionComun.gstrVersionSuite);
        parametros.put("reporte", ConstantReportes.nameReporteActaReprocesada);
        parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
        parametros.put("tituloSecundario", ConstantReportes.nameTituloActasCorregidasPorDigitacion);
        parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
        parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
        parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
        parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
        parametros.put("url_imagen", imagen);
        parametros.put("tipoEleccion", codTipoElec);

        StackPane sp = jp.reportShow(parametros, listReport, file, null);
        AppController appController = new AppController();
        String title = ConstantReportes.nameTituloActasCorregidasPorDigitacion;
        appController.subBarraStageReport(mainStage, sp, title, 2);
    }

    public void btnSalirOnAction() {
        stage.close();
        paneSeleccion.getChildren().clear();
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
