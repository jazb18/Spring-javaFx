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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantAdministracion;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatResumenTotalPorCentroComputo;
import pe.gob.onpe.suite.reportyconsult.RepConsuControllerFactory;
import pe.gob.onpe.suite.reportyconsult.service.RcResumenTotalCentroComputoService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ResumenTotalCentroComputoController extends AppController implements Initializable {

    private Stage mainStage;
    private Scene scene;
    private Stage stage;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    CriterioSeleccionController critSelecCtrl;

    @FXML
    private AnchorPane anchorPane, anchorPaneCentral;

    @FXML
    protected ToggleGroup consultaToggleGrp;

    @FXML
    protected RadioButton radioButtonEnPorcentaje, radioButtonEnCifras;

    @FXML
    protected Label mensaje;

    @Autowired
    private RcResumenTotalCentroComputoService rcResumenTotalCentroComputoService;

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
        subBarraStage(stage, ConstantReportes.nameTituloResumenTotalporCC, 2);

        try {

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            anchorPaneCentral.getChildren().removeAll(critSelecCtrl);
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowODPE(true);
            critSelecCtrl.setShowTipoConexion(true);
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendiente", "Terminados"));
            critSelecCtrl.setShowEstado(true);
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setShowProvincia(false);
            critSelecCtrl.setShowDistrito(false);
            critSelecCtrl.setShowDepartamento(false);
            critSelecCtrl.setTipoModulo(ConstantReportes.RESUMEN_TOTAL_PORCC);
            critSelecCtrl.cargarCombos();

            //Lista de los parametros de Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOCONEXION, critSelecCtrl.getCmbTipoConexion(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ESTADO, critSelecCtrl.getCmbEstado(), null, 0, 3},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 4}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);

            anchorPaneCentral.getChildren().add(critSelecCtrl);
            cmdBotones_Click(0);
        } catch (Exception ex) {
            System.out.println("Excepcion General " + ex);
            ex.printStackTrace();
        }

    }

    @FXML
    private void btnImprimirOnAction() {
        String tipoReporte, estado, tipoConexion;
        /**
         * ********* Estado ("Todos", "Pendiente", "Terminados")
         */
        if (critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem().equals("Todos")) {
            estado = "";
        } else if (critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem().equals("Pendiente")) {
            estado = "NO";
        } else {
            estado = "SI";
        }

        /**
         * ***** TipoConexion ("Todos", "Cobre", "Satelital")
         */
        if (critSelecCtrl.getCmbTipoConexion().getSelectionModel().getSelectedItem().equals("Todos")) {
            tipoConexion = "";
        } else if (critSelecCtrl.getCmbTipoConexion().getSelectionModel().getSelectedItem().equals("Cobre")) {
            tipoConexion = "C";
        } else {
            tipoConexion = "S";
        }

        /**
         * ******** RadioButtom
         */
        if (consultaToggleGrp.getSelectedToggle() == radioButtonEnPorcentaje) {
            tipoReporte = "1"; // en porcentaje
        } else {
            tipoReporte = "2"; // en cifras
        }

        List<CatResumenTotalPorCentroComputo> catResumenTotalPorCentroComputoList = rcResumenTotalCentroComputoService.getResumenTotalCentroComputoEnCifras(
                critSelecCtrl.getCentroComputoSelected().getCentCompu(),
                critSelecCtrl.getODPESelected().getCodiOdpe(),
                critSelecCtrl.getTipoEleccionSelected().getTipoElec(),
                estado,
                tipoConexion,
                tipoReporte
        );

        /**
         * Printer**
         */
        try {
            mensaje.setVisible(false);
            if (catResumenTotalPorCentroComputoList.size() == 0) {
                mensaje.setVisible(true);
                mensaje.setTextFill(Color.web("#FF0000"));
                mensaje.setText("NO HAY REGISTRO QUE MOSTRAR");
                return;
            }
            Map<String, Object> parametros = new HashMap<>();
            InputStream file = null;
            if (tipoReporte.equals("1")) { //reporte en procentajes
                file = this.getClass().getClassLoader().getResourceAsStream("pe/gob/onpe/suite/reportyconsult/view/jrxml/" + "RPT040105.jrxml");
                parametros.put("reporte", ConstantReportes.nameReporteResumenTotalCCporcentaje);

                for (CatResumenTotalPorCentroComputo iteratorBean : catResumenTotalPorCentroComputoList) {

                    if (iteratorBean.getMesasAInstalar() == 0) {
                        iteratorBean.setTotActasSiniestPrComodin(0.000);
                        iteratorBean.setTotActasExtravPrComodin(0.000); // solo se hace uso en el reporte RPT040105.jrxml como comodin
                        iteratorBean.setActasProcesadaPr(0.000);
                        iteratorBean.setActasCompuPr(0.000);
                    } else {

                        /**
                         * Cálculo % % Actas Siniestradas. *
                         */
                        iteratorBean.setTotActasSiniestPrComodin((iteratorBean.getTotActasSiniestPr() / iteratorBean.getMesasAInstalar()) * 100);
                        /**
                         * Cálculo % Actas extraviadas por resolución *
                         */
                        iteratorBean.setTotActasExtravPrComodin((iteratorBean.getTotActasExtravPr() / iteratorBean.getMesasAInstalar()) * 100);

                        /**
                         * % Actas procesadas (Recibidas) *
                         */
                        iteratorBean.setActasProcesadaPr((iteratorBean.getActasProcesadaPr() / iteratorBean.getMesasAInstalar()) * 100);

                        /**
                         * % Actas Contabiliz *
                         */
                        iteratorBean.setActasCompuPr((iteratorBean.getActasCompuPr() / iteratorBean.getMesasAInstalar()) * 100);
                    }

                    /**
                     * * [Digitalización ===> % Actas], [Control de Calidad
                     * ===> % Actas ] [Transmisión de Imágenes ===> % Actas ]**
                     */
                    if ((iteratorBean.getMesasAInstalar() - (iteratorBean.getTotActasExtravPr() + iteratorBean.getTotActasSiniestPr() + iteratorBean.getMesasNoInstal())) == 0) {
                        iteratorBean.setActasDigitPr(0.000);
                        iteratorBean.setActasDigitCalidad(0.000);
                        iteratorBean.setActasTransPr(0.000);
                    } else {
                        iteratorBean.setActasDigitPr((iteratorBean.getActasDigitPr() / (iteratorBean.getMesasAInstalar() - (iteratorBean.getTotActasExtravPr() + iteratorBean.getTotActasSiniestPr() + iteratorBean.getMesasNoInstal()))) * 100);
                        iteratorBean.setActasDigitCalidad((iteratorBean.getActasDigitCalidad() / (iteratorBean.getMesasAInstalar() - (iteratorBean.getTotActasExtravPr() + iteratorBean.getTotActasSiniestPr() + iteratorBean.getMesasNoInstal()))) * 100);
                        iteratorBean.setActasTransPr((iteratorBean.getActasTransPr() / (iteratorBean.getMesasAInstalar() - (iteratorBean.getTotActasExtravPr() + iteratorBean.getTotActasSiniestPr() + iteratorBean.getMesasNoInstal()))) * 100);

                    }

                    /**
                     * * [Digitalización ===> % Resol.] y [Control de Calidad
                     * ===> % Resol.] y [Transmisión de Imágenes ===> % Resol. ]
                     * **
                     */
                    if (iteratorBean.getResolIngresadas() == 0) {
                        iteratorBean.setResolDigitalizadas(0.000);
                        iteratorBean.setResolDigitCalidad(0.000);
                        iteratorBean.setResolTransmitidas(0.000);
                    } else {
                        iteratorBean.setResolDigitalizadas((iteratorBean.getResolDigitalizadas() / iteratorBean.getResolIngresadas()) * 100);
                        iteratorBean.setResolDigitCalidad((iteratorBean.getResolDigitCalidad() / iteratorBean.getResolIngresadas()) * 100);
                        iteratorBean.setResolTransmitidas((iteratorBean.getResolTransmitidas() / iteratorBean.getResolIngresadas()) * 100);
                    }

                    /**
                     * * [Registro de Omisos ===> % Miembr.] y [Registro de
                     * Omisos ===> % Votant.] **
                     */
                    if ((iteratorBean.getMesasInstal() + iteratorBean.getMesasNoInstal()) == 0) {
                        iteratorBean.setOmisosMmesa(0.000);
                        iteratorBean.setOmisosVotacion(0.00);
                    } else {
                        iteratorBean.setOmisosMmesa((iteratorBean.getOmisosMmesa() / iteratorBean.getMesasAInstalar()) * 100);
                        iteratorBean.setOmisosVotacion((iteratorBean.getOmisosVotacion() / iteratorBean.getMesasAInstalar()) * 100);
                    }
                }
                /**
                 * *************
                 */

            } else { // reporte por cifras
                file = this.getClass().getClassLoader().getResourceAsStream("pe/gob/onpe/suite/reportyconsult/view/jrxml/" + "RPT040105C.jrxml");
                parametros.put("reporte", ConstantReportes.nameReporteResumenTotalCCcifras);
            }

            String title = ConstantReportes.nameTituloResumenTotalCC;
            parametros.put("TituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);
            parametros.put("TituloRep", ConstantReportes.nameTituloResumenTotalCC);
            parametros.put("TituloEleccionSimple", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("TituloEleccionCompleto", "");
            parametros.put("desOdpe", critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe());
            parametros.put("desComp", critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getDescCompu());
            parametros.put("estado", critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem());
            parametros.put("tipoconexion", critSelecCtrl.getCmbTipoConexion().getSelectionModel().getSelectedItem());
            parametros.put("SVOF", ReportesConsultaUtil.validarInfOficial());
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);

            JasperReportUtil jp = new JasperReportUtil();
            StackPane sp = jp.reportShow(parametros, catResumenTotalPorCentroComputoList, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPCION GENERAL :" + e);
        }

    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
        cmdBotones_Click(3);
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resumen Total por Centro de computo - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resumen Total por Centro de computo- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
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
