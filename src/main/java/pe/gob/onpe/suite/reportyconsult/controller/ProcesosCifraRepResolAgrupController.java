/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.context.ApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.util.Messages;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatCifraRepartidoraData;
import pe.gob.onpe.suite.domain.ResolucionCr;
import pe.gob.onpe.suite.reportyconsult.service.RcProcesoCifraRepartidoraService;

/**
 * FXML Controller class
 *
 * @author juchida
 */
public class ProcesosCifraRepResolAgrupController extends AppController implements Initializable {

    private Stage mainStage;
    private Scene scene;
    private Stage stage;

    private String distritoElectoral;
    private String tipoEleccion;
    private String titulo;
    private String estadoDistritoElectoral;
    private String escanosDisponibles;

    private ObservableList<ResolucionCr> tableViewConsultaData = FXCollections.observableArrayList();
    List<ResolucionCr> listResolucionCr = new ArrayList<>();

    @Autowired
    ApplicationContext context;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label labelTEUbigeo;
    @FXML
    private TextField txtNroResolucion;
    @FXML
    private TextField txtTipoResolucion;
    @FXML
    private TextField txtCurulesAsignados;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TableView<ResolucionCr> tableViewSeleccion;
    @FXML
    private TableView<ResolucionCr> tableViewRegistros;
    @FXML
    private ComboBox cboEstados;
    @FXML
    private ComboBox cboEstadoAgrupacion;
    @FXML
    private ComboBox cboAgrupFavorecida;

    private Stage stageMessageBox;

    @Autowired()
    RcProcesoCifraRepartidoraService rcProcesoCifraRepartidoraService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void init() {
        String tituloUbigeo = distritoElectoral + " Elección: " + tipoEleccion + " - Escaños Por Repartir: " + escanosDisponibles;
        labelTEUbigeo.setText(tituloUbigeo);
        txtTipoResolucion.setText(ConstantReportes.ESTADO_EMPATE + " " + "EMPATE");

        llenarLista();

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
        subBarraStage(stage, titulo, 2);

        cargarTablaSeleccion();
        cargarTablaRegistros();
        cargarCombos();
    }

    private void llenarLista() {
        List<ResolucionCr> resoluciones = rcProcesoCifraRepartidoraService.obtenerResolucionesCR(tipoEleccion, distritoElectoral);
        tableViewConsultaData.removeAll(tableViewConsultaData);
        tableViewSeleccion.setEditable(true);
        tableViewSeleccion.setItems(gerResolucionesCrData(listResolucionCr));

    }

    private ObservableList<ResolucionCr> gerResolucionesCrData(List<ResolucionCr> listResolucionCrData) {
        for (ResolucionCr beanResolucionCrData : listResolucionCrData) {
            tableViewConsultaData.add(beanResolucionCrData);
        }
        return tableViewConsultaData;
    }

    private void cargarCombos() {
        String descEmpate = "";
        if (estadoDistritoElectoral.equals(ConstantReportes.ESTADO_EMPATE)) {
            descEmpate = "EMPATE";
        }
        cboEstados.getItems().add(estadoDistritoElectoral + " " + descEmpate);
        cboEstados.getSelectionModel().selectFirst();

        cboEstadoAgrupacion.getItems().addAll(ConstantReportes.ESTADO_AGRUPOL_WIN_X_SORTEO + " " + "GANÓ POR SORTEO",
                ConstantReportes.ESTADO_AGRUPOL_PERDIO_SORTEO + " " + "PERDIÓ EL SORTEO");
        cboEstadoAgrupacion.getSelectionModel().selectFirst();

        List<ResolucionCr> agrupaciones = rcProcesoCifraRepartidoraService.obtenerAgrupaciones(tipoEleccion, distritoElectoral);
        for (ResolucionCr agrupacion : agrupaciones) {
            cboAgrupFavorecida.getItems().add(agrupacion.getCodiAgrupol() + " " + agrupacion.getDescAgrupPol());
        }
        cboAgrupFavorecida.getSelectionModel().selectFirst();

    }

