/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.COD_MOD_REPCON;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.EXE_MOD_REPCON;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.TotalActaObservada;
import pe.gob.onpe.suite.reportyconsult.service.RcTotalActaObservadaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author JZafra
 */
public class TotalActaObservadaCentroComputoController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;

    @Autowired
    ApplicationContext context;

    @FXML
    private AnchorPane anchorPane;

    @Autowired
    IComunService comunService;

    private String name = ConstantCommon.titleReporCons;
    CriterioSeleccionController critSelecCtrl;
    @FXML
    private AnchorPane panelSeleccion;

    @Autowired
    RcTotalActaObservadaService rcTotalActaObservadaService;

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
            anchorPaneAux.getStyleClass().add("bodyComun");
            anchorPaneAux.getChildren().add(this.getView());

            scene = new Scene(anchorPaneAux);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

            stage.setScene(scene);
            stage.show();
            subBarraStage(stage, ConstantReportes.nameTituloTotActaObsXCC, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            critSelecCtrl.setTipoModulo(ConstantReportes.TOTAL_ACTA_OBSERVADA);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setListaEstado(Arrays.asList("DATA ACTUAL", "DATA HISTÓRICA"));
            critSelecCtrl.setListaConexion(Arrays.asList("POR LOCAL DE VOTACIÓN", "POR CENTRO DE CÓMPUTO"));
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowODPE(Boolean.TRUE);
            critSelecCtrl.setShowCentroComputo(Boolean.TRUE);
            critSelecCtrl.setShowEstado(Boolean.TRUE);
            critSelecCtrl.setShowTipoConexion(Boolean.TRUE);
            critSelecCtrl.cargarCombos();

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOCONSULTA, critSelecCtrl.getCmbEstado(), null, 0, 3}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_AGRUPACION, critSelecCtrl.getCmbTipoConexion(), null, 0, 3}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            critSelecCtrl.setDefaultStyleCss();
            panelSeleccion.getChildren().add(critSelecCtrl);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    public void btnImprimirOnAction() throws Exception {

        String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
        String descrOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe();
        String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        String descrComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();
        String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
        int TipoConsulta = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedIndex();
        int Agrupacion = critSelecCtrl.getCmbTipoConexion().getSelectionModel().getSelectedIndex();
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
        List<TotalActaObservada> service = null;
        String nameReport = null;
        String url = null;

        JasperReportUtil jp = new JasperReportUtil();
        Map<String, Object> parametros = new HashMap<>();
        List<TotalActaObservada> listReport = new ArrayList<>();
        System.out.println(" TipoConsulta : " + TipoConsulta);
        String title = "";
        
        switch (TipoConsulta) {
            case 1://Historico
                service = rcTotalActaObservadaService.getListadoSegHisActasObsCC(codComputo, codOdpe, codTipoElec);
                nameReport = "RPT04020404";
                url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                parametros.put("tituloSecundario", ConstantReportes.nameTituloHistoricoActa);
                title = ConstantReportes.nameTituloHistoricoActa;
                break;
            case 0://Actual
                parametros.put("tituloSecundario", ConstantReportes.nameTituloTotalActa);
                title = ConstantReportes.nameTituloTotalActa;
                switch (Agrupacion) {
                    case 0://Local
                        service = rcTotalActaObservadaService.getListadoSegActasObsLocal(codComputo, codOdpe, codTipoElec);
                        nameReport = "RPT04020404L";
                        url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";                        
                        break;
                    case 1://Centro de computo
                        service = rcTotalActaObservadaService.getListadoSegActasObsCC(codComputo, codOdpe, codTipoElec);
                        nameReport = "RPT04020404";
                        url = ConstantReportes.pathReportConsult + nameReport + ".jrxml";
                        break;
                }
                break;
        }

        if (service == null || service.size() < 1) {
            validador(0);
            return;
        }

        parametros.put("codigo_compu", codComputo);
        parametros.put("descrip_compu", descrComputo);
        parametros.put("odpe", descrOdpe);
        parametros.put("version", DeclaracionComun.gstrVersionSuite);
        parametros.put("reporte", nameReport);
        parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
        parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
        parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
        parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
        parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
        parametros.put("url_imagen", imagen);
        parametros.put("tipoEleccion", codTipoElec);

        listReport = service;
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(url);
        StackPane sp = jp.reportShow(parametros, listReport, file, null);
        AppController appController = new AppController();
        
        appController.subBarraStageReport(mainStage, sp, title, 2);
    }

    private void validador(int nro) {
        switch (nro) {
            case 0:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe información para la consulta realizada.!");
                break;
        }
    }

    public void btnSalirOnAction() {
        panelSeleccion.getChildren().clear();
        stage.close();
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
