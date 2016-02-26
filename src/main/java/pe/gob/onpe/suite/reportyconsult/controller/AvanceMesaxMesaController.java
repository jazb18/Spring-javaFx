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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.MesaXMesa;
import pe.gob.onpe.suite.domain.ParametrosInformacionOficial;
import pe.gob.onpe.suite.reportyconsult.service.RCAvanceEstadoActaService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 *
 * @author KLeon
 */
public class AvanceMesaxMesaController extends AppController implements Initializable {

    private Stage mainStage, stage, stageMessageBox;
    private Scene scene;

    private String name = ConstantCommon.titleReporCons;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane paneContainer;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    IComunService comunService;
    @Autowired
    RCAvanceEstadoActaService avanceEstadoActaService;
    @Autowired
    ApplicationContext context;

    private Float Ncandidatos = Float.parseFloat("0");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
            subBarraStage(stage, ConstantReportes.nameTituloAvanceMesaPorMesa, 2);

            /*Carga de los comboBox*/
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            paneContainer.getChildren().clear();
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setListaEstado(Arrays.asList("Todos", "Pendiente", "Recibido"));
            critSelecCtrl.setShowEstado(true);
            critSelecCtrl.setShowCentroComputo(true);
            critSelecCtrl.setShowDepartamento(true);
            critSelecCtrl.setShowProvincia(true);
            critSelecCtrl.setShowDistrito(true);
            critSelecCtrl.setShowODPE(true);
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setTipoModulo(ConstantReportes.AVANCE_MESAPORMESA);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();

