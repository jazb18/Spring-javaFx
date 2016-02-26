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
import javafx.scene.control.CheckBox;
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
import pe.gob.onpe.suite.domain.RcEstadoActaDigitacion;
import pe.gob.onpe.suite.domain.RcListadoMesasxEstadoActa;
import pe.gob.onpe.suite.domain.RcMesaEstadoMesaReporte;
import pe.gob.onpe.suite.reportyconsult.service.RcMesaEstadoDigitacionService;
import pe.gob.onpe.suite.reportyconsult.service.RcMesaEstadoMesaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class MesasEstadoDigitacionController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;

    /*@FXML
     private Label date;*/
    @FXML
    private AnchorPane anchorPane;

    @Autowired
    ApplicationContext context;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    IComunService comunService;

    @FXML
    private Pane paneSeleccion;
    private String name = ConstantCommon.titleReporCons;

    @Autowired
    RcMesaEstadoMesaService rcMesaEstadoMesaService;

    @Autowired
    RcMesaEstadoDigitacionService rcMesaEstadoDigitacionService;
    CheckBox cb1;

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
            subBarraStage(stage, ConstantReportes.nameTituloMesaPorDigitacion, 2);

            List<RcEstadoActaDigitacion> estadosDigitacion = rcMesaEstadoMesaService.getObtenerEstadosActa_Digitacion("AD");
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setListaEstadoDigitacion(estadosDigitacion);
            critSelecCtrl.setTipoModulo(ConstantReportes.MESA_ESTADO_MESA);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowProvincia(Boolean.TRUE);
            critSelecCtrl.setShowDistrito(Boolean.TRUE);
            critSelecCtrl.setShowDepartamento(Boolean.TRUE);
            critSelecCtrl.setShowEstadoDigitacion(Boolean.TRUE);
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
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADOS_DIGITACION_DIGITACION, critSelecCtrl.getCmbEstadoDigitacion(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), null, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), null, 4, 3}
            });

            cb1 = new CheckBox("A Pantalla");
            cb1.setLayoutX(750);
            cb1.setSelected(Boolean.TRUE);
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().addAll(critSelecCtrl, cb1);
            cmdBotones_Click(0);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnImprimirOnAction() throws Exception {

        try {
            String codUbigueo = ConstantReportes.VACIO;
            String codDpto = ConstantReportes.VACIO;
            String codProvincia = ConstantReportes.VACIO;
            String codDistrto = ConstantReportes.VACIO;
            String descOdpe = ConstantReportes.VACIO;
            String descComputo = ConstantReportes.VACIO;
            String descEstado = ConstantReportes.VACIO;
            String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
            String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
            String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String codEstado = critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getCodigo();
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteMesaEstaDigitacion + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe

            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            List<RcMesaEstadoMesaReporte> listReport = new ArrayList<>();

            if (!critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe().equals(ConstantReportes.VACIO)) {
                descOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe();
            }
            if (!critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu().equals(ConstantReportes.VACIO)) {
                descComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
            }
            if (!critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getCodigo().equals(ConstantReportes.VACIO)) {
                descEstado = critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getDescripcion();
            }
            if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec().equals(ConstantReportes.VACIO)) {
                codTipoElec = ConstantReportes.VACIO;
            }
            if (critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().equals(ConstantReportes.VACIO)) {
                codDpto = ConstantReportes.VACIO;
            } else {

                if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().substring(0, 2).equals(ConstantReportes.VACIO)) {
                    codDpto = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().substring(0, 2);
                    codUbigueo = codDpto;
                }
                if (!critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia().equals(ConstantReportes.VACIO)) {
                    codProvincia = critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia().substring(0, 4);
                    codUbigueo = codProvincia;
                }
                if (!critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito().equals(ConstantReportes.VACIO)) {
                    codDistrto = critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito();
                    codUbigueo = codDistrto;
                }
            }

            System.out.println(" codOdpe : " + codOdpe + " codComputo : " + codComputo + " codEstado : " + codEstado + " codUbigueo :" + codUbigueo + " codTipoElec :" + codTipoElec);
            List<RcListadoMesasxEstadoActa> ListMesasEstadoDigitacion = rcMesaEstadoDigitacionService.getListadomesasxestadodigita(codOdpe, codComputo, codEstado, codUbigueo, codTipoElec);

            if (ListMesasEstadoDigitacion == null || ListMesasEstadoDigitacion.size() < 1) {
                validador(0);
                return;
            }

            parametros.put("descOdpe", descOdpe);
            parametros.put("descCompu", descComputo);
            parametros.put("estadoMesa", descEstado);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("reporte", ConstantReportes.nameReporteMesaEstaDigitacion);
            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloListadodeMesaEstadoDigitacion);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("url_imagen", imagen);
            parametros.put("tipoEleccion", codTipoElec);

            for (RcListadoMesasxEstadoActa ListData : ListMesasEstadoDigitacion) {
                RcMesaEstadoMesaReporte r = new RcMesaEstadoMesaReporte();
                r.setDepartamento(ListData.getC_desc_departamento());
                r.setProvincia(ListData.getC_desc_provincia());
                r.setDistrito(ListData.getC_desc_distrito());
                r.setCnume_mesa(Integer.parseInt(ListData.getC_nume_acta()));
                r.setC_nume_acta(ListData.getC_nume_acta());
                r.setC_codi_local(ListData.getC_codi_local());
                r.setC_nombre_local(ListData.getC_nombre_local());
                r.setC_direccion_local(ListData.getC_direccion_local());
                r.setC_estado_mesa(ListData.getC_estado_acta());
                r.setN_elec_habil(Integer.parseInt(ListData.getN_elec_habil()));
                listReport.add(r);
            }

            if (cb1.isSelected()) {
                StackPane sp = jp.reportShow(parametros, listReport, file, null);
                AppController appController = new AppController();
                String title = ConstantReportes.nameTituloMesaPorDigitacion;
                appController.subBarraStageReport(mainStage, sp, title, 2);
            } else {
                String nameReport = "MesaEstadoDigitacion";
                String url = "D:/temp/report/REPCON/";
                jp.mkdir(url);
                jp.generatePDFReport(parametros, listReport, file, url + nameReport + ".pdf");
                jp.imprimirDefault(url + nameReport + ".pdf");
//                jp.generatePDFReport(parametros, listReport, file, "D:\\reporte\\" + "archivo.pdf");
//                jp.imprimirDefault("D:\\reporte\\" + "archivo.pdf");
            }

            cmdBotones_Click(1);
            cmdBotones_Click(2);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Mesa por Estado de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Mesa por Estado de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Mesa por Estado de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Mesa por Estado de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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

    @FXML
    private void btnSalirOnAction() {
        try {
            paneSeleccion.getChildren().clear();
            stage.close();
            cmdBotones_Click(3);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
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
