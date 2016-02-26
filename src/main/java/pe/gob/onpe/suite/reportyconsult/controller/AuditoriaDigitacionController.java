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
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.RCAuditoriaDigitacion;
import pe.gob.onpe.suite.domain.RCReporteAuditoriaDigitacion;
import pe.gob.onpe.suite.reportyconsult.service.RCSufragantesYVotosEmitidosService;
import pe.gob.onpe.suite.reportyconsult.service.RcAuditoriaDigitacionService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class AuditoriaDigitacionController extends AppController implements Initializable {

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

    @Autowired
    RcAuditoriaDigitacionService rcAuditoriaDigitacionService;

    @Autowired
    IComunService comunService;

    @FXML
    private Pane paneSeleccion;

    @Autowired
    ApplicationContext context;

    private Float Ncandidatos = Float.parseFloat("0");
    private List<RCAuditoriaDigitacion> aux = new ArrayList<>();

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
            subBarraStage(stage, ConstantReportes.nameTituloAuditoriaDigitacion, 2);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);
            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.AUDITORIA_DIGITACION);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());//C30000
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

            String nameReport = ConstantReportes.VACIO, reporte = ConstantReportes.VACIO;
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
            String codComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
            String codTipoElec = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String codUbigueo = ConstantReportes.VACIO, descDpto = ConstantReportes.VACIO, codDpto = ConstantReportes.VACIO, codProvincia = ConstantReportes.VACIO, codDistrto = ConstantReportes.VACIO;

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
                    descDpto = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento();
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

                if (descDpto.equals("LIMA CENTRO")) {
                    aux = rcAuditoriaDigitacionService.getAuditoriaDigitacionLC(codOdpe, codComputo, codUbigueo, codTipoElec);
                } else if (descDpto.equals("LIMA PROVINCIAS")) {
                    aux = rcAuditoriaDigitacionService.getAuditoriaDigitacionLP(codOdpe, codComputo, codUbigueo, codTipoElec);
                } else {
                    aux = rcAuditoriaDigitacionService.getAuditoriaDigitacion(codOdpe, codComputo, codUbigueo, codTipoElec);
                }

                if (aux != null) {
                    if (aux.size() < 1) {
                        validador(3);
                        return;
                    }
                }

                JasperReportUtil jp = new JasperReportUtil();
                List<RCReporteAuditoriaDigitacion> listaReporte = new ArrayList<>();
                Map<String, Object> parametros = new HashMap<>();

                for (RCAuditoriaDigitacion row : aux) {

                    RCReporteAuditoriaDigitacion k = new RCReporteAuditoriaDigitacion();
                    k.setDescOdpe(row.getC_desc_odpe());
                    k.setDescCompu(row.getC_desc_compu());
                    k.setDepartamento(row.getDepartamento());
                    k.setProvincia(row.getProvincia());
                    k.setDistrito(row.getDistrito());
                    k.setVDigitados(row.getN_votos_());
                    k.setCodigoNumeroActa(row.getC_nume_acta() + " - " + row.getC_copia_acta());
                    k.setNro(row.getN_ubicacion_agrupol());
                    k.setOrganizacionPolitica(row.getC_desc_agrupol());
                    k.setCodUbigeo(row.getC_codi_ubigeo());
                    k.setTvotos(row.getN_total_votos());
                    k.setNvoto1(row.getN_votos_1());
                    k.setNvoto2(row.getN_votos_2());
                    k.setNvoto3(row.getN_votos_3());
                    k.setNvoto4(row.getN_votos_4());
                    k.setNvoto5(row.getN_votos_5());
                    k.setNvoto6(row.getN_votos_6());
                    k.setNvoto7(row.getN_votos_7());
                    k.setNvoto8(row.getN_votos_8());
                    k.setNvoto9(row.getN_votos_9());
                    k.setNvoto10(row.getN_votos_10());
                    k.setNvoto11(row.getN_votos_11());
                    k.setNvoto12(row.getN_votos_12());
                    k.setNvoto13(row.getN_votos_13());
                    k.setNvoto14(row.getN_votos_14());
                    k.setNvoto15(row.getN_votos_15());
                    k.setNvoto16(row.getN_votos_16());
                    k.setNvoto17(row.getN_votos_17());
                    k.setNvoto18(row.getN_votos_18());
                    k.setNvoto19(row.getN_votos_19());
                    k.setNvoto20(row.getN_votos_20());
                    k.setNvoto21(row.getN_votos_21());
                    k.setNvoto22(row.getN_votos_22());
                    k.setNvoto23(row.getN_votos_23());
                    k.setNvoto24(row.getN_votos_24());
                    k.setNvoto25(row.getN_votos_25());
                    k.setNvoto26(row.getN_votos_26());
                    k.setNvoto27(row.getN_votos_27());
                    k.setNvoto28(row.getN_votos_28());
                    k.setNvoto29(row.getN_votos_29());
                    k.setNvoto30(row.getN_votos_30());
                    k.setNvoto31(row.getN_votos_31());
                    k.setNvoto32(row.getN_votos_32());
                    k.setNvoto33(row.getN_votos_33());
                    k.setNvoto34(row.getN_votos_34());
                    k.setNvoto35(row.getN_votos_35());
                    k.setNvoto36(row.getN_votos_36());

                    listaReporte.add(k);

                }

                //IMPRESION DE REPORTES
                switch (codTipoElec) {
                    case "10":
                        reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE10;
                        nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                        System.out.println(" @@INGRESE AL TIPO ELECCION 10 ");

                        break;
                    case "11":
                        switch (Math.round(Ncandidatos)) {
                            case 3:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE113;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_3 ");
                                break;
                            case 4:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE114;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_4 ");
                                break;
                            case 5:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE115;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_5 ");
                                break;
                            case 6:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE116;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_6 ");
                                break;
                            case 7:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE117;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_7 ");
                                break;
                            case 36:
                                reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE1136;
                                nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                                System.out.println(" @@INGRESE AL TIPO ELECCION 11_36 ");
                                break;
                        }
                        break;
                    case "12":
                        reporte = ConstantReportes.nameReporteAuditoriaDigitacionTE12;
                        nameReport = ConstantReportes.pathReportConsult + reporte + ".jrxml";
                        System.out.println(" @@INGRESE AL TIPO ELECCION 12 ");
                        break;

                }

                parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
                parametros.put("tituloRep", ConstantReportes.nameTituloAuditoriaDigitacionReporte);
                parametros.put("tituloSubRep", ConstantReportes.nameAnexoFormato);
                parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
                parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
                parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
                parametros.put("reporte", reporte);
                parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
                parametros.put("url_imagen", imagen);
                parametros.put("tipoEleccion", codTipoElec);
                parametros.put("version",  DeclaracionComun.gstrVersionSuite);

                InputStream file = this.getClass().getClassLoader().getResourceAsStream(nameReport);
                StackPane sp = jp.reportShow(parametros, listaReporte, file, null);
                AppController appController = new AppController();
                String title = ConstantReportes.nameTituloAuditoriaDigitacion;
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
            cmdBotones_Click(3);
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
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Auditoria de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Auditoria de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Auditoria de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Auditoria de Digitacion - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
        }
    }

    public Float getNcandidatos() {
        return Ncandidatos;
    }

    public void setNcandidatos(Float Ncandidatos) {
        this.Ncandidatos = Ncandidatos;
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
