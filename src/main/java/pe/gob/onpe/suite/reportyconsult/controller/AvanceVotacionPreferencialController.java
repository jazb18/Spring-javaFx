/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import com.sun.javafx.scene.control.skin.VirtualFlow;
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
import pe.gob.onpe.suite.domain.AvanceVotacionPreferencial;
import pe.gob.onpe.suite.domain.CatLocal;
import pe.gob.onpe.suite.domain.RcEstadoActaDigitacion;
import pe.gob.onpe.suite.reportyconsult.service.RcAvanceVotacionPreferencialService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author JZafra
 */
public class AvanceVotacionPreferencialController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;

    @Autowired
    ApplicationContext context;

    @FXML
    private AnchorPane anchorPane;

    @Autowired
    IComunService comunService;

    @Autowired
    RcAvanceVotacionPreferencialService service;

    private String name = ConstantCommon.titleReporCons;
    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane panelSeleccion;

    @Autowired
    RcAvanceVotacionPreferencialService preferencialService;

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

        subBarraStage(stage, ConstantReportes.nameTituloVotoPreferencialxCandidato, 2);

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
        beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
        BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
        beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);
        critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
        critSelecCtrl.setTipoModulo(ConstantReportes.AVANCE_VOTACION_PREFERENCIAL);
        critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
        critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
        critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
        critSelecCtrl.setShowDistritoElectoral(Boolean.TRUE);
        critSelecCtrl.setShowEstadoDigitacion(Boolean.TRUE);
        critSelecCtrl.setShowLocal(Boolean.TRUE);
        critSelecCtrl.cargarCombos();

        List<Object[]> listaOpciones = new ArrayList<>();
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0}
        });
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 0}
        });
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), null, 0, 0}
        });
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_AGRUPACION_POLITICA, critSelecCtrl.getCmbEstadoDigitacion(), null, 0, 0}
        });
        listaOpciones.add(new Object[]{
            new Object[]{ConstantReportes.FILTRO_CANDIDATOS, critSelecCtrl.getCmbLocVotacion(), null, 0, 0}
        });

        critSelecCtrl.dibujarCombos(listaOpciones);
        critSelecCtrl.setDefaultStyleCss();
        panelSeleccion.getChildren().add(critSelecCtrl);
    }

    public void btnImprimirOnAction() throws Exception {

        String TipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String CentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        String DistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
        String AgrupacionPolitica = critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getCodigo();
        String CandidatoElecto = critSelecCtrl.getCmbLocVotacion().getSelectionModel().getSelectedItem().getCodiLocal();
        //955 764 165 liana garcia
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteAvanceVotacionPreferencial + ".jrxml");
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
        String descComputo = null;

        JasperReportUtil jp = new JasperReportUtil();
        Map<String, Object> parametros = new HashMap<>();
        List<AvanceVotacionPreferencial> listReport = new ArrayList<>();
        List<AvanceVotacionPreferencial> ListaAvance = new ArrayList<>();
        if (!critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu().equals(ConstantReportes.VACIO)) {
            descComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();
        }

        parametros.put("descCompu", descComputo);
        parametros.put("version", DeclaracionComun.gstrVersionSuite);
        parametros.put("reporte", ConstantReportes.nameReporteAvanceVotacionPreferencial);
        parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
        parametros.put("tituloSecundario", ConstantReportes.nameTituloVotoPreferencialxCandidato);
        parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
        parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
        parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
        parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
        parametros.put("url_imagen", imagen);
        parametros.put("tipoEleccion", TipoEleccion);

        System.out.println(" TipoEleccion : " + TipoEleccion + "\n" + " CentroComputo : " + CentroComputo + "\n" + " DistritoElectoral : " + DistritoElectoral + "\n" + " AgrupacionPolitica : " + AgrupacionPolitica + "\n" + " CandidatoElecto : " + CandidatoElecto);
        
        if (TipoEleccion.equals("11")) {
            ListaAvance = preferencialService.getListAvanceVotacionxCandidatoCN(CentroComputo, TipoEleccion, DistritoElectoral, AgrupacionPolitica, CandidatoElecto, "");
        } else {
            ListaAvance = preferencialService.getListAvanceVotacionxCandidatoPA(CentroComputo, TipoEleccion, DistritoElectoral, AgrupacionPolitica, CandidatoElecto, "");
        }
        
        listReport = ListaAvance;

        StackPane sp = jp.reportShow(parametros, listReport, file, null);
        AppController appController = new AppController();
        String title = ConstantReportes.nameTituloVotoPreferencialxCandidato;
        appController.subBarraStageReport(mainStage, sp, title, 2);
    }

    public void btnSalirOnAction() {
        stage.close();
        panelSeleccion.getChildren().clear();
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