    private void cargarTablaSeleccion() {

        TableColumn<ResolucionCr, String> tableColumNumeroResolucion = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumAgrupacionFavorecida = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumTipoResolucion = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumDescResolucion = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumCurulesAsignados = new TableColumn();

        tableColumNumeroResolucion.setText(ConstantReportes.nameColumnProcesosNumeroResolucion);
        tableColumNumeroResolucion.setCellValueFactory(new PropertyValueFactory("numeResol"));
        tableColumNumeroResolucion.setMinWidth(200);

        tableColumAgrupacionFavorecida.setText(ConstantReportes.nameColumnProcesosAgrupacionFavorecida);
        tableColumAgrupacionFavorecida.setCellValueFactory(new PropertyValueFactory("agrupacionFavorecida"));
        tableColumAgrupacionFavorecida.setMinWidth(200);

        tableColumTipoResolucion.setText(ConstantReportes.nameColumnProcesosTipoResolucion);
        tableColumTipoResolucion.setCellValueFactory(new PropertyValueFactory("tipoResolucion"));
        tableColumTipoResolucion.setMinWidth(200);

        tableColumDescResolucion.setText(ConstantReportes.nameColumnProcesosDescripcionResol);
        tableColumDescResolucion.setCellValueFactory(new PropertyValueFactory("descResol"));
        tableColumDescResolucion.setMinWidth(200);

        tableColumCurulesAsignados.setText(ConstantReportes.nameColumnProcesosCurulesAsig);
        tableColumCurulesAsignados.setCellValueFactory(new PropertyValueFactory("escanosAsig"));
        tableColumCurulesAsignados.setMinWidth(200);

        tableViewSeleccion.getColumns().addAll(tableColumNumeroResolucion, tableColumAgrupacionFavorecida,
                tableColumTipoResolucion, tableColumDescResolucion, tableColumCurulesAsignados);
    }

    private void cargarTablaRegistros() {

        TableColumn<ResolucionCr, String> tableColumCodigo = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumAgrupacion = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumCodigo2 = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumEstadoAgrupacion = new TableColumn();
        TableColumn<ResolucionCr, String> tableColumCurulesAsignadas = new TableColumn();

        tableColumCodigo.setText(ConstantReportes.nameColumnProcesosCodigo);
        tableColumCodigo.setCellValueFactory(new PropertyValueFactory("numeResol"));
        tableColumCodigo.setMinWidth(200);

        tableColumAgrupacion.setText(ConstantReportes.nameColumnProcesosAgrupacion);
        tableColumAgrupacion.setCellValueFactory(new PropertyValueFactory("agrupacionFavorecida"));
        tableColumAgrupacion.setMinWidth(200);

        tableColumCodigo2.setText(ConstantReportes.nameColumnProcesosCodigo2);
        tableColumCodigo2.setCellValueFactory(new PropertyValueFactory("tipoResolucion"));
        tableColumCodigo2.setMinWidth(200);

        tableColumEstadoAgrupacion.setText(ConstantReportes.nameColumnProcesosEstadoAgrupacion);
        tableColumEstadoAgrupacion.setCellValueFactory(new PropertyValueFactory("descResol"));
        tableColumEstadoAgrupacion.setMinWidth(200);

        tableColumCurulesAsignadas.setText(ConstantReportes.nameColumnProcesosCurulesAsignadas);
        tableColumCurulesAsignadas.setCellValueFactory(new PropertyValueFactory("escanosAsig"));
        tableColumCurulesAsignadas.setMinWidth(200);

        tableViewSeleccion.getColumns().addAll(tableColumCodigo, tableColumAgrupacion, tableColumCodigo2,
                tableColumEstadoAgrupacion, tableColumCurulesAsignadas);
    }

    private boolean validar(boolean lValida) {
        String msg = null;
        boolean valido = false;
        String estado = (String) cboEstados.getSelectionModel().getSelectedItem();
        if (txtNroResolucion.getText().trim().equals("")) {
            msg = "Debe definir 'Nro. de Resolución'";
        }
        if (cboAgrupFavorecida.getSelectionModel().getSelectedItem().toString().equals("") && lValida) {
            msg = "Debe definir 'Agrupación Política'";
        }
        if (cboEstados.getSelectionModel().getSelectedItem().toString().equals("") && lValida) {
            msg = "Debe definir 'Tipo de Resolución'";
        }
        if (cboEstadoAgrupacion.getSelectionModel().getSelectedItem().toString().equals("") && lValida) {
            msg = "Debe definir 'Estado de Agrupación'";
        }
        if (!estadoDistritoElectoral.equals(estado.substring(0, 1)) && lValida) {
            msg = "El Tipo de resolución no corresponde al Tipo de Empate del Distrito Electoral.";
        }
        if (msg != null) {
            mostrarMensajeInformacion(msg, ConstantCommon.titleInformacion);
        } else {
            valido = true;
        }
        return valido;
    }

