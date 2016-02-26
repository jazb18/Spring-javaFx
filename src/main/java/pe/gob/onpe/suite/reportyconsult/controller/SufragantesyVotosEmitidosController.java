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
import pe.gob.onpe.suite.domain.CatResultSufragantesyVotosEmitidos;
import pe.gob.onpe.suite.domain.ReporteSufragantesyVotosEmitidos;
import pe.gob.onpe.suite.reportyconsult.service.RCSufragantesYVotosEmitidosService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class SufragantesyVotosEmitidosController extends AppController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Stage mainStage, stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorPane;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    RCSufragantesYVotosEmitidosService rcSufragantesYVotosEmitidosService;

    @FXML
    private Pane paneSeleccion;

    @Autowired
    ApplicationContext context;

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
            subBarraStage(stage, ConstantReportes.nameTituloSufragantresEmitidos, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.SUFRAGANTES_VOTOS_EMITIDOS);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setShowODPE(true);
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowDepartamento(true);
            critSelecCtrl.setShowProvincia(true);
            critSelecCtrl.setShowDistrito(true);
            critSelecCtrl.setShowLocal(true);

            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_ODPE, critSelecCtrl.getCmbOdpes(), null, 0, 1}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_CENTROCOMPUTO, critSelecCtrl.getCmbCC(), null, 0, 2}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), null, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), null, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), null, 4, 3}
            });
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_LOCALVOTACION, critSelecCtrl.getCmbLocVotacion(), null, 0, 4}
            });

            critSelecCtrl.dibujarCombos(listaOpciones);
            critSelecCtrl.setLayoutX(40);
            critSelecCtrl.setLayoutY(8);
            paneSeleccion.getChildren().add(critSelecCtrl);

            cmdBotones_Click(0);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    private void btnImprimirOnAction() {
        try {

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteSufragantes + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
            String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
            String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String codLocal = critSelecCtrl.getCmbLocVotacion().getSelectionModel().getSelectedItem().getCodiLocal();
            int sumaTotalElectoresHabiles = 0;
            int sumaTotalAusentismo = 0;
            int sumaTotalCiudadanosVotantes = 0;
            String codUbigueo = ConstantReportes.VACIO;
            String codDpto = ConstantReportes.VACIO;
            String codProvincia = ConstantReportes.VACIO;
            String codDistrto = ConstantReportes.VACIO;

            if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec().equals(ConstantReportes.VACIO)) {
                validador(1);
                return;
            }
            if (critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().equals(ConstantReportes.VACIO)) {
                validador(2);
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
                System.out.println("PARAMETROS : " + " codOdpe : " + codOdpe + " codComputo : " + codComputo + " codTipoElec : " + codTipoElec + " codUbigueo : " + codUbigueo + " codLocal : " + codLocal);
                List<CatResultSufragantesyVotosEmitidos> listaReport = rcSufragantesYVotosEmitidosService.getCiudavotaronvotosemitidos(codOdpe, codComputo, codTipoElec, codUbigueo, codLocal);

                if (listaReport != null) {
                    if (listaReport.size() < 1) {
                        validador(3);
                        return;
                    }
                }

                JasperReportUtil jp = new JasperReportUtil();
                List<ReporteSufragantesyVotosEmitidos> listaReporte = new ArrayList<>();
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
                parametros.put("tituloSecundario", ConstantReportes.nameTituloTotalCiudadanosVotantes);
                parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
                parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
                parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
                parametros.put("tipoEleccion", codTipoElec);
                parametros.put("reporte", ConstantReportes.nameReporteSufragantes);
                parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
                parametros.put("url_imagen", imagen);
                parametros.put("version", DeclaracionComun.gstrVersionSuite);

                for (CatResultSufragantesyVotosEmitidos getArray : listaReport) {
                    ReporteSufragantesyVotosEmitidos fila = new ReporteSufragantesyVotosEmitidos();
                    fila.setMesa(getArray.getCnume_acta());
                    fila.setElectoresHabiles(getArray.getElec_habil());
                    fila.setCiudadanosVotantes(getArray.getSufragaron());
                    fila.setAusentismo(getArray.getAusentismo());
                    fila.setDescOdpe(getArray.getCcodi_odpe() + " " + getArray.getTdesc_odpe());
                    fila.setDescCompu(getArray.getCcent_compu() + " " + getArray.getTdesc_compu());
                    fila.setDepartamento(getArray.getDepartamento());
                    fila.setProvincia(getArray.getProvincia());
                    fila.setDistrito(getArray.getDistrito());
                    fila.setCodigoLocal(getArray.getCcodi_local());
                    fila.setLocalVotacion(getArray.getTnomb_local());
                    fila.setDireccionVotacion(getArray.getTdire_local());
                    fila.setTotalMesasHabiles(getArray.getMesas_x_local());
                    fila.setTotalDepartamento(getArray.getMesas_dpto());
                    fila.setTotalProvincia(getArray.getMesas_prov());
                    fila.setTotalDistrito(getArray.getMesas_dist());

                    sumaTotalElectoresHabiles += Integer.parseInt(getArray.getElec_habil());
                    sumaTotalCiudadanosVotantes += Integer.parseInt(getArray.getSufragaron());
                    sumaTotalAusentismo += Integer.parseInt(getArray.getAusentismo());

                    fila.setTotalElectoresHabiles(sumaTotalElectoresHabiles);
                    fila.setTotalCiudadanosVotantes(sumaTotalCiudadanosVotantes);
                    fila.setTotalAusentismo(sumaTotalAusentismo);

                    listaReporte.add(fila);
                }

                StackPane sp = jp.reportShow(parametros, listaReporte, file, null);
                AppController appController = new AppController();
                String title = ConstantReportes.nameTituloTotalCiudadanosVotantes;
                appController.subBarraStageReport(mainStage, sp, title, 2);
                cmdBotones_Click(1);
                cmdBotones_Click(2);

            }
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    private void btnSalirOnAction() {
        try {
            stage.close();
            paneSeleccion.getChildren().clear();
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    private void validador(int nro) {
        switch (nro) {
            case 1:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe elegir el tipo de eleccion.");
                break;
            case 2:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe  seleccionar un departamento o continente.");
                break;
            case 3:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada");
                break;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Sufragantes y Votos Emitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Sufragantes y Votos Emitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Sufragantes y Votos Emitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Sufragantes y Votos Emitidos - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
