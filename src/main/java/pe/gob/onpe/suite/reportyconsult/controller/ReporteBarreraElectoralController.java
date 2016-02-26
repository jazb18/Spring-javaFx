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
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.RcBarreraElectoral;
import pe.gob.onpe.suite.domain.RcReporteBarreraElectoral;
import pe.gob.onpe.suite.reportyconsult.service.RcProcesoService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ReporteBarreraElectoralController extends AppController implements Initializable {
    
    private Stage mainStage, stage;
    private Scene scene;

    /* @FXML
     private Label date;*/
    @FXML
    private AnchorPane anchorPane;
    
    @Autowired
    ApplicationContext context;
    
    CriterioSeleccionController critSelecCtrl;
    @FXML
    private Pane paneSeleccion;
    
    @Autowired
    RcProcesoService procesoService;
    
    @Autowired
    IComunService comunService;
    
    private String name = ConstantCommon.titleReporCons;
    
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
            subBarraStage(stage, ConstantReportes.nameTituloBarreraElectoral, 2);
            
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);
            
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.REPORTE_BARRERA_ELECTORAL);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowDistritoElectoral(Boolean.TRUE);
            critSelecCtrl.setShowEstadoDigitacion(Boolean.TRUE);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();
            
            List<Object[]> listaOpciones = new ArrayList<>();
            
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_AGRUPACION_POLITICA, critSelecCtrl.getCmbEstadoDigitacion(), null, 0, 0},});
            
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);
            
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }
    
    @FXML
    private void btnImprimirOnAction() throws Exception {
        
        try {
            List<RcBarreraElectoral> dataList = new ArrayList<>();
            String TipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String Distrito = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
            String AgrupaPolitica = critSelecCtrl.getCmbEstadoDigitacion().getSelectionModel().getSelectedItem().getCodigo();
            String nameReport = ConstantReportes.VACIO;
            String url = ConstantReportes.VACIO;
            if (Distrito.equals(ConstantReportes.TODOS_DISTELECT_ENUM)) {
                Distrito = ConstantReportes.VACIO;
            }
            if (AgrupaPolitica.equals(ConstantReportes.VACIO)) {
                AgrupaPolitica = ConstantReportes.VACIO;
            }
            switch (TipoEleccion) {
                case "11":
                    nameReport = ConstantReportes.nameReporteBarreraElectoralCN;//"RPT040504_CN";
                    url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                    dataList = procesoService.ObtenerReporteVallaElectoral_CN(Distrito, AgrupaPolitica);
                    break;
                case "12":
                    nameReport = ConstantReportes.nameReporteBarreraElectoralPA;//"RPT040504_PA";
                    dataList = procesoService.ObtenerReporteVallaElectoral_PA(Distrito, AgrupaPolitica);
                    url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                    break;
                default:
                    break;
            }
            
            if (dataList != null || dataList.size() < 1) {
                validador(0);
                return;
            }
            
            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            List<RcReporteBarreraElectoral> listReport = new ArrayList<>();
            
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(url);
            
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloBarreraElectoralActasConstabilizadas);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("tipoEleccion", TipoEleccion);
            parametros.put("reporte", nameReport);
            parametros.put("url_imagen", imagen);
            
            for (RcBarreraElectoral col : dataList) {
                RcReporteBarreraElectoral r = new RcReporteBarreraElectoral();
                
                r.setC_codi_agrupol(col.getC_codi_agrupol());
                r.setC_desc_agrupol(col.getC_desc_agrupol());
                r.setN_votos_validos(Double.parseDouble(col.getN_votos_validos().equals(ConstantReportes.VACIO) ? ConstantReportes.VALORCERO : col.getN_votos_validos()));
                r.setN_porc_votval(Double.parseDouble(col.getN_porc_votval().equals(ConstantReportes.VACIO) ? ConstantReportes.VALORCERO : col.getN_porc_votval()));
                r.setN_escanos_obt(col.getN_escanos_obt());
                r.setNdistelect_obt(col.getNdistelect_obt());
                r.setC_estado_valla(col.getC_estado_valla());
                listReport.add(r);
            }
            
            StackPane sp = jp.reportShow(parametros, listReport, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTituloBarreraElectoral;
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
            critSelecCtrl.getCmbEstadoDigitacion2().setVisible(false);
            cmdBotones_Click(4);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    /**
     * @nota : definir sus usos, en VB unicamente se encuentra seteado en el
     * Reporte.
     */
    private void ObtenerPorcCompCN() {
        List<RcBarreraElectoral> PorCNList = new ArrayList<>();//procesoService.ObtenerPorcCompCN();

        for (RcBarreraElectoral PorCNList1 : PorCNList) {
            Integer porcentaje;
            Integer valor = Integer.parseInt(PorCNList1.getL_actas_a_inst());
            if (valor != null || valor != 0) {
                porcentaje = (Integer.parseInt(PorCNList1.getL_actas_comp()) / Integer.parseInt(PorCNList1.getL_actas_a_inst())) * 100;
            } else {
                porcentaje = 0;
            }
        }
        
    }
    
    private void ObtenerPorcCompPA() {
        List<RcBarreraElectoral> PorPAList = new ArrayList<>(); //procesoService.ObtenerPorcCompPA();

        for (RcBarreraElectoral PorPAList1 : PorPAList) {
            Integer porcentaje;
            Integer valor = Integer.parseInt(PorPAList1.getL_actas_a_inst());
            if (valor != null || valor != 0) {
                porcentaje = (Integer.parseInt(PorPAList1.getL_actas_comp()) / Integer.parseInt(PorPAList1.getL_actas_a_inst())) * 100;
            } else {
                porcentaje = 0;
            }
        }
    }
    
    private void validador(int nro) {
        switch (nro) {
            case 0:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada.!");
                break;
        }
    }
    
    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Barrera Electoral - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Barrera Electoral - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Barrera Electoral - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Barrera Electoral - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
