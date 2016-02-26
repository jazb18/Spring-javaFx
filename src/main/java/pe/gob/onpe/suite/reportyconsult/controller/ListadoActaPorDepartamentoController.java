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
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatListadoActasDepartamento;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcListadoActasDepartamentoService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ListadoActaPorDepartamentoController extends AppController implements Initializable {

    private Stage mainStage;
    private Scene scene;
    private Stage stage;

    @Autowired
    public ApplicationContext context;

    @Autowired
    IComunService comunService;

    CriterioSeleccionController critSelecCtrl;
    List<CatListadoActasDepartamento> listActasDepartamento = new ArrayList<>();

    @Autowired
    RcListadoActasDepartamentoService rcListadoActasDepartamentoService;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label lblCabecera;
    @FXML
    private Label labelHidden;

    @FXML
    private Pane paneSelection;

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
        subBarraStage(stage, "Listado de Actas Por Departamento", 2);
        try {

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            paneSelection.getChildren().removeAll(critSelecCtrl);
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setTipoModulo(ConstantReportes.LISTADO_ACTASPOR_DEPARTAMENTO);
            critSelecCtrl.setShowDepartamento(true);
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowEstado(true);
            critSelecCtrl.setListaEstado(Arrays.asList("0 NO PROCESADA", "1 PROCESADA", "2 OBSERVADA", "3 RESUELTA"));
            critSelecCtrl.cargarCombos();

            //Lista de los parametros de Filtros 
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0}});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 0}});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOESTADO, critSelecCtrl.getCmbEstado(), null, 0, 0}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            critSelecCtrl.setDefaultStyleCss();
            critSelecCtrl.setLayoutX(25);
            critSelecCtrl.setLayoutY(20);
            paneSelection.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
        }
    }

    @FXML
    private void btnConsultarOnAction() {
    }

    @FXML
    private void btnImprimirOnAction() {
        ParametrosInformacionOficial paramInfOfi = getParamListadoActas();
        try {
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantReportes.pathReportConsult + ConstantReportes.nameReporteListadoActasDepartamento + ".jrxml");
            listActasDepartamento.clear();
            listActasDepartamento = rcListadoActasDepartamentoService.getListadoActasDepartamento(paramInfOfi.getCodComputo(), paramInfOfi.getCodUbigeo(), paramInfOfi.getEstado(), paramInfOfi.getTipoElec());
            System.out.println("Listado de Actas por Depat: " + listActasDepartamento.size());

            if (listActasDepartamento != null) {
                if (listActasDepartamento.size() < 1) {
                    validador(0);
                    return;
                }
            }
            labelHidden.setVisible(false);
            labelHidden.setText("");

            Map<String, Object> parametros = new HashMap<>();
            String titulo = ConstantReportes.nameTituloListadoActaPorDepartamento;
            cargaParametros(parametros);

            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listActasDepartamento, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, titulo, 2);
            cmdBotones_Click(2);
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
        }

    }

    @FXML
    private void btnSalirOnAction() {
        labelHidden.setVisible(false);
        labelHidden.setText("");
        stage.close();
        cmdBotones_Click(3);
    }

    private ParametrosInformacionOficial getParamListadoActas() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();
        String codCentroComputo = DatosUsuario.getMstrCentroComputo();
        String codDepartamento = "000000";

        String codEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        String estado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();

        if (!critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu().equals("<Todos>")) {
            codCentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        }
        if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento().equals("<Todos>")) {
            codDepartamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento();
        }

        paramIO.setCodComputo(codCentroComputo.substring(0, ConstantReportes.LONGITUD_CENTROCOMPUTO));
        paramIO.setEstado(estado.substring(0, ConstantReportes.LONGITUD_ESTADO));
        paramIO.setTipoElec(codEleccion);
        paramIO.setCodUbigeo(codDepartamento);

        return paramIO;

    }

    private void cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {

            String estadoActas = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem().substring(2);

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion + "\n" + critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("tituloSecundario", ConstantReportes.nameTituloResumenDeActas);

            parametros.put("estadoActas", estadoActas);

            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());

            parametros.put("reporte", ConstantReportes.nameReporteListadoActasDepartamento);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);

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

    public void setLblCabecera(String lblCabeceraString) {

        String strHeader = "LISTADO DE ACTAS POR DEPARTAMENTO -";
        lblCabecera.setText("");
        System.out.println("Stylo de la cabecera : " + lblCabecera.getStyleClass().toString());
        lblCabecera.setText(strHeader + " " + lblCabeceraString);
    }

    private void validador(int nro) {
        switch (nro) {
            case 0:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe información para la consulta realizada.!");
                break;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Listado de Actas por Departamento - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Listado de Actas por Departamento - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Listado de Actas por Departamento - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Listado de Actas por Departamento- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
        }
    }

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(3000)
    );

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

}