    private boolean validaCuposDisponibles() {
        boolean isCorrect = false;
        int totalCurulesAsignados = 0;
        for (ResolucionCr beanResolucionCr : listResolucionCr) {
            totalCurulesAsignados += beanResolucionCr.getEscanosAsig();
        }
        if (totalCurulesAsignados == Integer.parseInt(escanosDisponibles)) {
            isCorrect = true;
        } else if (totalCurulesAsignados < Integer.parseInt(escanosDisponibles)) {
            int cuposDisponibles = Integer.parseInt(escanosDisponibles) - totalCurulesAsignados;
            mostrarMensajeInformacion("No se han resuelto todos los Cupos Disponibles. Quedan Por repartir: " + cuposDisponibles, ConstantCommon.titleInformacion);
            isCorrect = false;
        } else if (totalCurulesAsignados > Integer.parseInt(escanosDisponibles)) {
            mostrarMensajeInformacion("No se puede reasignar mas curules de los disponibles. Curules Disponibles: " + totalCurulesAsignados, ConstantCommon.titleInformacion);
            isCorrect = false;
        }
        return isCorrect;
    }

    @FXML
    private void btnGrabarOnAction() {
        boolean existeReg = false;
        boolean actualizo = false;
        if (validar(true)) {
            if (validaCuposDisponibles()) {
                return;
            }
            if (rcProcesoCifraRepartidoraService.existeResolucion(tipoEleccion, distritoElectoral, "1", txtNroResolucion.getText())) {
                mostrarMensajeInformacion("Ya existe la resolución con Nro:" + txtNroResolucion.getText(), ConstantCommon.titleInformacion);
            }
            for (ResolucionCr beanResolucionCr : listResolucionCr) {
                existeReg = true;
                String nroResolucion = beanResolucionCr.getNumeResol();
                String tipoEmpate = ((String) cboEstados.getSelectionModel().getSelectedItem()).substring(0, 1);
                String codAgruPol = beanResolucionCr.getCodiAgrupol();
                String estadoAgruPol = beanResolucionCr.getEstadoResol();
                String descResolucion = txtDescripcion.getText();
                int curulesAsignados = beanResolucionCr.getEscanosAsig();
                String codigoCandidato = " ";
                int ordenObtenido = 0;
                String cargoObtenido = " ";
                String codUsuario = DatosUsuario.getMstrCodigoUsuario();
                String nombrePCLog = DatosUsuario.getMstrNombreProceso();
                actualizo = rcProcesoCifraRepartidoraService.actualizaResolucion(tipoEleccion, distritoElectoral, "1",
                        nroResolucion, tipoEmpate, codAgruPol, estadoAgruPol,
                        descResolucion, curulesAsignados, codigoCandidato, ordenObtenido,
                        cargoObtenido, codUsuario, nombrePCLog);
            }

            if (existeReg) {
                mostrarMensajeInformacion("No existen datos seleccionados para Actualizar", ConstantCommon.titleInformacion);
            } else if (actualizo) {
                mostrarMensajeInformacion("Se agregó/actualizó con éxito la resolución. '" + txtNroResolucion.getText() + "'", ConstantCommon.titleInformacion);
            } else {
                mostrarMensajeInformacion("No se pudo agregar la resolución '" + txtNroResolucion + "'", ConstantCommon.titleInformacion);
            }
        }

    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
    }

