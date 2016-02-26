/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult;

import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import pe.gob.onpe.suite.common.service.impl.CommonServiceFactory;
import pe.gob.onpe.suite.reportyconsult.controller.ActaObservacionesController;
import pe.gob.onpe.suite.reportyconsult.controller.ActasRedigitadasController;
import pe.gob.onpe.suite.reportyconsult.controller.ActasResueltasController;
import pe.gob.onpe.suite.reportyconsult.controller.AuditoriaDigitacionController;
import pe.gob.onpe.suite.reportyconsult.controller.AvanceEstadoActaController;
import pe.gob.onpe.suite.reportyconsult.controller.AvanceMesaxMesaController;
import pe.gob.onpe.suite.reportyconsult.controller.AvanceResultadosElectoralesController;
import pe.gob.onpe.suite.reportyconsult.controller.AvanceVotacionPreferencialController;
import pe.gob.onpe.suite.reportyconsult.controller.CincoCandidatosMasVotadosController;
import pe.gob.onpe.suite.reportyconsult.controller.CriterioSeleccionController;
import pe.gob.onpe.suite.reportyconsult.controller.ListadoActaPorDepartamentoController;
import pe.gob.onpe.suite.reportyconsult.controller.MainController;
import pe.gob.onpe.suite.reportyconsult.controller.MonitoreoActasController;
import pe.gob.onpe.suite.reportyconsult.controller.MonitoreoInformacionOficialController;
import pe.gob.onpe.suite.reportyconsult.controller.MensajesTransmitidosController;
import pe.gob.onpe.suite.reportyconsult.controller.MesasEstadoActaController;
import pe.gob.onpe.suite.reportyconsult.controller.MesasEstadoDigitacionController;
import pe.gob.onpe.suite.reportyconsult.controller.MesasEstadoMesaController;
import pe.gob.onpe.suite.reportyconsult.controller.ProbablesCandidatosElectosController;
import pe.gob.onpe.suite.reportyconsult.controller.ProcesosCifraRepartidoraController;
import pe.gob.onpe.suite.reportyconsult.controller.ProductividadxDigitadorController;
import pe.gob.onpe.suite.reportyconsult.controller.ReporteBarreraElectoralController;
import pe.gob.onpe.suite.reportyconsult.controller.ResultadoActasContabDistritoElectoralController;
import pe.gob.onpe.suite.reportyconsult.controller.ResultadoLotizacionActasController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenAvanceActasContabilizadasController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenEstadoActasController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenHistoricoActaResumidoController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenResultadoActasContabilizadasController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenResultadosController;
import pe.gob.onpe.suite.reportyconsult.controller.ResumenTotalCentroComputoController;
import pe.gob.onpe.suite.reportyconsult.controller.SufragantesyVotosEmitidosController;
import pe.gob.onpe.suite.reportyconsult.controller.TiempoDigitacionActaController;
import pe.gob.onpe.suite.reportyconsult.controller.TotalActaObservadaCentroComputoController;
import pe.gob.onpe.suite.reportyconsult.controller.exampleController;
import pe.gob.onpe.suite.reportyconsult.service.imp.RepConsultServiceFactory;
import pe.gob.onpe.suite.reportyconsult.thread.ReportThread;

/**
 *
 * @author jzafra
 */
@Configuration
@Import({CommonServiceFactory.class, RepConsultServiceFactory.class})
public class RepConsuControllerFactory {

