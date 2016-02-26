/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CatErrorMaterial;
import pe.gob.onpe.suite.reportyconsult.service.RcMonitoreodeActaService;

/**
 * FXML Controller class
 *
 * @author jzafra
 */
public class ActaObservacionesController extends AppController implements Initializable {

    private Stage mainStage;
    private Scene scene;
    private Stage stage;

    private String errorMaterial;
    private String nroActa;
    private String tipoProceso;
    private String digita;
    private String nroLote;
    private String observPre;
    private String observCong;
    private String observPar;
    private String estadoProcPre;
    private String estadoProcCong;
    private String estadoProcPar;

    @Autowired()
    RcMonitoreodeActaService rcMonitoreodeActaService;

    @FXML
    private TableView<CatErrorMaterial> tableObservacionesPre, tableObservacionesCon, tableObservacionesPar;
    @FXML
    private TableColumn<CatErrorMaterial, String> ColumnCodigoPre, ColumnDescripcionPre, ColumnCodigoCong, ColumnDescripcionCong, ColumnCodigoPar, ColumnDescripcionPar;
    private ObservableList<CatErrorMaterial> tablevaluesPresidente;
    private ObservableList<CatErrorMaterial> tablevaluesCongresista;
    private ObservableList<CatErrorMaterial> tablevaluesParlamento;

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initWindows() {

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
        subBarraStage(stage, "Tipos de Error Material", 2);
        mostrarVariables();
        Procesarconsulta();

    }

    @FXML
    private void btnSalirOnAction() {
        stage.close();
    }

    private void Procesarconsulta() {
        switch (errorMaterial) {
            case "A":
                if (observPre.contains("1")) {
                    if (!(estadoProcPre.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_PRESIDENCIAL, "1");
                    }
                }
                if (observCong.contains("1")) {
                    if (!(estadoProcCong.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_CONGRESAL, "1");
                    }
                }
                if (observPar.contains("1")) {
                    if (!(estadoProcPar.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO, "1");
                    }
                }
                break;
            case "D":

                if (observPre.contains("8") || observPre.contains("14")) {
                    if (!(estadoProcPre.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_PRESIDENCIAL, "1");
                    }
                }
                if (observCong.contains("8") || observCong.contains("14")) {
                    if (!(estadoProcCong.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_CONGRESAL, "1");
                    }
                }
                if (observPar.contains("8") || observPar.contains("14")) {
                    if (!(estadoProcPar.equals(""))) {
                        cargarTabla(ConstantReportes.TIPOELECCION_PARLAMENTOANDINO, "1");
                    }
                }

                break;
        }
    }

    private void cargarTabla(String tipoProc, String existe) {
        try {
            /*Cargando el listado de Observaciones*/

            List<CatErrorMaterial> listaErrorMat = rcMonitoreodeActaService.ObtenerErrorMaterial(nroActa, tipoProc, errorMaterial, Float.parseFloat(existe), nroLote, Float.parseFloat(digita));
            
            System.out.println("########################################");
            System.out.println("errorMaterial :" + errorMaterial);
            System.out.println("nroActa :" + nroActa);
            System.out.println("tipoProceso :" + tipoProc);
            System.out.println("digita :" + digita);
            System.out.println("existe :" + existe);
            System.out.println("nroLote :" + nroLote);
            System.out.println("########################################");

            if (listaErrorMat.size() > 0) {
                System.out.println("Longitud de la lista : " + listaErrorMat.size());
                switch (tipoProc) {
                    case "10":

                        ColumnCodigoPre.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getCodigo()));
                        ColumnDescripcionPre.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
                        tablevaluesPresidente = FXCollections.observableArrayList(listaErrorMat);
                        tableObservacionesPre.setItems(tablevaluesPresidente);

                        break;
                    case "11":

                        ColumnCodigoCong.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getCodigo()));
                        ColumnDescripcionCong.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
                        tablevaluesCongresista = FXCollections.observableArrayList(listaErrorMat);
                        tableObservacionesCon.setItems(tablevaluesCongresista);

                        break;
                    case "12":

                        ColumnCodigoPar.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getCodigo()));
                        ColumnDescripcionPar.setCellValueFactory((TableColumn.CellDataFeatures<CatErrorMaterial, String> p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
                        tablevaluesParlamento = FXCollections.observableArrayList(listaErrorMat);
                        tableObservacionesPar.setItems(tablevaluesParlamento);

                        break;
                }
            } else {
                System.out.println("La lista obtenida es vacia :" + listaErrorMat);
            }

        } catch (Exception ex) {
            Logger.getLogger(MonitoreoActasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarVariables() {

        System.out.println("errorMaterial :" + errorMaterial);
        System.out.println("nroActa :" + nroActa);
        System.out.println("tipoProceso :" + tipoProceso);
        System.out.println("digita :" + digita);
        System.out.println("nroLote :" + nroLote);
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

    public String getErrorMaterial() {
        return errorMaterial;
    }

    public void setErrorMaterial(String errorMaterial) {
        this.errorMaterial = errorMaterial;
    }

    public String getNroActa() {
        return nroActa;
    }

    public void setNroActa(String nroActa) {
        this.nroActa = nroActa;
    }

    public String getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public String getDigita() {
        return digita;
    }

    public void setDigita(String digita) {
        this.digita = digita;
    }

    public String getNroLote() {
        return nroLote;
    }

    public void setNroLote(String nroLote) {
        this.nroLote = nroLote;
    }

    public String getObservPre() {
        return observPre;
    }

    public void setObservPre(String observPre) {
        this.observPre = observPre;
    }

    public String getObservCong() {
        return observCong;
    }

    public void setObservCong(String observCong) {
        this.observCong = observCong;
    }

    public String getObservPar() {
        return observPar;
    }

    public void setObservPar(String observPar) {
        this.observPar = observPar;
    }

    public String getEstadoProcPre() {
        return estadoProcPre;
    }

    public void setEstadoProcPre(String estadoProcPre) {
        this.estadoProcPre = estadoProcPre;
    }

    public String getEstadoProcCong() {
        return estadoProcCong;
    }

    public void setEstadoProcCong(String estadoProcCong) {
        this.estadoProcCong = estadoProcCong;
    }

    public String getEstadoProcPar() {
        return estadoProcPar;
    }

    public void setEstadoProcPar(String estadoProcPar) {
        this.estadoProcPar = estadoProcPar;
    }

}