    @FXML
    private void btnEliminarOnAction() {
        if (validar(false)) {
            String title = ConstantCommon.titleConfirmacion;
            String mensaje = "Está seguro de eliminar la resolución " + txtNroResolucion.getText();
            EventHandler eventYes = (EventHandler<ActionEvent>) (ActionEvent event) -> {
                boolean isEliminado = rcProcesoCifraRepartidoraService.eliminaResolucion(tipoEleccion, distritoElectoral, tipoEleccion);
                if(!isEliminado){
                    mostrarMensajeInformacion("Hubo un error eliminando la resolución!", ConstantCommon.titleInformacion);
                }else{
                    limpiar();
                    llenarLista();
                }
                stageMessageBox.close();
            };

            //Muestra el MessageBox
            stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.QUESTION, Messages.addButtons.YESNO, null, title, mensaje, null, null, eventYes, null);
        }
    }
    
    private void limpiar(){
        txtNroResolucion.setText(" ");
        txtDescripcion.setText(" ");
        txtCurulesAsignados.setText(" ");
        cboAgrupFavorecida.getSelectionModel().selectFirst();
        cboEstadoAgrupacion.getSelectionModel().selectFirst();
        cboEstados.getSelectionModel().selectFirst();
    }

    private void mostrarMensajeInformacion(String title, String mensaje) {
        EventHandler eventOK = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            stageMessageBox.close();
        };
        stageMessageBox = AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.INFORMATION, Messages.addButtons.ONLYOK, null, title, mensaje, null, null, null, eventOK);
    }

    @FXML
    private void btnAgregarOnAction() {
        if (existeAgrupacion()) {
            mostrarMensajeInformacion("La Agrupación ya fue agregada!", ConstantCommon.titleInformacion);
        } else {
            String estadoAgrupacion = (String) cboEstadoAgrupacion.getSelectionModel().getSelectedItem();

            switch (estadoAgrupacion.substring(0, 1)) {
                case "G":
                    if (txtCurulesAsignados.getLength() == 0) {
                        mostrarMensajeInformacion(ConstantCommon.titleInformacion, "Debe ingresar las Curules Asignadas");
                    }
                    if (txtCurulesAsignados.getText().equals("0")) {
                        mostrarMensajeInformacion(ConstantCommon.titleInformacion, "La Curul Asignada debe ser mayor a cero");
                    }
                    break;
                case "X":
                    if (txtCurulesAsignados.getLength() > 0 && !(txtCurulesAsignados.getText().equals("0"))) {
                        mostrarMensajeInformacion(ConstantCommon.titleInformacion, "No se puede Asignar Curules si Perdió el Sorteo.");
                    }
                    break;
            }
        }
        ResolucionCr resolucionCr = new ResolucionCr();
        String agrupFavorecida = (String) cboAgrupFavorecida.getSelectionModel().getSelectedItem();
        String estadoAgrupacion = (String) cboEstadoAgrupacion.getSelectionModel().getSelectedItem();
        resolucionCr.setNumeResol(agrupFavorecida.substring(0, 7));
        resolucionCr.setAgrupacionFavorecida(agrupFavorecida.substring(8));
        resolucionCr.setTipoResolucion(estadoAgrupacion.substring(0, 1));
        resolucionCr.setDescResol(estadoAgrupacion.substring(2));
        String curulesAsig = (txtCurulesAsignados.getText() == null || txtCurulesAsignados.getLength() == 0) ? "0" : txtCurulesAsignados.getText();
        resolucionCr.setEscanosAsig(Integer.parseInt(curulesAsig));
        listResolucionCr.add(resolucionCr);

        llenarLista();
    }

    private boolean existeAgrupacion() {
        TableColumn<ResolucionCr, String> tableColumNumeroResolucion = new TableColumn();
        int contadorSeleccion = tableViewSeleccion.getItems().size();
        for (ResolucionCr resolucionCr : tableViewSeleccion.getItems()) {
            return cboAgrupFavorecida.getSelectionModel().getSelectedItem() == tableColumNumeroResolucion.getCellData(resolucionCr);
        }
        return false;
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

    public void setDistritoElectoral(String distritoElectoral) {
        this.distritoElectoral = distritoElectoral;
    }

    public void setTipoEleccion(String tipoEleccion) {
        this.tipoEleccion = tipoEleccion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setEstadoDistritoElectoral(String estadoDistritoElectoral) {
        this.estadoDistritoElectoral = estadoDistritoElectoral;
    }

    public void setEscanosDisponibles(String escanosDisponibles) {
        this.escanosDisponibles = escanosDisponibles;
    }

}
