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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.COD_MOD_REPCON;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.EXE_MOD_REPCON;
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
 * @author jzafra
 */
public class TiempoDigitacionActaController extends AppController implements Initializable {
    
    private Stage mainStage, stage;
    private Scene scene;
    private String name = ConstantCommon.titleReporCons;
    
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane paneSeleccion;
    
    @Autowired
    IComunService comunService;
    @Autowired
    ApplicationContext context;
    
    CriterioSeleccionController critSelecCtrl;
    @Autowired
    RcEstadisticaService estadisticaService;
    
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
            subBarraStage(stage, ConstantReportes.nameTiempoDigitacionPorActa, 2);
            
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);
            
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.TIEMPO_DIGITACION_ACTA);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.setShowDepartamento(Boolean.TRUE);
            critSelecCtrl.setShowProvincia(Boolean.TRUE);
            critSelecCtrl.setShowDistrito(Boolean.TRUE);
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
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), null, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), null, 4, 3}
            });
            
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }
    
    @FXML
    private void btnImprimirOnAction() {
        
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
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteTiempoDidigtacionxActa + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe

            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            List<TiempoDigitacionActa> listReport = new ArrayList<>();
            
            if (!critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu().equals(ConstantReportes.VACIO)) {
                descComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();
            }
            if (critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().equals(ConstantReportes.VACIO)) {
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Seleccionar un departamento");
                return;
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
            System.out.println(" codTipoElec : " + codTipoElec + " codComputo : " + codComputo + " codOdpe : " + codOdpe + " codUbigueo :" + codUbigueo);
            List<TiempoDigitacionActa> service = estadisticaService.getTiempoDigitacionxActa(codTipoElec, codComputo, codOdpe, codUbigueo);
            if (service == null || service.size() < 1) {
                validador(0);
                return;
            }
            parametros.put("c_desc_compu", descComputo);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("reporte", ConstantReportes.nameReporteTiempoDidigtacionxActa);
            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloSecundario", ConstantReportes.nameTiempoDigitacionPorActa);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("url_imagen", imagen);
            parametros.put("tipoEleccion", codTipoElec);
            
            listReport = service;
            
            StackPane sp = jp.reportShow(parametros, listReport, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTiempoDigitacionPorActa;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(1);
            cmdBotones_Click(2);
            
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
        
    }
    
    @FXML
    private void btnSalirOnAction() {
        try {
            stage.close();
            paneSeleccion.getChildren().clear();
            cmdBotones_Click(3);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
        
    }
    
    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Tiempo de Digitacion de acta  - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Tiempo de Digitacion de acta - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Tiempo de Digitacion de acta - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Tiempo de Digitacion de acta - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
