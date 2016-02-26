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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.COD_MOD_REPCON;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.EXE_MOD_REPCON;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatAvanceActasXDeparatamento;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RcAvanceActasXDepartamentoService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author kleon
 */
public class ResumenAvanceActasContabilizadasController extends AppController implements Initializable {

    private Stage mainStage;
    private Scene scene;
    private Stage stage;
    private String lblDepartamento = "Departamento :";
    GridPane gridSeleccion = new GridPane();

    @Autowired
    ApplicationContext context;

    List<CatAvanceActasXDeparatamento> listAvanceActasXDeparatamento = new ArrayList<>();

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    RcAvanceActasXDepartamentoService rcAvanceActasXDepartamentoService;

    @Autowired
    IComunService comunService;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane panelSeleccion;

    private String name = ConstantCommon.titleReporCons;

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
        subBarraStage(stage, ConstantReportes.nameTituloResumenAvanceActas, 2);

        try {
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowDepartamento(Boolean.TRUE);
            critSelecCtrl.setTipoModulo(ConstantReportes.RESUMEN_AVANCEACTAS_CONTABILIZADAS);
            critSelecCtrl.cargarCombos();

            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 0}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            critSelecCtrl.setDefaultStyleCss();
            panelSeleccion.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);

        } catch (Exception ex) {
            comunService.determinarException(mainStage, ex, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnConsultarOnAction() {
    }

    @FXML
    private void btnImprimirOnAction() {

        try {
            ParametrosInformacionOficial paramInfOfi = getParamListadoActas();

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantReportes.pathReportConsult + ConstantReportes.nameReporteAvanceActasContabilizadas + ".jrxml");

            listAvanceActasXDeparatamento.clear();
            listAvanceActasXDeparatamento = rcAvanceActasXDepartamentoService.getAvanceActasXDepartamentos(paramInfOfi.getTipoElec(), paramInfOfi.getCodUbigeo());
            System.out.println("Listado de Actas por Depat: " + listAvanceActasXDeparatamento.size());

            if (listAvanceActasXDeparatamento != null) {
                if (listAvanceActasXDeparatamento.size() < 1) {
                    System.out.println("El listado de actas es vacio");
                    validador(0);
                    return;
                }
            }

            Map<String, Object> parametros = new HashMap<>();
            String titulo = ConstantReportes.nameTituloMonitInfEleccionesGenerales;
            cargaParametros(parametros);

            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listAvanceActasXDeparatamento, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, titulo, 2);
            cmdBotones_Click(2);

        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);
        }

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

    private ParametrosInformacionOficial getParamListadoActas() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();
        String tipoEleccion = "";
        String codDepartamento = "";

        if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento().equals("<Todos>")) {
            codDepartamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento();
        }
        if (!critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion().equals("<Todos>")) {
            tipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        }

        paramIO.setTipoElec(tipoEleccion);
        paramIO.setCodUbigeo(codDepartamento);

        return paramIO;

    }

    private void cargaParametros(Map<String, Object> parametros) {

        try {
            String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("tipoEleccion", codTipoElec);
            parametros.put("reporte", ConstantReportes.nameReporteAvanceActasContabilizadas);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);

        } catch (Exception ex) {
            System.out.println("Excepcion General :" + ex);

        }

    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resumen de Avance de Actas contab. - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resumen de Avance de Actas contab. - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resumen de Avance de Actas contab. - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resumen de Avance de Actas contab.- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
        }
    }

    private void validador(int nro) {
        switch (nro) {
            case 0:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe información para la consulta realizada.!");
                break;
        }
    }

    public String getLblDepartamento() {
        return lblDepartamento;
    }

    public void setLblDepartamento(String lblDepartamento) {
        this.lblDepartamento = lblDepartamento;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