    @Bean
    @Scope(value = "prototype")
    public MainController mainController() {
        return loadController("view/fxml/Main.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public MonitoreoActasController monitoreoActasController() {
        return loadController("view/fxml/MonitoreoActas.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ActaObservacionesController actaObservacionesController() {
        return loadController("view/fxml/ActaObservaciones.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public MonitoreoInformacionOficialController monitoreoInformacionOficialController() {
        return loadController("view/fxml/MonitoreoInformacionOficial.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public MensajesTransmitidosController relacionPuestaCeroController() {
        return loadController("view/fxml/MensajesTransmitidos.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public AvanceEstadoActaController avanceEstadoActaController() {
        return loadController("view/fxml/AvanceEstadoActa.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ResumenTotalCentroComputoController resumenTotalCentroComputoController() {
        return loadController("view/fxml/ResumenTotalCentroComputo.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ResumenEstadoActasController resumenEstadoActasController() {
        return loadController("view/fxml/ResumenEstadoActas.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public exampleController exController() {
        return loadController("view/fxml/example.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ListadoActaPorDepartamentoController listadoActaPorDepartamentoController() {
        return loadController("view/fxml/ListadoActaPorDepartamento.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ResumenAvanceActasContabilizadasController resumenAvanceActasContabilizadasController() {
        return loadController("view/fxml/ResumenAvanceActasContabilizadas.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ResumenResultadoActasContabilizadasController resumenResultadoActasContabilizadasController() {
        return loadController("view/fxml/ResumenResultadoActasContabilizadas.fxml");
    }

    @Bean(name = "criterioSeleccion")
    @Scope(value = "prototype")
    public CriterioSeleccionController criterioSeleccionController() {
        return new CriterioSeleccionController();
    }

    @Bean
    @Scope(value = "singleton")
    public SufragantesyVotosEmitidosController sufragantesyVotosEmitidosController() {
        return loadController("view/fxml/SufragantesyVotosEmitidos.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ResumenResultadosController resumenResultadosController() {
        return loadController("view/fxml/ResumenResultados.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public CincoCandidatosMasVotadosController cincoCandidatosMasVotadosController() {
        return loadController("view/fxml/CincoCandidatosMasVotados.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public AuditoriaDigitacionController auditoriaDigitacionController() {
        return loadController("view/fxml/AuditoriaDigitacion.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public AvanceResultadosElectoralesController avanceResultadosElectoralesController() {
        return loadController("view/fxml/AvanceResultadosElectorales.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ResultadoActasContabDistritoElectoralController resultadoActasContabDistritoElectoralController() {
        return loadController("view/fxml/ResultadoActasContabDistritoElectoral.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ResumenHistoricoActaResumidoController resumenHistoricoActaResumidoController() {
        return loadController("view/fxml/ResumenHistoricoActaResumido.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ProcesosCifraRepartidoraController procesosCifraRepartidoraController() {
        return loadController("view/fxml/ProcesosCifraRepartidora.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ResultadoLotizacionActasController resultadoLotizacionActasController() {
        return loadController("view/fxml/ResultadoLotizacionActas.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public MesasEstadoMesaController mesasEstadoMesaController() {
        return loadController("view/fxml/MesasEstadoMesa.fxml");
    }
    @Bean
    @Scope(value = "singleton")
    public AvanceMesaxMesaController avanceMesaxMesaController() {
        return loadController("view/fxml/AvanceMesaxMesa.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public MesasEstadoActaController mesasEstadoActaController() {
        return loadController("view/fxml/MesasEstadoActa.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public MesasEstadoDigitacionController mesasEstadoDigitacionController() {
        return loadController("view/fxml/MesasEstadoDigitacion.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ReporteBarreraElectoralController reporteBarreraElectoralController() {
        return loadController("view/fxml/ReporteBarreraElectoral.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ProbablesCandidatosElectosController candidatosElectosController() {
        return loadController("view/fxml/ProbablesCandidatosElectos.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public TiempoDigitacionActaController tiempoDigitacionActaController() {
        return loadController("view/fxml/TiempoDigitacionActa.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ProductividadxDigitadorController digitadorController() {
        return loadController("view/fxml/ProductividadxDigitador.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ActasRedigitadasController actasRedigitadasController() {
        return loadController("view/fxml/ActasRedigitadas.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public ActasResueltasController actasResueltasController() {
        return loadController("view/fxml/ActasResueltas.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public TotalActaObservadaCentroComputoController controller() {
        return loadController("view/fxml/TotalActaObservadaCentroComputo.fxml");
    }

    @Bean
    @Scope(value = "singleton")
    public AvanceVotacionPreferencialController avanceVotacionPreferencialController() {
        return loadController("view/fxml/AvanceVotacionPreferencial.fxml");
    }

    @Bean
    @Scope(value = "prototype")
    public ReportThread reportThread() {
        return new ReportThread();
    }

    private <T> T loadController(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RepConsuMainApp.class.getResource(fxmlFile));
            loader.load();
            return (T) loader.getController();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to load FXML file '%s'", fxmlFile), e);
        }
    }
}
