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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
import pe.gob.onpe.suite.domain.RCAvanceResultadoElectoral;
import pe.gob.onpe.suite.domain.RCAvanceResultadoElectoralGrid;
import pe.gob.onpe.suite.domain.RCReporteAvanceResultadosElectorales;
import pe.gob.onpe.suite.reportyconsult.service.RcAvanceResultadosElectoralesService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class AvanceResultadosElectoralesController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane paneSeleccion;

    @FXML
    private TableView<RCAvanceResultadoElectoralGrid> Tabla;

    @FXML
    private TableColumn<RCAvanceResultadoElectoralGrid, String> distElectoral, mesaContab, mesaInstall, xAvance, totVotoValido, OrgPolit, xVotosxOrgPol;

    @FXML
    private TextField totalMesaContab, totalMesaInstalar, totalVotoValido;

    @FXML
    private RadioButton radioButtonDetallado, radioButtonResumido;

    @FXML
    private ToggleGroup consultaToggleGrp;

    String strTipoEleccion = ConstantReportes.VACIO;

    String strReporte = ConstantReportes.VACIO;

    private String name = ConstantCommon.titleReporCons;

    @Autowired
    ApplicationContext context;

    CriterioSeleccionController critSelecCtrl;

    @Autowired
    IComunService comunService;

    @Autowired
    RcAvanceResultadosElectoralesService rcAvanceResultadosElectoralesService;

    List<RCAvanceResultadoElectoral> ListaResult = new ArrayList<>();
    List<RCAvanceResultadoElectoralGrid> aux2 = new ArrayList<>();
    ObservableList<RCAvanceResultadoElectoralGrid> data;

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
            subBarraStage(stage, ConstantReportes.nameTituloAvanceResultadosElectorales, 2);
            radioButtonEvent();
            radioButtonDetallado.setSelected(true);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
            beanDefinitionRegistry.removeBeanDefinition("criterioSeleccion");
            BeanDefinition beanDefinition = new RootBeanDefinition(CriterioSeleccionController.class);
            beanDefinitionRegistry.registerBeanDefinition("criterioSeleccion", beanDefinition);

            critSelecCtrl = context.getBean(CriterioSeleccionController.class);
            critSelecCtrl.setTipoModulo(ConstantReportes.AVANCE_RESULTADOS_ELECTORALES);
            critSelecCtrl.setCodCentroComputo(DatosUsuario.getMstrCentroComputo());
            critSelecCtrl.setShowTipoEleccion(Boolean.TRUE);
            critSelecCtrl.setShowDistritoElectoral(Boolean.TRUE);
            critSelecCtrl.cargarCombos();
            critSelecCtrl.setDefaultStyleCss();
            critSelecCtrl.setLayoutY(10);
