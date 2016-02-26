/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatDepartamento;
import pe.gob.onpe.suite.domain.CatDistrito;
import pe.gob.onpe.suite.domain.CatLocal;
import pe.gob.onpe.suite.domain.CatProvincia;
import pe.gob.onpe.suite.domain.DetalleUbigeo;
import pe.gob.onpe.suite.domain.HistoriaMesa;
import pe.gob.onpe.suite.domain.Mesa;
import pe.gob.onpe.suite.domain.MesaElecta;
import pe.gob.onpe.suite.domain.VotosXAgrup;
import pe.gob.onpe.suite.reportyconsult.service.RcMonitoreodeActaService;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class MonitoreoActasController extends AppController implements Initializable {

    private Stage mainStage;
    private Stage stageMessageBox;
    private Scene scene;
    private Stage stage;
    Timer timer;
    private boolean verifyTask = false;
    private static boolean finishedTask = false;
    public String minutes = "";
    public String nroActa = "";

    @Autowired()
    RcMonitoreodeActaService rcMonitoreodeActaService;
    @Autowired
    IComunService comunService;
    @Autowired()
    ApplicationContext context;

    List<CatDepartamento> listDepartamentos;
    List<CatProvincia> listProvincias;
    List<CatDistrito> listDistrito;
    List<CatLocal> listLocal;
    List<Mesa> listMesa;
    List<MesaElecta> listMesaElecta;
    List<HistoriaMesa> listHistoriaMesa;

    TreeItem<Object> rootNode;
    TreeItem<Object> nodoDepartamento;
    TreeItem<Object> nodoProvincia;
    TreeItem<Object> nodoDistrito;
    TreeItem<Object> nodoLocal;
    TreeItem<Object> nodoMesa;
    TreeItem<Object> nodoMesaElecta;
    TreeItem<Object> nodoHistoriaMesa;

    HashMap<String, String> mapCodDeparment = new HashMap<>();
    HashMap<String, String> mapCodProvincia = new HashMap<>();
    HashMap<String, String> mapCodDistrito = new HashMap<>();
    HashMap<String, String> mapCodLocal = new HashMap<>();
    HashMap<String, String> mapCodMesa = new HashMap<>();
    HashMap<String, String> mapCodMesaElecta = new HashMap<>();
    HashMap<String, String> mapCodHistoriaMesa = new HashMap<>();
    HashMap<String, String> mapCodGlobal = new HashMap<>();

    HashMap<String, TreeItem<Object>> mapNodeDeparment = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodeProvincia = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodedistrito = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodeLocal = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodeMesa = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodeMesaElecta = new HashMap<>();
    HashMap<String, TreeItem<Object>> mapNodeHistoriaMesa = new HashMap<>();

    private final Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/root.png")));
    private final Image depIcon = new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/nodoDep.png"));
    private final Image locIcon = new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/nodoloc.png"));
    private final Image mesaIcon = new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/nodomesa.png"));
    private final Image mesaElectIcon = new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/nodoMesElec.png"));
    private final Image hitoriaMesaElectIcon = new Image(getClass().getResourceAsStream("/pe/gob/onpe/suite/reportyconsult/view/image/nodoHist.png"));

    @FXML
    private Button btnDetalle, btnActa, btnRefresh;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView tableReporte;
    @FXML
    private Label lblheader, lblTimeminuts;
    @FXML
    private TextField NroMesa, txtSufragioPre, txtSumVotosPre, txtAusentismoPre, txtElecHabilesPre, txtSufragioCongre, txtSumVotosCongre, txtAusentismoCongre, txtElecHabilesCongre,
            txtSufragioPar, txtSumVotosPar, txtAusentismoPar, txtElecHabilesPar, txtLote, txtnroActa, txtEstadoMesa, txtUsuaModif, txtFechaModif, txtPresidenteActa, txtPresidenteProc,
            txtObservacionPre, txtObservacionCongre, txtObservacionPar, txtminutes, txtCongresistaActa, txtParlamentoActa, txtCongresistaProc, txtParlamentoProc;
    @FXML
    private TreeView treeview;
    @FXML
    private TableView<VotosXAgrup> tablaCongresista;
    @FXML
    private TableView<VotosXAgrup> tablaPresidente;
    @FXML
    private TableView<VotosXAgrup> tablaParAndino;

    @FXML
    private TableColumn<VotosXAgrup, String> ColumnNroPre, ColumnCandidatosPre, ColumnVotosPre;
    @FXML
    private TableColumn<VotosXAgrup, String> ParlamenNro, ParlamenCandidatos, ParlamenVotos;
    @FXML
    private TableColumn<VotosXAgrup, String> Parlamen1, Parlamen2, Parlamen3, Parlamen4, Parlamen5, Parlamen6, Parlamen7, Parlamen8, Parlamen9, Parlamen10, Parlamen11, Parlamen12, Parlamen13, Parlamen14, Parlamen15;

    private TableColumn<VotosXAgrup, String> congreNro, congreCandidatos, congreVotos;

    private TableColumn<VotosXAgrup, String> congre1, congre2, congre3, congre4, congre5, congre6, congre7, congre8, congre9, congre10, congre11, congre12, congre13, congre14, congre15, congre16, congre17, congre18, congre19, congre20, congre21, congre22, congre23, congre24, congre25, congre26, congre27, congre28, congre29, congre30, congre31, congre32, congre33, congre34, congre35, congre36;
    private List<TableColumn> listColumns = Arrays.asList(congre1, congre2, congre3, congre4, congre5, congre6, congre7, congre8, congre9, congre10, congre11, congre12, congre13, congre14, congre15, congre16, congre17, congre18, congre19, congre20, congre21, congre22, congre23, congre24, congre25, congre26, congre27, congre28, congre29, congre30, congre31, congre32, congre33, congre34, congre35, congre36);

    ObservableList<VotosXAgrup> tablevaluesPresidente, tablevaluesCongresales, tablevaluesParlamento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void init() {
        try {
            stage = new Stage();
            stage.initOwner(mainStage);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setX(10);
            stage.setY(70);
            //stage.centerOnScreen();
            //stage.sizeToScene();
            scene = new Scene(this.getView());
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);

            stage.setScene(scene);
            stage.show();
            subBarraStage(stage, "MODULO DE REPORTES Y CONSULTAS - MONITOREO DE ACTAS", 2);

            AppController.addMaxLengthRestriction(NroMesa, 6); //validacion del maximo de caracteres
            AppController.addPatternRestriction(NroMesa, "[0-9]"); //validacion de solo numeros

            filtroKeyEvent();
            inicializarDatos();
            limpiarDetalle();
            NroMesa.setText("");
            tvTreeView_Expand();
            comunService.registrarAuditoria(mainStage, "Ingreso a la ventana de Monitoreo de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnSalirOnAction() {
        if (timer != null) {
            System.out.println("Candelando el timer local");
            timer.cancel();
            timer.purge();
        }
        stage.close();
        comunService.registrarAuditoria(mainStage, "Salio de la ventana de Monitoreo de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
    }

    private void tvTreeView_Expand() {

        listDepartamentos = rcMonitoreodeActaService.getDepartamento(DatosUsuario.getMstrCentroComputo());
        rootNode = new TreeItem<>("CC-Nacion", rootIcon);

        for (Iterator<CatDepartamento> iterator = listDepartamentos.iterator(); iterator.hasNext();) {
            CatDepartamento next = iterator.next();

            if (next.getCodigo() instanceof Object) {
                Object codigoDepartamento = (Object) next.getCodigo().substring(0, 2);
                Node nodeDepartament = new ImageView(depIcon);
                nodeDepartament.setId("2" + "-" + codigoDepartamento.toString());
                nodoDepartamento = new TreeItem<>(next.getDescripcion(), nodeDepartament); //cargando el departamento en le three item
                mapNodeDeparment.put(codigoDepartamento.toString(), nodoDepartamento);

                nodoDepartamento.expandedProperty().addListener(new ChangeListener() { //Accion de expansion del nodo departamento

                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                        Object codigoDepartame = (Object) next.getCodigo().substring(0, 2);
                        TreeItem<Object> recoverNodeDepart = mapNodeDeparment.get(codigoDepartame.toString());
                        listProvincias = rcMonitoreodeActaService.getProvincia((String) codigoDepartame, DatosUsuario.getMstrCentroComputo());
                        recoverNodeDepart.getChildren().clear();
                        for (Iterator<CatProvincia> iterator1 = listProvincias.iterator(); iterator1.hasNext();) {
                            CatProvincia next1 = iterator1.next();

                            if (next1.getCodigo().substring(0, 2).equals(codigoDepartame.toString())) {

                                Object codigoProvincia = (Object) next1.getCodigo().substring(0, 4);
                                Node nodeProvincia = new ImageView(depIcon);
                                nodeProvincia.setId("3" + "-" + codigoProvincia.toString());
                                nodoProvincia = new TreeItem<>(next1.getDescripcion(), nodeProvincia);
                                mapNodeProvincia.put(codigoProvincia.toString(), nodoProvincia); //cargando los nodos de provincia

                                nodoProvincia.expandedProperty().addListener(new ChangeListener() { //action de expansion del nodo de provincia

                                    @Override
                                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                                        Object codigoProvincia = (Object) next1.getCodigo().substring(0, 4);
                                        TreeItem<Object> recoverNodeProv = mapNodeProvincia.get(codigoProvincia.toString());
                                        listDistrito = rcMonitoreodeActaService.getDistrito(codigoProvincia.toString(), DatosUsuario.getMstrCentroComputo());
                                        recoverNodeProv.getChildren().clear();
                                        for (Iterator<CatDistrito> iterator2 = listDistrito.iterator(); iterator2.hasNext();) {
                                            CatDistrito next2 = iterator2.next();

                                            if (next2 instanceof Object) {

                                                Object codDistrito = next2.getCodigo();
                                                Node nodeDistrito = new ImageView(depIcon);
                                                nodeDistrito.setId("4" + "-" + codDistrito.toString());
                                                nodoDistrito = new TreeItem<>(next2.getDescripcion(), nodeDistrito);
                                                mapNodedistrito.put(codDistrito.toString(), nodoDistrito);

                                                nodoDistrito.expandedProperty().addListener(new ChangeListener() {
                                                    @Override

                                                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                                                        Object codUbigueoDistrito = next2.getCodigo();
                                                        TreeItem<Object> recoverNodeDistrit = mapNodedistrito.get(codUbigueoDistrito.toString());
                                                        listLocal = rcMonitoreodeActaService.getLocal("", (String) codUbigueoDistrito);
                                                        recoverNodeDistrit.getChildren().clear();
                                                        for (Iterator<CatLocal> iterator3 = listLocal.iterator(); iterator3.hasNext();) {
                                                            CatLocal next3 = iterator3.next();

                                                            if (next3 instanceof Object) {

                                                                Object codigoLocal = next3.getCodiLocal();
                                                                Node nodeLocal = new ImageView(locIcon);
                                                                nodeLocal.setId("5" + "-" + codigoLocal.toString());
                                                                nodoLocal = new TreeItem<>(next3.getNombreLocal(), nodeLocal);
                                                                mapNodeLocal.put(codigoLocal.toString(), nodoLocal);

                                                                nodoLocal.expandedProperty().addListener(new ChangeListener() {

                                                                    @Override
                                                                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                                                                        Object codlocal = next3.getCodiLocal();
                                                                        TreeItem<Object> recoverNodelocal = mapNodeLocal.get(codlocal.toString());
                                                                        listMesa = rcMonitoreodeActaService.getMesa(codlocal.toString(), codUbigueoDistrito.toString(), "");
                                                                        recoverNodelocal.getChildren().clear();

                                                                        for (Iterator<Mesa> iterator4 = listMesa.iterator(); iterator4.hasNext();) {
                                                                            Mesa next4 = iterator4.next();

                                                                            if (next4 instanceof Object) {

                                                                                Object codigoMesa = (Object) next4.getNumeMesa();
                                                                                Node nodeMesa = new ImageView(mesaIcon);
                                                                                nodeMesa.setId("6" + "-" + codigoMesa.toString());
                                                                                nodoMesa = new TreeItem<>(next4.getEstadoMesa(), nodeMesa);
                                                                                mapNodeMesa.put(codigoMesa.toString(), nodoMesa);

                                                                                nodoMesa.expandedProperty().addListener(new ChangeListener() {

                                                                                    @Override
                                                                                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                                                                        System.out.println("Ingreso al action de expansion de la Mesa ");
                                                                                        Object codigoMesa = (Object) next4.getNumeMesa();
                                                                                        TreeItem<Object> recoverNodeMesa = mapNodeMesa.get((String) codigoMesa);
                                                                                        listMesaElecta = rcMonitoreodeActaService.getMesaeleccion(codigoMesa.toString());
                                                                                        recoverNodeMesa.getChildren().clear();
                                                                                        for (Iterator<MesaElecta> iterator5 = listMesaElecta.iterator(); iterator5.hasNext();) {
                                                                                            MesaElecta next5 = iterator5.next();

                                                                                            if (next5 instanceof Object) {

                                                                                                Object codMesaElecta = (Object) next5.getCodigo();
                                                                                                Node nodeMesaElec = new ImageView(mesaElectIcon);
                                                                                                nodeMesaElec.setId("7" + "-" + codMesaElecta.toString() + "-" + next5.getOrden());
                                                                                                nodoMesaElecta = new TreeItem<>(next5.getDescripcion(), nodeMesaElec);
                                                                                                mapNodeMesaElecta.put("7" + "-" + codMesaElecta.toString() + "-" + next5.getOrden(), nodoMesaElecta);

                                                                                                nodoMesaElecta.expandedProperty().addListener(new ChangeListener() {

                                                                                                    @Override
                                                                                                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                                                                                        Object nroActa = (Object) codMesaElecta.toString();
                                                                                                        Object tipoEleccion = (Object) next5.getOrden();

                                                                                                        TreeItem<Object> recoverNodeMesaElecta = mapNodeMesaElecta.get("7" + "-" + nroActa.toString() + "-" + tipoEleccion.toString());
                                                                                                        System.out.println("NroActa : " + nroActa.toString() + "------" + "tipo de eleccion : " + tipoEleccion.toString());
                                                                                                        listHistoriaMesa = rcMonitoreodeActaService.getHistoriaMesa(nroActa.toString(), tipoEleccion.toString());
                                                                                                        System.out.println("Longitud de la lista de mesa Historica : " + listHistoriaMesa.size());
                                                                                                        recoverNodeMesaElecta.getChildren().clear();
                                                                                                        mapCodGlobal.clear();
                                                                                                        for (Iterator<HistoriaMesa> iterator6 = listHistoriaMesa.iterator(); iterator6.hasNext();) {
                                                                                                            HistoriaMesa next6 = iterator6.next();

                                                                                                            if (next6.getC_nume_acta().equals(nroActa)) {
                                                                                                                System.out.println("Entro a la comparacion");
                                                                                                                Object codigoHistorico = (Object) next6.getCodigo();
                                                                                                                Node nodeHistoricoMesa = new ImageView(hitoriaMesaElectIcon);
                                                                                                                nodeHistoricoMesa.setId("8" + "-" + "X" + codigoHistorico.toString() + "D" + next6.getOrden() + next6.getProceso());
                                                                                                                nodoHistoriaMesa = new TreeItem<>(next6.getDescripcion(), nodeHistoricoMesa);
                                                                                                                mapNodeHistoriaMesa.put(codigoHistorico.toString(), nodoHistoriaMesa);

                                                                                                            }
                                                                                                            recoverNodeMesaElecta.getChildren().add(nodoHistoriaMesa);
                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                                nodoMesaElecta.getChildren().add(new TreeItem<>(""));
                                                                                            }
                                                                                            recoverNodeMesa.getChildren().add(nodoMesaElecta);
                                                                                        }
                                                                                    }
                                                                                });
                                                                                nodoMesa.getChildren().add(new TreeItem<>(""));
                                                                            }
                                                                            recoverNodelocal.getChildren().add(nodoMesa);
                                                                        }
                                                                    }
                                                                });
                                                                nodoLocal.getChildren().add(new TreeItem<>(""));
                                                            }
                                                            recoverNodeDistrit.getChildren().add(nodoLocal);
                                                        }
                                                    }
                                                });
                                                nodoDistrito.getChildren().add(new TreeItem<>(""));
                                            }
                                            recoverNodeProv.getChildren().add(nodoDistrito);
                                        }

                                    }
                                });
                                nodoProvincia.getChildren().add(new TreeItem<>(""));
                            }

                            recoverNodeDepart.getChildren().add(nodoProvincia);

                        }
                    }
                });

                nodoDepartamento.setExpanded(false);
                nodoDepartamento.getChildren().add(new TreeItem<>(""));
            }
            rootNode.getChildren().add(nodoDepartamento);
        }
        rootNode.setExpanded(true);
        treeview.setRoot(rootNode);

        tvTreeView_NodeClick(treeview);
    }

    private void tvTreeView_NodeClick(TreeView treeview) {
        treeview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue,
                    Object newValue) {
                finishedTask = false;
                tablaCongresista.getColumns().clear();
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                if (selectedItem == null || selectedItem.getGraphic() == null || selectedItem.getGraphic().getId() == null) {
                    System.out.println("El id recuperado es nulo");
                    limpiarTablas();
                    limpiarDetalle();
                    return;
                }
                Integer indexSelect = treeview.getSelectionModel().getSelectedIndex();
                System.out.println("###ID DE LA SELECCION  : " + selectedItem.getGraphic().getId());
                String[] arrayId = selectedItem.getGraphic().getId().split("-");
                String nivel = arrayId[0];
                String codigo = arrayId[1];
                switch (nivel) {
                    case "2":
                        System.out.println("##Seleccion Departamento :");
                        System.out.println("##Codigo :" + codigo);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotos = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PRESIDENCIAL);
                        cargarTablaPresidente(listVotos);
                        System.out.println("##Cantidad de archivos PRE :" + listVotos.size());
                        listVotos = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_CONGRESAL);
                        System.out.println("##Cantidad de archivos CON :" + listVotos.size());
                        cargarTablaCongresales(listVotos);
                        listVotos = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PARLAMENTOANDINO);
                        cargarTablaParlamentoAndino(listVotos);
                        System.out.println("##Cantidad de archivos PAR :" + listVotos.size());
                        break;
                    case "3":
                        System.out.println("##Seleccion Provincia :");
                        System.out.println("##Codigo :" + codigo);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotosP = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PRESIDENCIAL);
                        cargarTablaPresidente(listVotosP);
                        System.out.println("##Cantidad de archivos P :" + listVotosP.size());
                        listVotosP = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_CONGRESAL);
                        cargarTablaCongresales(listVotosP);
                        System.out.println("##Cantidad de archivos C :" + listVotosP.size());
                        listVotosP = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PARLAMENTOANDINO);
                        cargarTablaParlamentoAndino(listVotosP);
                        System.out.println("##Cantidad de archivos PAR :" + listVotosP.size());
                        break;
                    case "4":
                        System.out.println("##Seleccion Distrito :");
                        System.out.println("##Codigo :" + codigo);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotosD = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PRESIDENCIAL);
                        cargarTablaPresidente(listVotosD);
                        System.out.println("##Cantidad de archivos P :" + listVotosD.size());
                        listVotosD = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_CONGRESAL);
                        cargarTablaCongresales(listVotosD);
                        System.out.println("##Cantidad de archivos C :" + listVotosD.size());
                        listVotosD = rcMonitoreodeActaService.ObtenerVotosAgruPol_Ubigeo(codigo, ConstantReportes.TIPOELECCION_PARLAMENTOANDINO);
                        cargarTablaParlamentoAndino(listVotosD);
                        System.out.println("##Cantidad de archivos PAR :" + listVotosD.size());
                        break;
                    case "5":
                        String[] arrayIdParent = selectedItem.getParent().getGraphic().getId().split("-");
                        String nivelParent = arrayIdParent[0];
                        String codigoParent = arrayIdParent[1];
                        System.out.println("##Seleccion Local :");
                        System.out.println("##Codigo :" + codigo);
                        System.out.println("##CodigoParent :" + codigoParent);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotosL = rcMonitoreodeActaService.ObtenerVotosAgruPol_Local(codigo, codigoParent, ConstantReportes.TIPOELECCION_PRESIDENCIAL);
                        cargarTablaPresidente(listVotosL);
                        System.out.println("##Cantidad de archivos P :" + listVotosL.size());
                        listVotosL = rcMonitoreodeActaService.ObtenerVotosAgruPol_Local(codigo, codigoParent, ConstantReportes.TIPOELECCION_CONGRESAL);
                        cargarTablaCongresales(listVotosL);
                        System.out.println("##Cantidad de archivos C :" + listVotosL.size());
                        listVotosL = rcMonitoreodeActaService.ObtenerVotosAgruPol_Local(codigo, codigoParent, ConstantReportes.TIPOELECCION_PARLAMENTOANDINO);
                        cargarTablaParlamentoAndino(listVotosL);
                        System.out.println("##Cantidad de archivos PAR :" + listVotosL.size());
                        break;
                    case "6":
                        System.out.println("##Seleccion Mesa :");
                        System.out.println("##Codigo :" + codigo);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotosM = rcMonitoreodeActaService.ObtenerVotosAgruPol_Acta(codigo, ConstantReportes.TIPOELECCION_PRESIDENCIAL);
                        cargarTablaPresidente(listVotosM);
                        cargarDetalleMesa(ConstantReportes.TIPOELECCION_PRESIDENCIAL, codigo, listVotosM, nivel, "");
                        System.out.println("##Cantidad de archivos P :" + listVotosM.size());
                        listVotosM = rcMonitoreodeActaService.ObtenerVotosAgruPol_Acta(codigo, ConstantReportes.TIPOELECCION_CONGRESAL);
                        cargarTablaCongresales(listVotosM);
                        cargarDetalleMesa(ConstantReportes.TIPOELECCION_CONGRESAL, codigo, listVotosM, nivel, "");
                        System.out.println("##Cantidad de archivos C :" + listVotosM.size());
                        listVotosM = rcMonitoreodeActaService.ObtenerVotosAgruPol_Acta(codigo, ConstantReportes.TIPOELECCION_PARLAMENTOANDINO);
                        cargarTablaParlamentoAndino(listVotosM);
                        cargarDetalleMesa(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO, codigo, listVotosM, nivel, "");
                        System.out.println("##Cantidad de archivos PAR :" + listVotosM.size());
                        break;
                    case "7":
                        String tipoEleccion = arrayId[2];
                        System.out.println("##Seleccion Mesa Electa :");
                        System.out.println("##Codigo :" + codigo);
                        System.out.println("##Tipo de eleccion :" + tipoEleccion);
                        limpiarDetalle();
                        List<VotosXAgrup> listVotosME = rcMonitoreodeActaService.ObtenerVotosAgruPol_Acta(codigo, tipoEleccion);

                        switch (tipoEleccion) {
                            case "10":
                                System.out.println("##Cantidad de archivos P :" + listVotosME.size());
                                cargarTablaPresidente(listVotosME);
                                cargarTablaCongresales(new ArrayList<>());
                                cargarTablaParlamentoAndino(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_PRESIDENCIAL, codigo, listVotosME, nivel, "");
                                break;
                            case "11":
                                System.out.println("##Cantidad de archivos C :" + listVotosME.size());
                                cargarTablaCongresales(listVotosME);
                                cargarTablaPresidente(new ArrayList<>());
                                cargarTablaParlamentoAndino(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_CONGRESAL, codigo, listVotosME, nivel, "");
                                break;
                            case "12":
                                System.out.println("##Cantidad de archivos PAR :" + listVotosME.size());
                                cargarTablaParlamentoAndino(listVotosME);
                                cargarTablaPresidente(new ArrayList<>());
                                cargarTablaCongresales(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO, codigo, listVotosME, nivel, "");
                                break;
                        }

                        break;
                    case "8":
                        System.out.println("##Seleccion Mesa Historica :");
                        System.out.println("##Codigo :" + codigo);
                        limpiarDetalle();
                        String nrActa = codigo.substring(1, 7);
                        String nroDigita = codigo.substring(7, 9);
                        String nroLote = codigo.substring(11, 17);
                        String tipoLote = codigo.substring(9, 11);
                        String copiaActa = codigo.substring(17, 20);
                        String nDigitacion = codigo.substring(20, 21);//nuevo parametro
                        String tipEleccion = codigo.substring(23);
                        List<VotosXAgrup> listVotosMH = new ArrayList<>();
                        if (nroDigita.equals("01")) {
                            listVotosMH = rcMonitoreodeActaService.ObtenerVotosAgruPol_Digitacion(nrActa, tipEleccion, nDigitacion, tipoLote, nroDigita, nroLote, copiaActa, ConstantReportes.STOREP_MONITOREOACTA_DIG1);
                        } else {
                            listVotosMH = rcMonitoreodeActaService.ObtenerVotosAgruPol_Digitacion(nrActa, tipEleccion, nDigitacion, tipoLote, nroDigita, nroLote, copiaActa, ConstantReportes.STOREP_MONITOREOACTA_DIG2);
                        }
                        switch (tipEleccion) {
                            case "10":
                                cargarTablaPresidente(listVotosMH);
                                cargarTablaCongresales(new ArrayList<>());
                                cargarTablaParlamentoAndino(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_PRESIDENCIAL, nrActa, listVotosMH, nivel, codigo);
                                break;
                            case "11":
                                cargarTablaCongresales(listVotosMH);
                                cargarTablaPresidente(new ArrayList<>());
                                cargarTablaParlamentoAndino(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_CONGRESAL, nrActa, listVotosMH, nivel, codigo);
                                break;
                            case "12":
                                cargarTablaParlamentoAndino(listVotosMH);
                                cargarTablaPresidente(new ArrayList<>());
                                cargarTablaCongresales(new ArrayList<>());
                                cargarDetalleMesa(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO, nrActa, listVotosMH, nivel, codigo);
                                break;
                        }

                        break;
                    case "9":
                        break;
                }
                System.out.println("Index seleccionado : " + indexSelect);
                if (indexSelect - 5 > 0) {
                    treeview.scrollTo(indexSelect - 5);
                } else {
                    treeview.scrollTo(1);
                }

                System.out.println("------------------------------------------------------------------------");
                finishedTask = true;
            }

        });
    }

    @FXML
    public void btnActaOnAction() {

        TreeItem itemAux = (TreeItem) treeview.getSelectionModel().getSelectedItem();
        String[] arrayNode = itemAux.getGraphic().getId().split("-");
        String nivel = arrayNode[0];
        String codigo = arrayNode[1];

        System.out.println("nivel " + nivel);
        System.out.println("codigo " + codigo);
        ActaObservacionesController actaObservacionesController = context.getBean(ActaObservacionesController.class);
        actaObservacionesController.setObservPre(txtObservacionPre.getText().trim());
        actaObservacionesController.setObservCong(txtObservacionCongre.getText().trim());
        actaObservacionesController.setObservPar(txtObservacionPar.getText().trim());
        actaObservacionesController.setEstadoProcPre(txtPresidenteProc.getText().trim());
        actaObservacionesController.setEstadoProcCong(txtCongresistaProc.getText().trim());
        actaObservacionesController.setEstadoProcPar(txtParlamentoProc.getText().trim());
        actaObservacionesController.setMainStage(mainStage);
        actaObservacionesController.setErrorMaterial("A");
        actaObservacionesController.setNroActa(nivel.equals("8") ? codigo.substring(1, 7) : codigo);
        actaObservacionesController.setDigita((nivel.equals("6") ? "0" : (nivel.equals("7") ? "0" : (nivel.equals("8") ? codigo.substring(7, 9) : ""))));
        actaObservacionesController.setNroLote((nivel.equals("6") ? "" : (nivel.equals("7") ? "" : (nivel.equals("8") ? codigo.substring(11, 17) : ""))));
        actaObservacionesController.initWindows();
    }

    @FXML
    public void btnDetalleOnAction() {

        TreeItem itemAux = (TreeItem) treeview.getSelectionModel().getSelectedItem();
        String[] arrayNode = itemAux.getGraphic().getId().split("-");
        String nivel = arrayNode[0];
        String codigo = arrayNode[1];

        System.out.println("nivel " + nivel);
        System.out.println("codigo " + codigo);

        ActaObservacionesController actaObservacionesController = context.getBean(ActaObservacionesController.class);
        actaObservacionesController.setObservPre(txtObservacionPre.getText().trim());
        actaObservacionesController.setObservCong(txtObservacionCongre.getText().trim());
        actaObservacionesController.setObservPar(txtObservacionPar.getText().trim());
        actaObservacionesController.setEstadoProcPre(txtPresidenteProc.getText().trim());
        actaObservacionesController.setEstadoProcCong(txtCongresistaProc.getText().trim());
        actaObservacionesController.setEstadoProcPar(txtParlamentoProc.getText().trim());
        actaObservacionesController.setMainStage(mainStage);
        actaObservacionesController.setErrorMaterial("D");
        actaObservacionesController.setNroActa(nivel.equals("8") ? codigo.substring(1, 7) : codigo);
        actaObservacionesController.setDigita((nivel.equals("6") ? "0" : (nivel.equals("7") ? "0" : (nivel.equals("8") ? codigo.substring(7, 9) : ""))));
        actaObservacionesController.setNroLote((nivel.equals("6") ? "" : (nivel.equals("7") ? "" : (nivel.equals("8") ? codigo.substring(11, 17) : ""))));
        actaObservacionesController.initWindows();

    }

    @FXML
    public void btnConfTiempo() {
        /*Configuracion del tiempo para la consulta automatica*/
        if (txtminutes.isVisible()) {
            txtminutes.setVisible(false);
            btnRefresh.setDisable(false);
            lblTimeminuts.setVisible(false);
        } else {
            txtminutes.setVisible(true);
            btnRefresh.setDisable(true);
            lblTimeminuts.setVisible(true);
        }
    }

    @FXML
    public void btnRefrescar() {
        /*Ejecutar la consulta automaticamente*/
        try {

            if (treeview.getSelectionModel() == null) {
                return;
            }
            Integer indexSelect = treeview.getSelectionModel().getSelectedIndex();
            TreeItem itemAux = (TreeItem) treeview.getSelectionModel().getSelectedItem();

            if (indexSelect.equals(0)) {
                System.out.println("El index seleccionado es el padre ");
                return;
            } else {
                System.out.println("El index seleccionado no pertenece al padre ");
                treeview.getSelectionModel().select(0);
                treeview.getSelectionModel().select(itemAux);
            }

            System.out.println("Select Index : " + indexSelect);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTablaPresidente(List<VotosXAgrup> listConsult) {
        if (tablevaluesPresidente != null) {
            tablevaluesPresidente.clear();
        }
        ColumnNroPre.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getNroAgrupol()));
        ColumnCandidatosPre.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getDescAgrupol()));
        ColumnCandidatosPre.setStyle("-fx-alignment: CENTER-LEFT;");
        ColumnVotosPre.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getNrovotos()));
        tablevaluesPresidente = FXCollections.observableArrayList(listConsult);
        tablaPresidente.setItems(tablevaluesPresidente);
    }

    private void cargarTablaCongresales(List<VotosXAgrup> listConsult) {
        /*Carga de la tabla congresales para el monitoreo de actas*/
        if (listConsult.size() <= 0) {
            return;
        }
        congreNro = new TableColumn();
        congreNro.setText("Nro");
        congreNro.setCellValueFactory(new PropertyValueFactory("nroAgrupol"));
        congreNro.setMinWidth(50);

        congreCandidatos = new TableColumn();
        congreCandidatos.setText("Organizacion Politica");
        congreCandidatos.setCellValueFactory(new PropertyValueFactory("descAgrupol"));
        congreCandidatos.setMinWidth(443);
        congreCandidatos.setStyle("-fx-alignment: CENTER-LEFT;");

        congreVotos = new TableColumn();
        congreVotos.setText("Votos");
        congreVotos.setCellValueFactory(new PropertyValueFactory("nrovotos"));
        congreVotos.setMinWidth(93);
        tablaCongresista.getColumns().addAll(congreNro, congreCandidatos, congreVotos);

        System.out.println("Cantidad de columnas :" + listConsult.get(0).getSizeColumns());

        Integer nroColumns = listConsult.get(0).getSizeColumns() - 3;
        for (int x = 0; x < nroColumns; x++) {
            TableColumn<VotosXAgrup, String> columNew = listColumns.get(x);
            columNew = new TableColumn();
            columNew.setText(String.valueOf(x + 1));
            columNew.setCellValueFactory(new PropertyValueFactory(String.valueOf("voto" + (x + 1))));
            columNew.setPrefWidth(67);
            tablaCongresista.getColumns().add(columNew);
        }
        tablevaluesCongresales = FXCollections.observableArrayList(listConsult);
        tablaCongresista.setItems(tablevaluesCongresales);
    }

    private void cargarTablaParlamentoAndino(List<VotosXAgrup> listConsult) {
        if (tablevaluesParlamento != null) {
            tablevaluesParlamento.clear();
        }
        ParlamenNro.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getNroAgrupol()));
        ParlamenCandidatos.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getDescAgrupol()));
        ParlamenCandidatos.setStyle("-fx-alignment: CENTER-LEFT;");
        ParlamenVotos.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getNrovotos()));
        Parlamen1.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto1()));
        Parlamen2.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto2()));
        Parlamen3.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto3()));
        Parlamen4.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto4()));
        Parlamen5.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto5()));
        Parlamen6.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto6()));
        Parlamen7.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto7()));
        Parlamen8.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto8()));
        Parlamen9.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto9()));
        Parlamen10.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto10()));
        Parlamen11.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto11()));
        Parlamen12.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto12()));
        Parlamen13.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto13()));
        Parlamen14.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto14()));
        Parlamen15.setCellValueFactory((CellDataFeatures<VotosXAgrup, String> p) -> new SimpleStringProperty(p.getValue().getVoto15()));
        tablevaluesParlamento = FXCollections.observableArrayList(listConsult);
        tablaParAndino.setItems(tablevaluesParlamento);
    }

    private void cargarDetalleMesa(String tipoEleccion, String codigo, List<VotosXAgrup> listVotos, String nivel, String codigoNodo) {
        /*Obtiene detalle de Acta segun el tipo de eleccion*/
        switch (tipoEleccion) {
            case "10":/*Cargando el detalle para el tipo de eleccion Presidencial*/

                List<DetalleUbigeo> listDetalleMesaPre = new ArrayList<>();
                if (nivel.equals("6") || nivel.equals("7")) {
                    listDetalleMesaPre = rcMonitoreodeActaService.ObtenerDetalleMesa(codigo, tipoEleccion);
                } else if (nivel.equals("8")) {
                    String nrActa = codigoNodo.substring(1, 7);
                    String nroDigita = codigoNodo.substring(7, 9);
                    String nroLote = codigoNodo.substring(11, 17);
                    if (nroDigita.equals("01")) {
                        listDetalleMesaPre = rcMonitoreodeActaService.ObtenerDetalleMesaDigita(nrActa, tipoEleccion, nroLote, ConstantReportes.STOREP_MONITOREOACTA_DETALLEDIG1);
                    } else if (nroDigita.equals("02")) {
                        listDetalleMesaPre = rcMonitoreodeActaService.ObtenerDetalleMesaDigita(nrActa, tipoEleccion, nroLote, ConstantReportes.STOREP_MONITOREOACTA_DETALLEDIG2);
                    }
                } else {
                    return;
                }
                if (listDetalleMesaPre.size() > 0) {
                    DetalleUbigeo beanDetalleUbig = listDetalleMesaPre.get(0);
                    /*Primera pestaÃ±a*/
                    txtLote.setText(beanDetalleUbig.getNumLote());
                    txtnroActa.setText(beanDetalleUbig.getNumMesa());
                    txtEstadoMesa.setText(beanDetalleUbig.getEstadoMesa());
                    txtUsuaModif.setText(beanDetalleUbig.getUsuarioLog());
                    txtFechaModif.setText(beanDetalleUbig.getFechaRegistro());
                    txtPresidenteActa.setText(beanDetalleUbig.getEstadoCompu());
                    txtPresidenteProc.setText(beanDetalleUbig.getEstadoActa());
                    /*Segunda pestaÃ±a*/
                    txtObservacionPre.setText(beanDetalleUbig.getObservacion().trim().equals("")?"Sin Observacion":beanDetalleUbig.getObservacion().trim());
                    System.out.println("Observacion Presidencial :" + beanDetalleUbig.getObservacion());
                    /*Tercera pestaÃ±a*/
                    txtSufragioPre.setText(beanDetalleUbig.getCvas());
                    txtElecHabilesPre.setText(beanDetalleUbig.getElecHabiles());
                    if (!beanDetalleUbig.getElecAusentes().equals("")) {
                        if (txtObservacionPre.getText().trim().equals("Sin Observacion")) {
                            txtAusentismoPre.setText(beanDetalleUbig.getElecAusentes().equals("0") ? "" : beanDetalleUbig.getElecAusentes());
                        } else {
                            txtAusentismoPre.setText("");
                        }
                    } else {
                        txtAusentismoPre.setText("");
                    }
                    txtSumVotosPre.setText(calculaSumaVotosTotal(listVotos).toString());
                }

                break;
            case "11":/*Cargando el detalle para el tipo de eleccion Congresal*/

                List<DetalleUbigeo> listDetalleMesaCong = rcMonitoreodeActaService.ObtenerDetalleMesa(codigo, tipoEleccion);
                if (listDetalleMesaCong.size() > 0) {
                    DetalleUbigeo beanDetalleUbig = listDetalleMesaCong.get(0);
                    /*Primera pestaÃ±a*/
                    txtLote.setText(beanDetalleUbig.getNumLote());
                    txtnroActa.setText(beanDetalleUbig.getNumMesa());
                    txtEstadoMesa.setText(beanDetalleUbig.getEstadoMesa());
                    txtUsuaModif.setText(beanDetalleUbig.getUsuarioLog());
                    txtFechaModif.setText(beanDetalleUbig.getFechaRegistro());
                    txtCongresistaActa.setText(beanDetalleUbig.getEstadoCompu());
                    txtCongresistaProc.setText(beanDetalleUbig.getEstadoActa());
                    /*Segunda pestaÃ±a*/
                    txtObservacionCongre.setText(beanDetalleUbig.getObservacion().trim().equals("")?"Sin Observacion":beanDetalleUbig.getObservacion().trim());
                    System.out.println("Observacion Congresal :" + beanDetalleUbig.getObservacion());
                    /*Tercera pestaÃ±a*/
                    txtSufragioCongre.setText(beanDetalleUbig.getCvas());
                    txtElecHabilesCongre.setText(beanDetalleUbig.getElecHabiles());
                    if (!beanDetalleUbig.getElecAusentes().equals("")) {
                        if (txtObservacionCongre.getText().trim().equals("Sin Observacion")) {
                            txtAusentismoCongre.setText(beanDetalleUbig.getElecAusentes().equals("0") ? "" : beanDetalleUbig.getElecAusentes());
                        } else {
                            txtAusentismoCongre.setText("");
                        }
                    } else {
                        txtAusentismoCongre.setText("");
                    }
                    txtSumVotosCongre.setText(calculaSumaVotosTotal(listVotos).toString());
                }

                break;
            case "12":/*Cargando el detalle para el tipo de eleccion Parlamento Andino*/

                List<DetalleUbigeo> listDetalleMesaPar = rcMonitoreodeActaService.ObtenerDetalleMesa(codigo, tipoEleccion);
                if (listDetalleMesaPar.size() > 0) {
                    DetalleUbigeo beanDetalleUbig = listDetalleMesaPar.get(0);
                    /*Primera pestaÃ±a*/
                    txtLote.setText(beanDetalleUbig.getNumLote());
                    txtnroActa.setText(beanDetalleUbig.getNumMesa());
                    txtEstadoMesa.setText(beanDetalleUbig.getEstadoMesa());
                    txtUsuaModif.setText(beanDetalleUbig.getUsuarioLog());
                    txtFechaModif.setText(beanDetalleUbig.getFechaRegistro());
                    txtParlamentoActa.setText(beanDetalleUbig.getEstadoCompu());
                    txtParlamentoProc.setText(beanDetalleUbig.getEstadoActa());
                    /*Segunda pestaÃ±a*/
                    txtObservacionPar.setText(beanDetalleUbig.getObservacion().trim().equals("")?"Sin Observacion":beanDetalleUbig.getObservacion().trim());
                    System.out.println("Observacion Parlamento :" + beanDetalleUbig.getObservacion());
                    /*Tercera pestaÃ±a*/
                    txtSufragioPar.setText(beanDetalleUbig.getCvas());
                    txtElecHabilesPar.setText(beanDetalleUbig.getElecHabiles());
                    if (!beanDetalleUbig.getElecAusentes().equals("")) {
                        if (txtObservacionPar.getText().trim().equals("Sin Observacion")) {
                            txtAusentismoPar.setText(beanDetalleUbig.getElecAusentes().equals("0") ? "" : beanDetalleUbig.getElecAusentes());
                        } else {
                            txtAusentismoPar.setText(""); //kleon - 2015-11-20 
                        }
                    } else {
                        txtAusentismoPar.setText("");
                    }
                    txtSumVotosPar.setText(calculaSumaVotosTotal(listVotos).toString());
                }

                break;
        }
        /*Validacion de las observaciones para habilitar los botones*/
        if (txtObservacionPar.getText().contains("1,") || txtObservacionPre.getText().contains("1,") || txtObservacionCongre.getText().contains("1,")) {
            btnActa.setDisable(false);
            btnActa.setStyle("-fx-background-color:#AE8006;");
        } else {
            btnActa.setDisable(true);
            btnActa.setStyle("-fx-background-color:#B8B8B8;");
        }

        if ((txtObservacionPar.getText().contains("8") || txtObservacionPre.getText().contains("8") || txtObservacionCongre.getText().contains("8"))
                || (txtObservacionPar.getText().contains("14") || txtObservacionPre.getText().contains("14") || txtObservacionCongre.getText().contains("14"))) {
            btnDetalle.setDisable(false);
            btnDetalle.setStyle("-fx-background-color:#AE8006;");
        } else {
            btnDetalle.setDisable(true);
            btnDetalle.setStyle("-fx-background-color:#B8B8B8;");
        }

    }

    private void limpiarDetalle() {
        /*limpiando las cajas de texto*/
        txtLote.setText("");
        txtnroActa.setText("");
        txtEstadoMesa.setText("");
        txtUsuaModif.setText("");
        txtFechaModif.setText("");
        txtObservacionPar.setText("");
        txtSufragioPar.setText("");
        txtElecHabilesPar.setText("");
        txtAusentismoPar.setText("");
        txtAusentismoPre.setText("");
        txtObservacionCongre.setText("");
        txtSufragioCongre.setText("");
        txtElecHabilesCongre.setText("");
        txtAusentismoCongre.setText("");
        txtObservacionPre.setText("");
        txtSufragioPre.setText("");
        txtElecHabilesPre.setText("");
        txtSumVotosCongre.setText("");
        txtSumVotosPar.setText("");
        txtSumVotosPre.setText("");
        txtPresidenteActa.setText("");
        txtPresidenteProc.setText("");
        txtCongresistaActa.setText("");
        txtParlamentoActa.setText("");
        txtCongresistaProc.setText("");
        txtParlamentoProc.setText("");

    }

    private void limpiarTablas() {
        if (tablevaluesPresidente != null) {
            tablevaluesPresidente.clear();
        }
        if (tablevaluesParlamento != null) {
            tablevaluesParlamento.clear();
        }
    }

    private void inicializarDatos() {
        txtLote.setDisable(true);
        txtnroActa.setDisable(true);
        txtEstadoMesa.setDisable(true);
        txtUsuaModif.setDisable(true);
        txtFechaModif.setDisable(true);
        txtObservacionPar.setDisable(true);
        txtSufragioPar.setDisable(true);
        txtElecHabilesPar.setDisable(true);
        txtAusentismoPar.setDisable(true);
        txtAusentismoPre.setDisable(true);
        txtObservacionCongre.setDisable(true);
        txtSufragioCongre.setDisable(true);
        txtElecHabilesCongre.setDisable(true);
        txtAusentismoCongre.setDisable(true);
        txtObservacionPre.setDisable(true);
        txtSufragioPre.setDisable(true);
        txtElecHabilesPre.setDisable(true);
        txtSumVotosCongre.setDisable(true);
        txtSumVotosPar.setDisable(true);
        txtSumVotosPre.setDisable(true);
        txtPresidenteActa.setDisable(true);
        txtPresidenteProc.setDisable(true);
        txtCongresistaActa.setDisable(true);
        txtParlamentoActa.setDisable(true);
        txtCongresistaProc.setDisable(true);
        txtParlamentoProc.setDisable(true);
        txtminutes.setVisible(false);
        lblTimeminuts.setVisible(false);
        btnActa.setDisable(true);
        btnActa.setStyle("-fx-background-color:#B8B8B8;");
        btnDetalle.setDisable(true);
        btnDetalle.setStyle("-fx-background-color:#B8B8B8;");
        finishedTask = true;
        txtminutes.setText("5");
    }

    private Integer calculaSumaVotosTotal(List<VotosXAgrup> listVotos) {
        Integer sumaTotalVotos = 0;
        for (VotosXAgrup iterator : listVotos) {
            sumaTotalVotos = sumaTotalVotos + (iterator.getNrovotos().equals("") ? 0 : Integer.parseInt(iterator.getNrovotos()));
        }
        System.out.println("Suma Total :" + sumaTotalVotos);
        return sumaTotalVotos;
    }

    private void ejecutarTime(Integer period) {
        System.out.println("periodo :" + period);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    System.out.println("Current time [BEFORE]:" + GregorianCalendar.getInstance().getTime());
                    Platform.runLater(() -> btnRefrescar());
                } catch (Exception e) {
                    Logger.getLogger(MonitoreoActasController.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }, period, period);
    }

    private void filtroKeyEvent() {

        txtminutes.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("[0-9]{0,2}")) {
                    if (newValue.equals("")) {
                        return;
                    }
                    int value = Integer.parseInt(newValue);
                    System.out.println("Value now :" + value);
                    if (value > 0) {
                        System.out.println("Time Now :" + GregorianCalendar.getInstance().getTime());
                        ejecutarTarea(newValue);
                    } else {
                        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                            stageMessageBox.close();
                            txtminutes.setText("");
                            stage.requestFocus();
                            txtminutes.requestFocus();
                        };
                        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, ConstantCommon.titleValidacion, "Debe ingresar un valor mayor a cero", null, null, null, eventOK);
                    }
                } else {
                    txtminutes.setText(oldValue);
                    System.out.println("oldValue :" + oldValue);
                }
            }
        });
    }

    @FXML
    private void txtMesa_OnKeyPressed(KeyEvent E) {
        try {
            if (E.getCode().equals(KeyCode.ENTER)) {
                posicionaMesa(NroMesa.getText());
            }
        } catch (Exception e) {
            AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.ERROR,
                    Messages.addButtons.ONLYOK, ConstantReportes.nameIngresoMonitoreoActas,
                    ConstantCommon.titleError, ConstantReportes.MSJE_ERROR_GENERICO_MONITOREOACTA.replaceAll("<detError>", e.getMessage()));
        }
    }

    public String concatNroMesa(String nmesa) {
        nroActa = nroActa + nmesa;
        System.out.println("Texto nro de Acta :" + nroActa);
        return nroActa;
    }

    public void ejecutarTarea(String minutes) {

        try {
            Integer intMinuts = Integer.parseInt(minutes) * 60000;

            if (verifyTask) {
                if (timer != null) {
                    System.out.println("Cancelando el timer[1]");
                    timer.cancel();
                    timer.purge();
                }
            }

            while (!verifyTask) {
                if (finishedTask) {
                    System.out.println("Cancelando el timer[2]");
                    verifyTask = true;
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MonitoreoActasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ejecutarTime(Integer.parseInt(minutes) * 60000);
    }

    private void posicionaMesa(String nroMesa) {
        /*Metodo de posicionamiento de Mesa*/
        System.out.println("[posicionaMesa] Nro de mesa a consultas :" + NroMesa.getText());
        listMesa = rcMonitoreodeActaService.getMesa("", "", nroMesa);

        if (listMesa.size() > 0) {
            /*Consultado la mesa por el numero */
            String valorUbigeo = listMesa.get(0).getCodiUbigeo();
            String valorLocal = listMesa.get(0).getCodiLocal();

            buscarNodo(valorUbigeo, valorLocal, nroMesa);
            comunService.registrarAuditoria(mainStage, "Se consultaron los datos de la mesa " + nroMesa + " en el monitoreo de Actas - Reportes y Consultas de la Suite Electoral", ConstantCommon.COD_MOD_REPCON);
        } else {
            System.out.println("La mesa ingresada no existe");
            mostrarMensaje(ConstantReportes.MSJ_MESANOEXISTE);
        }

    }

    private void buscarNodo(String ubigeo, String local, String nroMesa) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        TreeItem<Object> rootAux = treeview.getRoot();

        System.out.println("Time Before :" + sdf.format(new Date()));

        one:
        for (TreeItem iterate : rootAux.getChildren()) {
            System.out.println("Nodo Id :" + iterate.getGraphic().getId());
            String[] arrayId = iterate.getGraphic().getId().split("-");
            String codigo = arrayId[1];
            if (codigo.equals(ubigeo.substring(0, 2))) {
                iterate.setExpanded(true);
                two:
                for (Object iterate1 : iterate.getChildren()) {
                    TreeItem<Object> rootAux1 = (TreeItem<Object>) iterate1;
                    System.out.println("Nodo Id1 :" + rootAux1.getGraphic().getId());
                    String[] arrayId1 = rootAux1.getGraphic().getId().split("-");
                    String codigo1 = arrayId1[1];
                    if (codigo1.equals(ubigeo.substring(0, 4))) {
                        rootAux1.setExpanded(true);
                        three:
                        for (Object iterate2 : rootAux1.getChildren()) {
                            TreeItem<Object> rootAux2 = (TreeItem<Object>) iterate2;
                            System.out.println("Nodo Id2 :" + rootAux2.getGraphic().getId());
                            String[] arrayId2 = rootAux2.getGraphic().getId().split("-");
                            String codigo2 = arrayId2[1];
                            if (codigo2.equals(ubigeo.substring(0, 6))) {
                                rootAux2.setExpanded(true);
                                four:
                                for (Object iterate3 : rootAux2.getChildren()) {
                                    TreeItem<Object> rootAux3 = (TreeItem<Object>) iterate3;
                                    System.out.println("Nodo Id3 :" + rootAux3.getGraphic().getId());
                                    String[] arrayId3 = rootAux3.getGraphic().getId().split("-");
                                    String codigo3 = arrayId3[1];
                                    if (codigo3.equals(local)) {
                                        rootAux3.setExpanded(true);
                                        five:
                                        for (Object iterate4 : rootAux3.getChildren()) {
                                            TreeItem<Object> rootAux4 = (TreeItem<Object>) iterate4;
                                            String[] arrayId4 = rootAux4.getGraphic().getId().split("-");
                                            String codigo4 = arrayId4[1];
                                            if (codigo4.equals(nroMesa)) {
                                                rootAux4.setExpanded(true);
                                                treeview.getSelectionModel().select(rootAux4);
                                                System.out.println("Time After :" + sdf.format(new Date()));
                                                break five;
                                            }
                                        }
                                        break four;
                                    }
                                }
                                break three;
                            }
                        }
                        break two;
                    }
                }
                break one;
            }
        }
    }

    public void mostrarMensaje(String msg) {

        String title = ConstantCommon.titleValidacion;
        String message = msg;
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
            NroMesa.setText("");
            stage.requestFocus();
            NroMesa.requestFocus();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.WARNING, Messages.addButtons.ONLYOK, null, title, message, null, null, null, eventOK);

    }

    public void resetMinuts() {
        minutes = "";
    }

    public void resetNroActas() {
        nroActa = "";
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