            //Lista de los parametros de Filtros
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
                new Object[]{ConstantReportes.FILTRO_DEPARTAMENTO, critSelecCtrl.getCmbDepa(), 150, 0, 3},
                new Object[]{ConstantReportes.FILTRO_PROVINCIA, critSelecCtrl.getCmbProv(), 150, 2, 3},
                new Object[]{ConstantReportes.FILTRO_DISTRITO, critSelecCtrl.getCmbDistrito(), 150, 4, 3}
            });
            critSelecCtrl.dibujarCombos(listaOpciones);

            paneContainer.getChildren().add(critSelecCtrl);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
    }

    @FXML
    private void btnImprimirOnAction() {
        if (validacionPrevia()) {
            procesarConsulta();
        }

    }

    private boolean validacionPrevia() {
        boolean validate = true;
        if (critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion().equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
            validate = false;
            mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Elegir el tipo de eleccion.");
        } else if (critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCentCompu().equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
            validate = false;
            mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe seleccionar un departamento o continente.");
        }
        return validate;
    }

    private void procesarConsulta() {
        ParametrosInformacionOficial beanParamSeleccion = getParamInformacionOficial();
        try {
            String nameStoreProcedure = ConstantReportes.STOREP_AVANCE_MESAXMESA;
            if (beanParamSeleccion.getCodUbigeo() != null && !beanParamSeleccion.getCodUbigeo().equals("")) {
                String departamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento();
                if (!departamento.equals(ConstantReportes.SELECCION_COMBO_TODOS)) {
                    if (departamento.equals("LIMA CENTRO")) {
                        nameStoreProcedure = ConstantReportes.STOREP_AVANCE_MESAXMESA_LIMAC;
                    } else if (departamento.equals("LIMA PROVINCIAS")) {
                        nameStoreProcedure = ConstantReportes.STOREP_AVANCE_MESAXMESA_LIMAP;
                    }
                }
            }
            //String storeProc,String codCC,String codODPE,String codUbigeo,String codTipoElec
            List<MesaXMesa> lista = avanceEstadoActaService.ObtenerAvanceMesaXMesa(nameStoreProcedure, beanParamSeleccion.getCodComputo(), beanParamSeleccion.getCodOdpe(),
                    beanParamSeleccion.getCodUbigeo(), beanParamSeleccion.getTipoElec());
            if (lista != null && lista.size() > 0) {
                /*Proceso de impresion*/
                String namereport = ConstantReportes.nameReporteAvanceMesaXMesaPRE;
                switch (beanParamSeleccion.getTipoElec()) {
                    case "10":
                        namereport = ConstantReportes.nameReporteAvanceMesaXMesaPRE;
                        break;
                    case "11":
                        namereport = getNameReportByNcandidatos(Ncandidatos);
                        break;
                    case "12":
                        namereport = ConstantReportes.nameReporteAvanceMesaXMesaPAR;
                        break;
                }

                InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                        ConstantReportes.pathReportConsult + namereport + ".jrxml");
                String title = ConstantReportes.nameTituloAvanceMesaPorMesa;

                Map<String, Object> parametros = new HashMap<>();
                parametros.put("reporte", namereport);
                cargaParametros(parametros);

                JasperReportUtil jp = new JasperReportUtil();

                StackPane sp = jp.reportShow(parametros, lista, file, null);

                AppController appController = new AppController();
                appController.subBarraStageReport(mainStage, sp, title, 2);
            } else {
                EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                    stageMessageBox.close();
                    stage.requestFocus();
                };
                stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, ConstantCommon.titleValidacion, ConstantReportes.MSJ_NODATA, null, null, null, eventOK);
            }
        } catch (Exception ex) {
            System.out.println("Excepcion General : " + ex);
        }
    }

    /*Obteniendo el nombre del reporte por el num de candidatos*/
    public String getNameReportByNcandidatos(Float Ncandidatos) {
        switch (Math.round(Ncandidatos)) {
            case 2:
                return ConstantReportes.nameReporteAvanceMesaXMesaC3;
            case 3:
                return ConstantReportes.nameReporteAvanceMesaXMesaC3;
            case 4:
                return ConstantReportes.nameReporteAvanceMesaXMesaC4;
            case 5:
                return ConstantReportes.nameReporteAvanceMesaXMesaC5;
            case 6:
                return ConstantReportes.nameReporteAvanceMesaXMesaC6;
            case 7:
                return ConstantReportes.nameReporteAvanceMesaXMesaC7;
            case 9:
                return ConstantReportes.nameReporteAvanceMesaXMesaC9;
            case 36:
                return ConstantReportes.nameReporteAvanceMesaXMesaC36;
            default:
                return ConstantReportes.nameReporteAvanceMesaXMesaC3;
        }
    }

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloGeneral", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloEleccionSimple", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("TituloRep", ConstantReportes.nameTituloAvanceMesaPorMesa);

            parametros.put("odpe", critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getDescOdpe());
            parametros.put("ccomputo", critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem() != null ? critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getDescDepartamento() : critSelecCtrl.getCmbDepa().getPromptText());

            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());

            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            parametros.put("url_imagen", imagen);
            return parametros;
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoInformacionOficialController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private ParametrosInformacionOficial getParamInformacionOficial() {

        ParametrosInformacionOficial paramIO = new ParametrosInformacionOficial();

        String codOdpe = critSelecCtrl.getCmbOdpes().getSelectionModel().getSelectedItem().getCodiOdpe();
        String codCentroComputo = critSelecCtrl.getCmbCC().getSelectionModel().getSelectedItem().getCentCompu();
        String estado = critSelecCtrl.getCmbEstado().getSelectionModel().getSelectedItem();
        String tipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();

        if (!critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento().equals("")) {
            String codDepartamento = critSelecCtrl.getCmbDepa().getSelectionModel().getSelectedItem().getCodDepartamento();
            if (!codDepartamento.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDEPARTAMENTO).equals("")) {
                paramIO.setCodUbigeo(codDepartamento.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDEPARTAMENTO));

            }

        }

        if (!critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia().equals("")) {
            String codProvincia = critSelecCtrl.getCmbProv().getSelectionModel().getSelectedItem().getCodProvincia();
            if (!codProvincia.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODPROVINCIA).equals("")) {
                paramIO.setCodUbigeo(codProvincia.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODPROVINCIA));

            }

        }

        if (!critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito().equals("")) {
            String codDistrito = critSelecCtrl.getCmbDistrito().getSelectionModel().getSelectedItem().getCodDistrito();
            if (!codDistrito.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDISTRITO).equals("")) {
                paramIO.setCodUbigeo(codDistrito.substring(0, ConstantReportes.LONGITUD_UBIGEO_CODDISTRITO));

            }
        }

        paramIO.setCodOdpe(codOdpe.substring(0, ConstantReportes.LONGITUD_COD_ODP));
        paramIO.setCodComputo(codCentroComputo.substring(0, ConstantReportes.LONGITUD_CENTROCOMPUTO));
        paramIO.setTipoElec(tipoEleccion.substring(0, ConstantReportes.LONGITUD_TIPO_ELECCION));

        return paramIO;
    }

    public void RegistrarAuditoria(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Monitoreo de Informacion Oficial - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Monitoreo de Informacion Oficial- Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
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

    public Float getNcandidatos() {
        return Ncandidatos;
    }

    public void setNcandidatos(Float Ncandidatos) {
        this.Ncandidatos = Ncandidatos;
    }

}