//            critSelecCtrl.setLayoutX(50);

            List<Object[]> listaOpciones = new ArrayList<>();
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_TIPOELECCION, critSelecCtrl.getCmbTipoElec(), null, 0, 0},});
            listaOpciones.add(new Object[]{
                new Object[]{ConstantReportes.FILTRO_DISTRITOELECTORAL, critSelecCtrl.getCmbDistritoElectoral(), null, 0, 0},});

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
            Cleaner();
            int UbigeoGana = 0, N_Total_Votos = 0, nCon = 0, nGanador = 0, Mesa_Habil = 0, Mesas_Compu = 0, Votos_Emi = 0, dblPorcMesas = 0, dblPorcVotos = 0, lngMesasComp = 0, lngMesasHabi = 0, lngVotosEmi = 0;
            String UbigeoNuevo = ConstantReportes.VACIO, Ubigeo = ConstantReportes.VACIO;
            String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            String codDistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getCodDistritoElectoral();
            ListaResult = rcAvanceResultadosElectoralesService.getAvancereselectoralCN(codDistritoElectoral, ConstantReportes.TODOS_CCOM_ENUM, codTipoEleccion);
            System.out.println(" codDistritoElectoral :  " + codDistritoElectoral + " CC :  " + ConstantReportes.TODOS_CCOM_ENUM + " codTipoEleccion : " + codTipoEleccion);

            if (ListaResult != null) {
                if (ListaResult.size() < 1) {
                    validador(0);
                    return;
                }

                for (RCAvanceResultadoElectoral aux : ListaResult) {

                    Ubigeo = aux.getC_desc_ubigeo();
                    N_Total_Votos = Integer.parseInt(aux.getN_total_votos());
                    Mesa_Habil = Integer.parseInt(aux.getMesas_habil());
                    Mesas_Compu = Integer.parseInt(aux.getMesas_compu());
                    Votos_Emi = Integer.parseInt(aux.getVotos_emi());
                    dblPorcMesas = 0;
                    dblPorcVotos = 0;

                    if (!Ubigeo.equals(UbigeoNuevo)) {
                        UbigeoNuevo = Ubigeo;
                        UbigeoGana = Integer.parseInt(aux.getN_total_votos());
                        nGanador = 0;
                        System.out.println(" UbigeoNuevo - : " + UbigeoNuevo + " UbigeoGana : " + UbigeoGana);
                    }
                    if (N_Total_Votos == UbigeoGana) {
                        if (Mesa_Habil > 0) {
                            dblPorcMesas = (Mesas_Compu * 100) / Mesa_Habil;
                            System.out.println(" se realizara un porcentaje Mesas : " + dblPorcMesas);
                        } else {
                            dblPorcMesas = 0;
                        }
                        if (Votos_Emi > 0) {
                            dblPorcVotos = (N_Total_Votos * 100) / Votos_Emi;
                            System.out.println(" se realizara un porcentaje Votos : " + dblPorcVotos);
                        } else {
                            dblPorcVotos = 0;
                        }

                        RCAvanceResultadoElectoralGrid bean = new RCAvanceResultadoElectoralGrid();
                        bean.setC_desc_ubigeo(Ubigeo);
                        bean.setMesas_compu(aux.getMesas_compu());
                        bean.setMesas_habil(aux.getMesas_habil());
                        bean.setDblPorcMesas(dblPorcMesas);
                        bean.setVotos_emi(aux.getVotos_emi());
                        bean.setTdesc_agrupo(aux.getTdesc_agrupo());
                        bean.setDblPorcVotos(dblPorcVotos);
                        bean.setN_total_votos(aux.getN_total_votos());
                        aux2.add(bean);

                        if (nGanador == 0) {
                            lngMesasComp = lngMesasComp + Mesas_Compu;
                            lngMesasHabi = lngMesasHabi + Mesa_Habil;
                            lngVotosEmi = lngVotosEmi + Votos_Emi;
                        }
                        nGanador += 1;
                    }
                    nCon += 1;
                }

            }

            distElectoral.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(param.getValue().getC_desc_ubigeo()));
            mesaContab.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(param.getValue().getMesas_compu()));
            mesaInstall.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(param.getValue().getMesas_habil()));
            xAvance.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getDblPorcMesas())));
            totVotoValido.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getVotos_emi())));
            OrgPolit.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getTdesc_agrupo())));
            xVotosxOrgPol.setCellValueFactory((TableColumn.CellDataFeatures<RCAvanceResultadoElectoralGrid, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getDblPorcVotos())));
            data = FXCollections.observableArrayList(aux2);
            Tabla.setItems(data);

            totalMesaContab.setText(String.valueOf(lngMesasComp));
            totalMesaInstalar.setText(String.valueOf(lngMesasHabi));
            totalVotoValido.setText(String.valueOf(lngVotosEmi));
            cmdBotones_Click(1);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    @FXML
    private void btnImprimirOnAction() {
        try {
            String nameReport = radioButtonEvent();
            String codTipoEleccion = critSelecCtrl.getCmbTipoElec().getSelectionModel().getSelectedItem().getTipoElec();
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + nameReport + ".jrxml");
            String DistritoElectoral = critSelecCtrl.getCmbDistritoElectoral().getSelectionModel().getSelectedItem().getDistElec();
            JasperReportUtil jp = new JasperReportUtil();
            Map<String, Object> parametros = new HashMap<>();
            switch (codTipoEleccion) {
                case "10":
                    strTipoEleccion = "P";
                    break;
                case "11":
                    strTipoEleccion = "C";
                    break;
                case "12":
                    strTipoEleccion = "A";
                    break;
            }

            if (ListaResult != null || aux2 != null) {
                if (ListaResult.size() < 1 || aux2.size() < 1) {
                    validador(1);
                    return;
                }
            }
            List<RCReporteAvanceResultadosElectorales> listaReporte = new ArrayList<>();
            if (nameReport.equals(ConstantReportes.nameReporteAvanceResultadoD)) {
                for (RCAvanceResultadoElectoral lista : ListaResult) {
                    RCReporteAvanceResultadosElectorales aux = new RCReporteAvanceResultadosElectorales();
                    aux.setDescripcion(lista.getC_desc_ubigeo());
                    aux.setOrganizacionPolitica(lista.getTdesc_agrupo());
                    aux.setVotosObtenidos(Integer.parseInt(lista.getN_total_votos()));
                    aux.setValEmitido(Integer.parseInt(lista.getVotos_emi()));
                    aux.setMesaHabil(Integer.parseInt(lista.getMesas_habil()));
                    aux.setMesaCompu(Integer.parseInt(lista.getMesas_compu()));
                    listaReporte.add(aux);
                }
            } else {
                for (RCAvanceResultadoElectoralGrid lista : aux2) {
                    RCReporteAvanceResultadosElectorales aux = new RCReporteAvanceResultadosElectorales();
                    aux.setDescripcion(lista.getC_desc_ubigeo());
                    aux.setOrganizacionPolitica(lista.getTdesc_agrupo());
                    aux.setVotosObtenidos(Integer.parseInt(lista.getN_total_votos()));
                    aux.setValEmitido(Integer.parseInt(lista.getVotos_emi()));
                    aux.setMesaHabil(Integer.parseInt(lista.getMesas_habil()));
                    aux.setMesaCompu(Integer.parseInt(lista.getMesas_compu()));
                    listaReporte.add(aux);
                }
            }

            parametros.put("tituloPrincipal", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloAvanceResultadosElectorales);
            parametros.put("reporte", nameReport);
            parametros.put("tipoEleccion", strTipoEleccion);
            parametros.put("Departamento", DistritoElectoral);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("url_imagen", imagen);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);

            StackPane sp = jp.reportShow(parametros, listaReporte, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameTituloAvanceResultadosElectorales;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(2);
        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
        Cleaner();
        cmdBotones_Click(4);
    }

    public String radioButtonEvent() {

        radioButtonDetallado.setUserData("0");
        radioButtonResumido.setUserData("1");

        consultaToggleGrp.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (consultaToggleGrp.getSelectedToggle().getUserData().equals("0")) {
                    strReporte = ConstantReportes.nameReporteAvanceResultadoD;
                } else {
                    strReporte = ConstantReportes.nameReporteAvanceResultadoR;
                }
            }
        });
        return strReporte;
    }

    public void Cleaner() {
        totalMesaContab.setText(ConstantReportes.VACIO);
        totalMesaInstalar.setText(ConstantReportes.VACIO);
        totalVotoValido.setText(ConstantReportes.VACIO);
        Tabla.getItems().clear();
        ListaResult.clear();
        aux2.clear();

    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Avance Resultado Electorales - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 1:
                comunService.registrarAuditoria(mainStage, "Se consulto el reporte Avance de Resultados Electorales - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 2:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte Avance de Resultados Electorales - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
            case 4:
                comunService.registrarAuditoria(mainStage, "Salio de la ventana de Avance Resultado Electorales - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
                break;
        }
    }

    private void validador(int nro) {
        switch (nro) {
            case 0:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada");
                break;
            case 1:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "Debe Consultar Antes de imprimir, No Existen Datos para Mostrar.");
                break;
        }
    }

    public ToggleGroup getConsultaToggleGrp() {
        return consultaToggleGrp;
    }

    public void setConsultaToggleGrp(ToggleGroup consultaToggleGrp) {
        this.consultaToggleGrp = consultaToggleGrp;
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
