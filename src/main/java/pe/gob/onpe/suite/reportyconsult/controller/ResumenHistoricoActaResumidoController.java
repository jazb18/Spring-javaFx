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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatHistoricoActa;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasDEService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;

/**
 *
 * @author kleon
 */
public class ResumenHistoricoActaResumidoController extends AppController implements Initializable {

    private Stage mainStage;
    private Stage stageMessageBox;
    private Scene scene;
    private Stage stage;

    private String msgError = "";

    CriterioSeleccionController critSelecCtrl;
    List<CatHistoricoActa> listHistorico = new ArrayList<>();
    List<CatHistoricoActa> listHistorico01 = new ArrayList<>();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane panelSeleccion;

    @Autowired
    ApplicationContext context;

    @Autowired
    IComunService comunService;

    @Autowired
    RcResultadoActasContabilizadasDEService rcResultadoActasContabilizadasDEService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        subBarraStage(stage, ConstantReportes.nameTituloResumenHistoricoActa, 2);
        try {
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            panelSeleccion.getChildren().clear();
            critSelecCtrl = (CriterioSeleccionController) context.getBean("criterioSeleccion");
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo()); //C30000
            critSelecCtrl.setShowTipoEleccion(true);
            critSelecCtrl.setShowtextFieldNumMesa(true);

            critSelecCtrl.setTipoModulo(ConstantReportes.RESUMEN_HOSTORICO_ACTA);

            critSelecCtrl.setDefaultStyleCss();

            critSelecCtrl.cargarCombos();
//            Lista de los Filtros
            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), 300, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_NUMERO_MESA, critSelecCtrl.getTextFieldNumMesa(), 100, 0, 1},});
            critSelecCtrl.dibujarCombos(listaOpciones);
            panelSeleccion.getChildren().add(critSelecCtrl);
            filtroKeyEvent();
            cmdBotones_Click(0);
        } catch (Exception ex) {
            System.out.println("Excepcion General al momento de inicializar :" + ex);
        }

    }

    @FXML
    private void btnImprimirOnAction() {

        String numMesaTxt = critSelecCtrl.getTextFieldNumMesa().getText();
        String tipoEleccion = critSelecCtrl.getCmbTipoElec().getValue().getTipoElec();

        if (esValido(numMesaTxt)) {
            procesarConsulta(tipoEleccion, numMesaTxt);

            if (listHistorico.size() > 0 || listHistorico01.size() > 0) {

                if (listHistorico01.isEmpty()) {
                    iniciarLista();
                }

                String title = ConstantReportes.nameTituloResumenHistoricoUnActa;
                String nameReport = ConstantReportes.nameReporteResumenHistoricoActa;
                InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
                /*Cargando los parametros del reporte*/
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("reporte", nameReport);
                parametros.put("numMesa", numMesaTxt);
                parametros.put("urlsubreport", ConstantReportes.pathReportConsult);
                parametros.put("listHistorico", new JRBeanCollectionDataSource(listHistorico01));
                cargaParametros(parametros);

                imprimirReporte(listHistorico, parametros, file, title);
                cmdBotones_Click(2);
            } else {
                mostrarMensaje(ConstantReportes.NO_EXISTE_INFORMACIONPARAMOSTRAR);
            }

        } else {
            mostrarMensaje(msgError);
        }
    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
        cmdBotones_Click(3);
    }

    public boolean esValido(String value) {

        boolean valido = true;
        value = value.trim();
        if (value.equals("")) {
            msgError = "Debe indicar la mesa a consultar";
            valido = false;
        } else if (value.length() < ConstantReportes.MAXLENGTH_NUMMESA || value.length() > ConstantReportes.MAXLENGTH_NUMMESA) {
            msgError = "N° de mesa es de 6 digitos";
            valido = false;
        }

        return valido;

    }

    public void mostrarMensaje(String msg) {

        String title = ConstantCommon.titleValidacion;
        String message = msg;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, title, message, null, null, null, eventOK);

    }

    private void procesarConsulta(String tipoEleccion, String numMesa) {

        try {
            listHistorico = new ArrayList<>();
            listHistorico = rcResultadoActasContabilizadasDEService.getHistoricoActas(numMesa, "00", tipoEleccion);
            System.out.println("Longitud del listado 0 :" + listHistorico.size());
            listHistorico01 = new ArrayList<>();
            listHistorico01 = rcResultadoActasContabilizadasDEService.getHistoricoActas(numMesa, "01", tipoEleccion);
            System.out.println("Longitud del listado 1 :" + listHistorico01.size());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void filtroKeyEvent() {

        critSelecCtrl.getTextFieldNumMesa().addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (!ConstantReportes.NUMBER.contains(event.getCharacter())) {
                    event.consume();
                }
                if (critSelecCtrl.getTextFieldNumMesa().getText().length() >= ConstantReportes.MAXLENGTH_NUMMESA) {
                    event.consume();
                }
            }
        });

    }

    private void imprimirReporte(List<CatHistoricoActa> listResumActascontbDE, Map<String, Object> parametros, InputStream file, String title) {
        try {
            JasperReportUtil jp = new JasperReportUtil();

            StackPane sp = jp.reportShow(parametros, listResumActascontbDE, file, null);

            AppController appController = new AppController();
            appController.subBarraStageReport(mainStage, sp, title, 2);
        } catch (Exception ex) {
            System.out.println("Excepcion general :" + ex);
            ex.printStackTrace();
        }
    }

    public Map<String, Object> cargaParametros(Map<String, Object> parametros) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            parametros.put("tituloGeneral", ConstantReportes.nameTituloMonitInfEleccionesGenerales);

            parametros.put("nombreCorto", critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getNombreEleccion());
            parametros.put("tituloRep", ConstantReportes.nameTituloResumenHistoricoUnActa);

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
            Logger.getLogger(ResumenResultadoActasContabilizadasController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void iniciarLista() {
        CatHistoricoActa beanNew = new CatHistoricoActa();
        beanNew.setNumResolucion("");
        beanNew.setProcedResolucion("");
        beanNew.setFechaRegistroLog("");
        beanNew.setUsuarioNombre("");
        listHistorico01.add(beanNew);
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Resumne Historico de Actas Resumido - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte de Resumne Historico de Actas Resumido - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de Resumne Historico de Actas Resumido - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
                break;
            case 3:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Resumne Historico de Actas Resumido - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
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
