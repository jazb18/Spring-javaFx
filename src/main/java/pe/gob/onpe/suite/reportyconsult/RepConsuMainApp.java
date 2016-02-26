package pe.gob.onpe.suite.reportyconsult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantCommon;
import pe.gob.onpe.suite.common.global.Acceso;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.reportyconsult.controller.MainController;

public class RepConsuMainApp extends Application {

    static String argumentoOriginal = "";
    static String[] argumentoFinal;
    private Stage primaryStage;

    ApplicationContext context;

    @Override
    public void start(Stage primaryStage) throws Exception {
        context = new AnnotationConfigApplicationContext(RepConsuControllerFactory.class);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(ConstantCommon.titleReporCons);

        this.primaryStage.getIcons().add(new Image(ConstantCommon.pathImageCommon + "icono_suite256x256.png"));

        MainController mainController = context.getBean(MainController.class);
        mainController.setMainStage(primaryStage);
        mainController.setContext(context);
        mainController.initMain();

        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            mainController.onCloseRequest();
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    //Método invocado automáticamente solamente en ambiente de desarrollo (para uso del módulo independientemente del módulo Inicio).    
    public static void main(String[] args) {

        try {

            InetAddress direccion = InetAddress.getLocalHost();
            String strEstacion = direccion.getHostName();

            //TODO PRUEBAS            
            argumentoOriginal = "J32000:C32000:C320000001:000:" + strEstacion + ":ADMINISTRADOR XXXXX:XXXXX:XXXXX:04:1:REPCON";

            argumentoFinal = argumentoOriginal.trim().split(":");
            DatosUsuario.cargarArgumentosUsuario(argumentoFinal);
            Acceso.setIntTipoProceso(Integer.parseInt(argumentoFinal[9]));
            Acceso.setEjecutableModulo(argumentoFinal[10]);

            launch(args);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RepConsuMainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
