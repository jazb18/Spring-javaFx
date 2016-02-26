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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.CincoCandidatosMasVotados;
import pe.gob.onpe.suite.domain.ReporteCincoCandidatosMasVotados;
import pe.gob.onpe.suite.reportyconsult.service.RcCincoCandidatosMasVotadosService;
import pe.gob.onpe.suite.util.report.JasperReportUtil;
import pe.gob.onpe.suite.common.util.ReportesConsultaUtil;

/**
 *
 * @author jzafra
 */
public class CincoCandidatosMasVotadosController extends AppController implements Initializable {

    private Stage mainStage, stage;
    private Scene scene;

    @FXML
    private AnchorPane anchorPane;

    private String name = ConstantCommon.titleReporCons;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Autowired
    RcCincoCandidatosMasVotadosService rcCincoCandidatosMasVotadosService;

    @Autowired
    IComunService comunService;

    public void init() throws Exception {

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
            stage.close();

            List<ReporteCincoCandidatosMasVotados> listaReporte = new ArrayList<>();
            List<CincoCandidatosMasVotados> data = rcCincoCandidatosMasVotadosService.getcincocandidatosmasvotados();
            if (data != null) {
                if (data.size() < 1) {
                    validador(1);
                    return;
                }
            }
            JasperReportUtil jp = new JasperReportUtil();
            InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantReportes.pathReportConsult + ConstantReportes.nameReporteCincoCandidatosMasVotados + ".jrxml");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantCommon.pathImageCommon + "onpe.jpg"); //logo onpe
            Map<String, Object> parametros = new HashMap<>();
            int i = 0;

            parametros.put("tituloGeneral", DeclaracionComun.gstrNombreEleccion);
            parametros.put("tituloSecundario", ConstantReportes.nameTituloCincoCandidatosMasVotados);
            parametros.put("servidor", DeclaracionComun.gstrNombreServidorBD);
            parametros.put("usuario", DatosUsuario.getMstrCodigoUsuario());
            parametros.put("estacion", DatosUsuario.getMstrEstacionUsuario());
            parametros.put("reporte", ConstantReportes.nameReporteCincoCandidatosMasVotados);
            parametros.put("version", DeclaracionComun.gstrVersionSuite);
            parametros.put("sinvaloroficial", ReportesConsultaUtil.validarInfOficial());
            parametros.put("odpe", ConstantReportes.SELECCION_COMBO_TODOS);
            parametros.put("distrito", ConstantReportes.SELECCION_COMBO_TODOS);
            parametros.put("url_imagen", imagen);
            parametros.put("nombreCC", ConstantReportes.SELECCION_COMBO_TODOS);

            for (CincoCandidatosMasVotados p : data) {
                i += 1;
                ReporteCincoCandidatosMasVotados r = new ReporteCincoCandidatosMasVotados();
                r.setCodigo(String.valueOf(i));
                r.setCandidato(p.getCandidato());
                r.setDistritoE(p.getC_desc_dist_elec());
                r.setOrganizacionP(p.getC_desc_agrupol());
                r.setVotoP(p.getN_votos_());
                listaReporte.add(r);
            }

            StackPane sp = jp.reportShow(parametros, listaReporte, file, null);
            AppController appController = new AppController();
            String title = ConstantReportes.nameCincoCandidatosMasVotados;
            appController.subBarraStageReport(mainStage, sp, title, 2);
            cmdBotones_Click(0);

        } catch (Exception e) {
            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }

    }

    private void validador(int nro) {
        switch (nro) {
            case 1:
                mostrarMensajeRPT(ConstantCommon.titleInformacion, "No existe informacion para la consulta realizada");
                break;
        }
    }

    public void cmdBotones_Click(int valor) {
        switch (valor) {
            case 0:
                comunService.registrarAuditoria(mainStage, "Se imprimio el reporte de CincoCandidatos mas votados - Reportes y Consultas de la Suite Electoral", COD_MOD_REPCON);
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
