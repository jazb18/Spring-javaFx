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
import javafx.scene.control.TextField;
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
import pe.gob.onpe.suite.domain.ReporteResumenResultados;
import pe.gob.onpe.suite.domain.ResumenResultados;
import pe.gob.onpe.suite.reportyconsult.service.RcResumenResultadosService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ResumenResultadosController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    private Stage mainStage, stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane paneSeleccion;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    private String name = ConstantCommon.titleReporCons;

    @Autowired
    public RcResumenResultadosService resultadosService;

    private List<ResumenResultados> listDB = new ArrayList<>();

    @FXML
    private TextField txtPresidencial, txtPresidencial2, txtPresidencial3, txtPresidencial4, txtPresidencial5, txtPresidencial6, txtPresidencial7, txtPresidencial8, txtPresidencial9, txtPresidencial10, txtPresidencial11, txtPresidencial12, txtCongresal, txtCongresal2, txtCongresal3, txtCongresal4, txtCongresal5, txtCongresal6, txtCongresal7, txtCongresal8, txtCongresal9, txtCongresal10, txtCongresal11, txtCongresal12, txtParlamento, txtParlamento2, txtParlamento3, txtParlamento4, txtParlamento5, txtParlamento6, txtParlamento7, txtParlamento8, txtParlamento9, txtParlamento10, txtParlamento11, txtParlamento12;

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
            subBarraStage(stage, ConstantReportes.nameTituloResumenResultados, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowODPE(true);
            critSelecCtrl.setTipoModulo(ConstantReportes.RESUMEN_RESULTADOS);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();
//            critSelecCtrl.setLayoutX(230);
            critSelecCtrl.setLayoutX(20);

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 0}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 1}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);
            paneSeleccion.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    private void btnConsultarOnAction() {
        try {
            cleaner();
            String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
            String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
            listDB = resultadosService.getResumenResultados(codOdpe, codComputo);

            if (listDB != null) {
                if (listDB.size() < 1) {
                    validador(1);
                    return;
                }
            }

            for (ResumenResultados data : listDB) {
                //Mesas Asignadas
                txtPresidencial.setText(data.getMesas_habiles_pre());
                txtCongresal.setText(data.getMesas_habiles_con());
                txtParlamento.setText(data.getMesas_habiles_par());
                //Actas sin observación
                txtPresidencial2.setText(data.getMesas_sin_obs_pre());
                txtCongresal2.setText(data.getMesas_sin_obs_con());
                txtParlamento2.setText(data.getMesas_sin_obs_par());
                //Actas observadas
                txtPresidencial3.setText(data.getActas_obs_pre());
                txtCongresal3.setText(data.getActas_obs_con());
                txtParlamento3.setText(data.getActas_obs_par());
                //Porcentaje de procesadas
                txtPresidencial4.setText(AppController.formatPorcentaje3(data.getPorc_concesadas_pre()));
                txtCongresal4.setText(AppController.formatPorcentaje3(data.getPorc_concesadas_con()));
                txtParlamento4.setText(AppController.formatPorcentaje3(data.getPorc_concesadas_par()));
                //porcentaje de Actas por Procesar
                txtPresidencial5.setText(AppController.formatPorcentaje3(data.getPorc_por_concesar_pre()));
                txtCongresal5.setText(AppController.formatPorcentaje3(data.getPorc_por_concesar_con()));
                txtParlamento5.setText(AppController.formatPorcentaje3(data.getPorc_por_concesar_par()));
                //Electores Hábiles
                txtPresidencial6.setText(data.getNumero_elect_habil_pre());
                txtCongresal6.setText(data.getNumero_elect_habil_con());
                txtParlamento6.setText(data.getNumero_elect_habil_par());
                //Ciudadanos que Votaron
                txtPresidencial7.setText(data.getNumero_ciuda_vot_pre());
                txtCongresal7.setText(data.getNumero_ciuda_vot_con());
                txtParlamento7.setText(data.getNumero_ciuda_vot_par());
                //Ausentismo Presidencial
                if (data.getNumero_ciuda_vot_pre().isEmpty()) {
                    txtPresidencial8.setText(ConstantReportes.VACIO);
                } else {
                    int resultado = Integer.parseInt(txtPresidencial6.getText()) - Integer.parseInt(txtPresidencial7.getText());
                    txtPresidencial8.setText(String.valueOf(resultado));
                }
                if (data.getNumero_ciuda_vot_con().isEmpty()) {
                    txtCongresal8.setText(ConstantReportes.VACIO);
                } else {
                    int resultado = Integer.parseInt(txtCongresal6.getText()) - Integer.parseInt(txtCongresal7.getText());
                    txtCongresal8.setText(String.valueOf(resultado));
                }
                if (data.getNumero_ciuda_vot_par().isEmpty()) {
                    txtParlamento8.setText(ConstantReportes.VACIO);
                } else {
                    int resultado = Integer.parseInt(txtParlamento6.getText()) - Integer.parseInt(txtParlamento7.getText());
                    txtParlamento8.setText(String.valueOf(resultado));
                }
                //Porcentaje de Participación
                txtPresidencial9.setText(AppController.formatPorcentaje3(data.getPorc_participacion_pre()));
                txtCongresal9.setText(AppController.formatPorcentaje3(data.getPorc_participacion_con()));
                txtParlamento9.setText(AppController.formatPorcentaje3(data.getPorc_participacion_par()));
                //Porcentaje de Ausentismo
                txtPresidencial10.setText(AppController.formatPorcentaje3(data.getPor_ausentismo_pre()));
                txtCongresal10.setText(AppController.formatPorcentaje3(data.getPor_ausentismo_con()));
                txtParlamento10.setText(AppController.formatPorcentaje3(data.getPor_ausentismo_par()));
                //Total de Votos Blancos Nulos e Impugnados
                txtPresidencial11.setText(data.getV_blanco_pre());
                txtCongresal11.setText(data.getV_blanco_con());
                txtParlamento11.setText(data.getV_blanco_par());
                // 
                txtPresidencial12.setText(data.getV_nulo_pre());
                txtCongresal12.setText(data.getV_nulo_con());
                txtParlamento12.setText(data.getV_nulo_par());
                cmdBotones_Click(1);

            }
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnImprimirOnAction() throws Exception {

        try {
            List<ResumenResultados> printList = new ArrayList<>();
            List<ReporteResumenResultados> listReport = new ArrayList<>();
            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            printList = listDB;

            if (printList != null) {
                if (printList.size() < 1) {
                    validador(2);
                    return;
                }
            }

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteResumenResultados + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            String descrOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe();
            String descrComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu();
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloResumenResultados);
            parametros.put("odpe", descrOdpe);
            parametros.put("nombreCC", descrComputo);
            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("reporte", ConstantReportes.nameReporteResumenResultados);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            for (ResumenResultados p : printList) {

                ReporteResumenResultados r = new ReporteResumenResultados();

                r.setMesaPresidente(p.getMesas_habiles_pre());
                r.setMesaCongresales(p.getMesas_habiles_con());
                r.setMesaParlamento(p.getMesas_habiles_par());

                r.setActContabPresidente(p.getMesas_sin_obs_pre());
                r.setActContabCongresales(p.getMesas_sin_obs_con());
                r.setActContabParlamento(p.getMesas_sin_obs_par());

                r.setActObsPresidente(p.getActas_obs_pre());
                r.setActObsCongresales(p.getActas_obs_con());
                r.setActObsParlamento(p.getActas_obs_par());

                r.setPorcProcPresidente(AppController.formatPorcentaje3(p.getPorc_concesadas_pre()));
                r.setPorcProcCongresales(AppController.formatPorcentaje3(p.getPorc_concesadas_con()));
                r.setPorcProcParlamento(AppController.formatPorcentaje3(p.getPorc_concesadas_par()));

                r.setPorcxProcPresidente(AppController.formatPorcentaje3(p.getPorc_por_concesar_pre()));
                r.setPorcxProcCongresales(AppController.formatPorcentaje3(p.getPorc_por_concesar_con()));
                r.setPorcxProcParlamento(AppController.formatPorcentaje3(p.getPorc_por_concesar_par()));

                r.setElectHabPresidente(p.getNumero_elect_habil_pre());
                r.setElectHabCongresales(p.getNumero_elect_habil_con());
                r.setElectHabParlamento(p.getNumero_elect_habil_par());

                r.setCiudaVotaPresidente(p.getNumero_ciuda_vot_pre());
                r.setCiudaVotaCongresales(p.getNumero_ciuda_vot_con());
                r.setCiudaVotaParlamento(p.getNumero_ciuda_vot_par());

                r.setAusentPresidente(txtPresidencial8.getText());
                r.setAusentCongresales(txtCongresal8.getText());
                r.setAusentParlamento(txtParlamento8.getText());

                r.setPorcParticPresidente(AppController.formatPorcentaje3(p.getPorc_participacion_pre()));
                r.setPorcParticCongresales(AppController.formatPorcentaje3(p.getPorc_participacion_con()));
                r.setPorcParticParlamento(AppController.formatPorcentaje3(p.getPorc_participacion_par()));

                r.setPorcAusentPresidente(AppController.formatPorcentaje3(p.getPor_ausentismo_pre()));
                r.setPorcAusentCongresales(AppController.formatPorcentaje3(p.getPor_ausentismo_con()));
                r.setPorcAusentParlamento(AppController.formatPorcentaje3(p.getPor_ausentismo_par()));

                r.setVotoBlancPresidente(p.getV_blanco_pre());
                r.setVotoBlancCongresales(p.getV_blanco_con());
                r.setVotoBlancParlamento(p.getV_blanco_par());

                r.setVotoNuloPresidente(p.getV_nulo_pre());
                r.setVotoNuloCongresales(p.getV_nulo_con());
                r.setVotoNuloParlamento(p.getV_nulo_par());

                listReport.add(r);
            }
            StackPane sp = jp.reportShow(parametros, listReport, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTituloResumenResultados;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnSalirOnAction() {
        try {
            stage.close();
            cleaner();
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    public void cleaner() {

        txtPresidencial.setText(ConstantReportes.VACIO);
        txtPresidencial2.setText(ConstantReportes.VACIO);
        txtPresidencial3.setText(ConstantReportes.VACIO);
        txtPresidencial4.setText(ConstantReportes.VACIO);
        txtPresidencial5.setText(ConstantReportes.VACIO);
        txtPresidencial6.setText(ConstantReportes.VACIO);
        txtPresidencial7.setText(ConstantReportes.VACIO);
        txtPresidencial8.setText(ConstantReportes.VACIO);
        txtPresidencial9.setText(ConstantReportes.VACIO);
        txtPresidencial10.setText(ConstantReportes.VACIO);
        txtPresidencial11.setText(ConstantReportes.VACIO);
        txtPresidencial12.setText(ConstantReportes.VACIO);

        txtCongresal.setText(ConstantReportes.VACIO);
        txtCongresal2.setText(ConstantReportes.VACIO);
        txtCongresal3.setText(ConstantReportes.VACIO);
        txtCongresal4.setText(ConstantReportes.VACIO);
        txtCongresal5.setText(ConstantReportes.VACIO);
        txtCongresal6.setText(ConstantReportes.VACIO);
        txtCongresal7.setText(ConstantReportes.VACIO);
        txtCongresal8.setText(ConstantReportes.VACIO);
        txtCongresal9.setText(ConstantReportes.VACIO);
        txtCongresal10.setText(ConstantReportes.VACIO);
        txtCongresal11.setText(ConstantReportes.VACIO);
        txtCongresal12.setText(ConstantReportes.VACIO);

        txtParlamento.setText(ConstantReportes.VACIO);
        txtParlamento2.setText(ConstantReportes.VACIO);
        txtParlamento3.setText(ConstantReportes.VACIO);
        txtParlamento4.setText(ConstantReportes.VACIO);
        txtParlamento5.setText(ConstantReportes.VACIO);
        txtParlamento6.setText(ConstantReportes.VACIO);
        txtParlamento7.setText(ConstantReportes.VACIO);
        txtParlamento8.setText(ConstantReportes.VACIO);
        txtParlamento9.setText(ConstantReportes.VACIO);
        txtParlamento10.setText(ConstantReportes.VACIO);
        txtParlamento11.setText(ConstantReportes.VACIO);
        txtParlamento12.setText(ConstantReportes.VACIO);
        listDB.clear();

    }

    private void validador(int nro) {
        switch (nro) {
            case 1:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada");
                break;
            case 2:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Consultar Antes, No Existen Datos para Mostrar");
                break;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resumen Resultados - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Resumen Resultados - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Resumen Resultados - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resumen Resultados - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
