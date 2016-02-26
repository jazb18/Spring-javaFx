/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import static pe.gob.onpe.suite.common.constant.ConstantCommon.*;
import pe.gob.onpe.suite.common.global.Acceso;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.global.DeclaracionComun;
import pe.gob.onpe.suite.common.service.IComunService;
import pe.gob.onpe.suite.common.service.IMenuUsuarioModuloPerfilService;
import pe.gob.onpe.suite.common.util.Messages;
import static pe.gob.onpe.suite.common.util.Messages.addButtons.*;
import static pe.gob.onpe.suite.common.util.Messages.typeMessage.*;
import pe.gob.onpe.suite.common.view.AppController;
import pe.gob.onpe.suite.domain.DetUsuarioModulo;
import pe.gob.onpe.suite.util.UtilControllerFactory;
import pe.gob.onpe.suite.util.menu.controller.MenuController;

/**
 *
 * @author jzafra
 */
@Controller
public class MainController extends AppController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label lblODPE, lblCentroComputo, lblUsuario, lblEstacion, lblHora, lblFecha;

    Timer timer = new Timer();

    private Stage mainStage;

    ApplicationContext context;

    @Autowired
    IMenuUsuarioModuloPerfilService menuUsuarioModuloPerfilService;

    @Autowired
    MonitoreoActasController monitoreoActasController;

    @Autowired
    IComunService comunService;

    private Stage stageMessageBox;

    private String name = ConstantCommon.titleReporCons;

    private Integer intentosSalir = 0;

    @FXML
    private Label lblfecha;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*Calendar calendario = GregorianCalendar.getInstance(Locale.ROOT);
         Date dia = calendario.getTime();

         SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
         lblfecha.setText(formatoFecha.format(dia));

         SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm a");
         lblHora.setText(formatoHora.format(dia));*/
    }

    public void initMain() {
        try {
            if (validaAcceso()) {
                //Setea parámetros
                InicializarSistema();
                Scene scene = new Scene(this.getView());
                //Carga la hoja de estilos común
                scene.getStylesheets().add(ConstantCommon.pathCssCommon + ConstantCommon.cssMain);
                mainStage.setScene(scene);
                mainStage.setFullScreenExitHint("");
                mainStage.initStyle(StageStyle.UNDECORATED);
                mainStage.setMaximized(true);

                mainStage.show();
                barraStagePrincipal(mainStage);
                anchorPane.requestFocus(); //temporalmente

                //Carga el menu - INI         
                //DetUsuarioModulo detUsuarioModulo = new DetUsuarioModulo();
                //detUsuarioModulo.setModulo(ConstantCommon.codModuloReportesConsultas);
                //detUsuarioModulo.setPerfil(DatosUsuario.getMstrPerfil());
                //detUsuarioModulo.setPerfil("001");
                //AnchorPane menu;
                DetUsuarioModulo detUsuarioModulo = new DetUsuarioModulo();
                detUsuarioModulo.setModulo(COD_MOD_REPCON);
                detUsuarioModulo.setPerfil(DatosUsuario.getMstrPerfil());
                AnchorPane menu;

                ApplicationContext contextMenu = new AnnotationConfigApplicationContext(UtilControllerFactory.class);
                MenuController menuController = contextMenu.getBean(MenuController.class);
                menu = menuController.load();

                menuController.setModulo(detUsuarioModulo.getModulo());
                List<Object> lista = menuUsuarioModuloPerfilService.getListaMenu(detUsuarioModulo);
                menuController.setListaOpcionModuloObject(lista);
                menuController.setMainStage(mainStage);
                menuController.setContext(context);
                menuController.init();
                AnchorPane ap = (AnchorPane) getView();
                ap.getChildren().add(menu);

            }
        } catch (Exception e) {

            comunService.determinarException(mainStage, e, name, COD_MOD_REPCON, EXE_MOD_REPCON);
        }
    }

    private boolean validaAcceso() {
        String title = ConstantCommon.titleError;
        String message = "";

        //Valida seteo del usuario
        if (DatosUsuario.getMstrODPE().equals("")
                || DatosUsuario.getMstrCentroComputo().equals("")
                || DatosUsuario.getMstrCodigoUsuario().equals("")
                || DatosUsuario.getMstrPerfil().equals("")
                || DatosUsuario.getMstrEstacionUsuario().equals("")
                || DatosUsuario.getMstrNombre().equals("")
                || DatosUsuario.getMstrNombreCentroComputo().equals("")
                || DatosUsuario.getMstrNombreODPE().equals("")
                || Acceso.getIntTipoProceso() == 0) {

            message = ConstantCommon.msgDatosInicialesNoCargados;

            //Muestra el MessageBox
            AppController.handleMessageBoxModal(mainStage, Messages.typeMessage.ERROR, Messages.addButtons.ONLYOK, name, title, message);

            return false;
        }
        return true;
    }

    @FXML
    private void btnSalirOnAction() {
        //Evento a ejecutar al hacer clic en YES 
        EventHandler eventYes = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            try {
                ResumenEstadoActasController rsmctr = new ResumenEstadoActasController();
                if (rsmctr.newThread != null) {
                    if (!rsmctr.newThread.isInterrupted()) {
                        rsmctr.newThread.interrupt();
                    }
                }
                if (monitoreoActasController.timer != null) {
                    System.out.println("Cancelando el timer de MonitoreoActas");
                    monitoreoActasController.timer.cancel();
                    monitoreoActasController.timer.purge();
                }

                stageMessageBox.close();
                intentosSalir++;
                timer.cancel();
                comunService.cerrarModuloActual(mainStage,
                        DatosUsuario.getMstrCodigoUsuario(),
                        COD_MOD_REPCON,
                        EXE_MOD_REPCON,
                        DatosUsuario.getMstrEstacionUsuario());
                comunService.closeConnectionADM();
            } catch (Exception e) {
                 comunService.escribirStackTrace(COD_MOD_REPCON, e);
                if (intentosSalir > 1) {
                    mainStage.close();
                } else {
                    AppController.handleMessageBoxModal(mainStage,
                            ERROR, ONLYOK, name, titleError, MSG_ERROR_SALIR_MODULO);
                }
            }
        };

        //Muestra el MessageBox
        stageMessageBox = AppController.handleMessageBoxModal(mainStage,
                QUESTION, YESNO, name, titleConfirmacion, MSG_SALIR_MODULO,
                null, null, eventYes, null);

    }
    
     public void onCloseRequest() {
        try {
            timer.cancel();
            comunService.cerrarModuloActual(mainStage,
                    DatosUsuario.getMstrCodigoUsuario(),
                    COD_MOD_REPCON,
                    EXE_MOD_REPCON,
                    DatosUsuario.getMstrEstacionUsuario());
        } catch (Exception e) {
            comunService.escribirStackTrace(COD_MOD_REPCON, e);
        }

    }

    private void InicializarSistema() {

        //El módulo de Inicio debería setear estas variables. 
        //Si no se accede desde Inicio (es decir se accede directamente al módulo, por desarrollo, pruebas. debug) se setean a continuación:
        if (DeclaracionComun.gstrNombreEleccion.equals("")) {
            DeclaracionComun.gstrNombreEleccion = comunService.entregar_valoresParametro("NOMBRE_PROCESO");
        }

        if (DeclaracionComun.gstrNombreServidorBD.equals("")) {
            DeclaracionComun.gstrNombreServidorBD = comunService.entregar_nombreServidor();
        }

        if (DeclaracionComun.gstrVersionSuite.equals("")) {
            DeclaracionComun.gstrVersionSuite = comunService.entregar_valoresParametro(ConstantCommon.parVersionSuite);
        }

        if (DeclaracionComun.gstrFechaEleccion.equals("")) {
            DeclaracionComun.gstrFechaEleccion = comunService.entregar_valoresParametro(ConstantCommon.parFechaEleccion);
        }

        if (DeclaracionComun.gstrDiasSinValorOficial.equals("")) {
            DeclaracionComun.gstrDiasSinValorOficial = comunService.entregar_valoresParametro(ConstantCommon.parDiasSinValorOficial);
        }

        if (DeclaracionComun.gstrAbreviaturaProceso.equals("")) {
            DeclaracionComun.gstrAbreviaturaProceso = comunService.entregar_valoresParametro(ConstantCommon.parAbreviaturaProceso);
        }

        //Llena el status bar
        lblODPE.setText(lblODPE.getText() + DatosUsuario.getMstrNombreODPE());
        lblCentroComputo.setText(lblCentroComputo.getText() + DatosUsuario.getMstrNombreCentroComputo());
        lblUsuario.setText(lblUsuario.getText() + DatosUsuario.getMstrCodigoUsuario());
        lblEstacion.setText(lblEstacion.getText() + DatosUsuario.getMstrEstacionUsuario());
        reloj();

        try {
            //Actualiza DET_USUARIO_MODULO
            comunService.actualizaModUsuarioIngSal(DatosUsuario.getMstrCodigoUsuario(),
                    COD_MOD_REPCON,
                    ConstantCommon.usuarioIngresoModuloS,
                    DatosUsuario.getMstrEstacionUsuario(),
                    EXE_MOD_REPCON);

        } catch (Exception e) {
            //Vuelve a intentar                
            try {
                comunService.actualizaModUsuarioIngSal(DatosUsuario.getMstrCodigoUsuario(),
                        COD_MOD_REPCON,
                        ConstantCommon.usuarioIngresoModuloS,
                        DatosUsuario.getMstrEstacionUsuario(),
                        EXE_MOD_REPCON);
            } catch (Exception e2) {
                comunService.escribirStackTrace(COD_MOD_REPCON, e2);
                throw new RuntimeException(MSG_ERROR_INGRESAR_MODULO);
            }
        }

        comunService.registrarAuditoria(mainStage, "Se ingresó al módulo de Reportes y Consultas", COD_MOD_REPCON);

    }

    private void reloj() {
        SimpleDateFormat formateadorFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formateadorHora = new SimpleDateFormat("hh:mm aa");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Date ahora = new Date();
                        String hora = formateadorHora.format(ahora);
                        String fecha = formateadorFecha.format(ahora);

                        lblHora.setText(hora);
                        lblFecha.setText(fecha);
                    }
                });

            }
        }, 1 * 1 * 1000, 1 * 1 * 1000);
    }

    public Parent getView() {
        return anchorPane;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

}
