/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pe.gob.onpe.suite.common.constant.ConstantReportes;
import static pe.gob.onpe.suite.common.constant.ConstantReportes.*;
import pe.gob.onpe.suite.common.global.DatosUsuario;
import pe.gob.onpe.suite.common.service.ICentroComputoService;
import pe.gob.onpe.suite.common.service.IEleccionService;
import pe.gob.onpe.suite.common.service.ILocalService;
import pe.gob.onpe.suite.common.service.IOdpeService;
import pe.gob.onpe.suite.common.service.IUbigeoService;
import pe.gob.onpe.suite.domain.CatCentroComputo;
import pe.gob.onpe.suite.domain.CatEleccion;
import pe.gob.onpe.suite.domain.CatLocal;
import pe.gob.onpe.suite.domain.CatLote;
import pe.gob.onpe.suite.domain.CatOdpe;
import pe.gob.onpe.suite.domain.CatUbigeo;
import pe.gob.onpe.suite.domain.CatUnidadSoporte;
import pe.gob.onpe.suite.domain.RcEstadoActaDigitacion;
import pe.gob.onpe.suite.reportyconsult.service.RCAvanceEstadoActaService;
import pe.gob.onpe.suite.reportyconsult.service.RcAvanceVotacionPreferencialService;
import pe.gob.onpe.suite.reportyconsult.service.RcEstadisticaService;
import pe.gob.onpe.suite.reportyconsult.service.RcMesaEstadoActaService;
import pe.gob.onpe.suite.reportyconsult.service.RcMesaEstadoMesaService;
import pe.gob.onpe.suite.reportyconsult.service.RcProbablesCandidatosElectosService;
import pe.gob.onpe.suite.reportyconsult.service.RcProcesoCifraRepartidoraService;
import pe.gob.onpe.suite.reportyconsult.service.RcProcesoService;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasDEService;
import pe.gob.onpe.suite.reportyconsult.service.RcResultadoActasContabilizadasService;

/**
 *
 * @author ratayauri
 */
public class CriterioSeleccionController extends AnchorPane {

    private ComboBox<CatEleccion> cmbTipoElec;
    private ComboBox<CatOdpe> cmbOdpes;
    private ComboBox<CatCentroComputo> cmbCC;
    private ComboBox<CatUbigeo> cmbDepa;
    private ComboBox<CatUbigeo> cmbProv = new ComboBox<>();
    private ComboBox<CatUbigeo> cmbDistrito;
    private ComboBox<CatUbigeo> cmbDistritoElectoral;
    private ComboBox<CatLocal> cmbLocVotacion;
    private ComboBox<String> cmbEstado;
    private ComboBox<String> cmbTipoConexion;
    private ComboBox<CatUnidadSoporte> cmbUnidadSoporte;
    private ComboBox<CatUnidadSoporte> cmbCCxUnidadSoporte;
    private ComboBox<CatUnidadSoporte> cmbODPExUnidadSoporte;
    private ComboBox<String> cmbTipoConsulta;
    private ComboBox<CatLote> cmbTipoLote;
    private ComboBox<RcEstadoActaDigitacion> cmbEstadoDigitacion;
    private ComboBox<RcEstadoActaDigitacion> cmbEstadoDigitacion2;
    private HashMap<String, String> mapNcandidatos;
    private TextField textFieldAvance;
    private TextField textFieldNumMesa;
    private TextField textFieldTimeInit;
    private TextField textFieldTimeFin;
    private DatePicker firstCheckInDatePicker;
    private DatePicker lastCheckInDatePicker;

    @Autowired
    ApplicationContext context;

    @Autowired
    private IEleccionService eleccionService;

    @Autowired
    private IOdpeService odpeService;

    @Autowired
    private ICentroComputoService centroComputoService;

    @Autowired
    private IUbigeoService ubigeoService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private RCAvanceEstadoActaService rcAvanceEstadoActaService;

    @Autowired
    private RcProcesoService rcProcesoService;

    @Autowired
    private RcAvanceVotacionPreferencialService preferencialService;

    @Autowired
    RcResultadoActasContabilizadasService rcResultadoActasContabilizadasService;

    @Autowired
    RcProcesoCifraRepartidoraService rcProcesosCifraRepartidora;
    @Autowired
    RcResultadoActasContabilizadasDEService rcResultadoActasContabilizadasDEService;
    @Autowired
    RcMesaEstadoMesaService rcMesaEstadoMesaService;
    @Autowired
    RcMesaEstadoActaService rcMesaEstadoActaService;
    @Autowired
    RcProbablesCandidatosElectosService rcProbablesCandidatosElectosService;
    @Autowired
    ResultadoLotizacionActasController resultadoLotizacionActasController;
    @Autowired
    AvanceEstadoActaController AEACtrl;
    ResumenResultadoActasContabilizadasController resumActasCon;
    AvanceMesaxMesaController avanceMesaXMesaCon;
    ResultadoActasContabDistritoElectoralController resumActasConDE;
    AuditoriaDigitacionController auditoriaDigCon;

    private String codCentroComputo;
    private GridPane grid;
    private String lblDepartamento = "Departamento:";
    private List<String> listaEstado;
    private List<String> listaConexion;
    private List<CatUnidadSoporte> listaUnidadSoporte;
    private List<CatUnidadSoporte> listaCCxUnidadSoporte;
    private List<RcEstadoActaDigitacion> listaEstadoDigitacion;
    private List<Object[]> listaObjectFiltros = new ArrayList<>();

    private List<Object[]> listaDeFiltros;

    private boolean showTipoEleccion;
    private boolean showODPE;
    private boolean showCentroComputo;
    private boolean showDepartamento;
    private boolean showProvincia;
    private boolean showDistrito;
    private boolean showDistritoElectoral;
    private boolean showLocal;
    private boolean showEstado;
    private boolean showTipoConexion;
    private boolean showUnidadSoporte;
    private boolean showCCUnidadSoporte;
    private boolean showODPEUnidadSoporte;
    private boolean showSelector;
    private boolean showTipoConsulta;
    private boolean showtextFieldAvance;
    private boolean showtextFieldNumMesa;
    private boolean showTipoLote;
    private boolean showEstadoDigitacion;
    private boolean showEstadoDigitacion2;

    private boolean showMessageFirstDP = false;
    private boolean showMessageLastDP = false;

    private Stage stage;

    private String tipoModulo = "";

    public CriterioSeleccionController() {

        showTipoEleccion = Boolean.FALSE;
        showODPE = Boolean.FALSE;
        showCentroComputo = Boolean.FALSE;
        showDepartamento = Boolean.FALSE;
        showProvincia = Boolean.FALSE;
        showDistrito = Boolean.FALSE;
        showLocal = Boolean.FALSE;
        showEstado = Boolean.FALSE;
        showTipoConexion = Boolean.FALSE;
        showUnidadSoporte = Boolean.FALSE;
        showCCUnidadSoporte = Boolean.FALSE;
        showODPEUnidadSoporte = Boolean.FALSE;
        showSelector = Boolean.FALSE;
        showTipoConsulta = Boolean.FALSE;
        showtextFieldAvance = Boolean.FALSE;
        showDistritoElectoral = Boolean.FALSE;
        showtextFieldNumMesa = Boolean.FALSE;
        showTipoLote = Boolean.FALSE;
        showEstadoDigitacion = Boolean.FALSE;
        showEstadoDigitacion2 = Boolean.FALSE;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/pe/gob/onpe/suite/reportyconsult/view/fxml/CriterioSeleccion.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cargarCombos() {
        System.out.println("@@@@@@@@@@@@  CARGA DE COMBOS  @@@@@@@@@@@@@");
        /*Inicializacion y longitud de los ComboBox*/
        cargarPropiedadesCombobox();
        //Establecemos los Action de los comboBox
        cargarActionsComboBox();

        List<CatUbigeo> listDepartment = new ArrayList<>();
        List<CatUbigeo> listDistritoElectoral = new ArrayList<>();
        List<CatEleccion> listaTipoeleccion;
        List<CatUbigeo> listaDistritoElectoral;
        List<CatLote> listaLotes = new ArrayList<>();
        List<CatCentroComputo> listaCentroComputo = new ArrayList<>();
        List<RcEstadoActaDigitacion> listEstaDig = new ArrayList<>();
        List<CatUbigeo> listUbigeo = new ArrayList<>();
        listUbigeo.add(cargarDefaultComboUbigeo());
        List<CatLocal> listLocal = new ArrayList<>();
        listLocal.add(DefaultComboLocal());

        /*Carga del tipo de Eleccion*/
        listaTipoeleccion = getListaEleccionXCC(codCentroComputo);
        System.out.println("Lista recuperada de base de datos " + listaTipoeleccion.toString());
        if (listaTipoeleccion == null || listaTipoeleccion.size() < 1 || tipoModulo.equals("AEA") || tipoModulo.equals("RBE") || tipoModulo.equals("PCE") || tipoModulo.equals("AVP")) {
            System.out.println("La lista tipo de elecciones recuperada es nula ");
            listaTipoeleccion = CargalistaElecciones();
        }

        switch (tipoModulo) {
            case LISTADO_ACTASPOR_DEPARTAMENTO:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());

                listaCentroComputo.add(0, cargarDefaultComboCC());
                break;
            case RESUMEN_AVANCEACTAS_CONTABILIZADAS:
                listDepartment = getListDepartamentos("", ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                break;

            case RESULTADO_ACTAS_CONTABILIZADAS:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;

            case RESULTADO_ACTASCONTAB_DISTRITOELECT:
                listaTipoeleccion = new ArrayList<CatEleccion>();
                listaTipoeleccion = cargaEleccionCongresal();

                listDistritoElectoral = getListDistritoElectoralCN(codCentroComputo, "");
                listDistritoElectoral.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;

            case PROCESOS_CIFRA_REPARTIDORA:
                listaTipoeleccion = new ArrayList<CatEleccion>();
                listaTipoeleccion = CargalistaElecciones();
                listDistritoElectoral = getListDistritoElectoralCR("");
                break;

            case REPORTE_BARRERA_ELECTORAL:
                listDistritoElectoral = getListDistritoElectoralPCR(TODOS_DISTELECT_ENUM);
                listEstaDig = rcProcesoService.ObtenerAgrupacionPolitica(TIPOELECCION_CONGRESAL, "");
                listEstaDig.add(0, DefaultComboEstadoDigitacion());
                break;

            case AUDITORIA_DIGITACION:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;

            case AVANCE_RESULTADOS_ELECTORALES:
                listDistritoElectoral = getListDistritoElectoralCN(codCentroComputo, "");
                listDistritoElectoral.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;
            case RESULTADO_LOTIZACION_ACTAS:
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                listaLotes = getListaLotes();
                break;

            case MESA_ESTADO_MESA:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                listEstaDig = getListaEstadoDigitacion();
                listEstaDig.add(0, DefaultComboEstadoDigitacion());
                break;

            case "RHAUR":

                break;

            case PROBABLES_CANDIDATOS_ELECTOS:
                listDistritoElectoral = getListDistritoElectoralCR(VACIO);
                listDepartment = getCargoElectoral(TIPOELECCION_CONGRESAL);
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listEstaDig = getOrganizacionPoliticaPCE(TIPOELECCION_CONGRESAL);
                listEstaDig.add(0, DefaultComboEstadoDigitacion());
                break;

            case TIEMPO_DIGITACION_ACTA:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;
            case AVANCE_MESAPORMESA:
                listDepartment = getListDepartamentos(codCentroComputo, ""); //carga por default todos los departamentos
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;

            case PRODUCTIVIDAD_POR_DIGITADOR:
                listEstaDig = getUsuarioXCC(codCentroComputo); //carga por default todos los departamentos
                listEstaDig.add(0, DefaultComboEstadoDigitacion());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;

            case AVANCE_VOTACION_PREFERENCIAL:
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                listDistritoElectoral = getListDistritoElectoralCN(codCentroComputo, "");
                listDistritoElectoral.add(0, cargarDefaultComboUbigeo());
                listEstaDig.add(0, DefaultComboEstadoDigitacion());
                break;

            default:
                String tipoEleccion = listaTipoeleccion.get(0).getTipoElec();
                listDepartment = getListDepartamentosXEleccion("", codCentroComputo, tipoEleccion);
                listDepartment.add(0, cargarDefaultComboUbigeo());
                listaCentroComputo = getListaCentroComputo(codCentroComputo, "");
                break;
        }

        //EMPEZAMOS A DIBUJAR LA PANTALLA
        switch (tipoModulo) {
            case RESUMEN_AVANCEACTAS_CONTABILIZADAS:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                break;
            case RESUMEN_ESTADO_ACTAS:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                break;
            case RESULTADO_ACTAS_CONTABILIZADAS:
                grid = new GridPane();
                grid.setVgap(10);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                grid.prefHeight(146);
                break;
            case AUDITORIA_DIGITACION:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                grid.prefHeight(200);
                break;

            case SUFRAGANTES_VOTOS_EMITIDOS:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                grid.prefHeight(200);
                break;

            case MONITOREO_INFORMACION_OFICIAL:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(1200);
                grid.prefWidth(1200);
                grid.prefHeight(200);
                break;
            case AVANCE_MESAPORMESA:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(1200);
                grid.prefWidth(1200);
                grid.prefHeight(200);
                break;

            case MESA_ESTADO_MESA:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                grid.prefHeight(200);
                break;

            default:
                grid = new GridPane();
                grid.setVgap(5);
                grid.setHgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));
                grid.setMaxWidth(750);
                grid.prefWidth(750);
                grid.prefHeight(200);
                break;
        }

        //Falta armar el tipo de eleccion 03 ABOGADOS
        /* falta tipo conexion*/
        if (showTipoEleccion) {
            cargarComboTipoEleccionXCC(listaTipoeleccion);
        }
        if (showCentroComputo) {
            cargarComboCC(listaCentroComputo);
        }
        if (showODPE) {
            cargarComboOdpes(getListaODPEsXCC(codCentroComputo));//C30000 - C26000 codigo de prueba
        }
        if (showDepartamento) {
            cargarComboDepa(listDepartment);
            cmbDepa.getSelectionModel().selectFirst();
        }
        if (showProvincia) {
            cargarComboProvincia(listUbigeo);
            cmbProv.getSelectionModel().selectFirst();
        }
        if (showDistrito) {
            cargarComboDistrito(listUbigeo);
            cmbDistrito.getSelectionModel().selectFirst();
        }
        if (showEstado) {
            cargarComboEstado(getListaEstado());
            cmbEstado.getSelectionModel().selectFirst();
        }

        if (showUnidadSoporte) {
            cargarComboUnidadSoporte(getListaUnidadSoporte());
            cmbUnidadSoporte.getSelectionModel().selectFirst();
        }
        if (showCCUnidadSoporte) {
            cargarComboCCUnidadSoporte(getListaCCxUnidadSoporte());
            cmbCCxUnidadSoporte.getSelectionModel().selectFirst();
        }
        if (showODPEUnidadSoporte) {
            cargarComboODPEUnidadSoporte(getListaCCxUnidadSoporte());
            cmbODPExUnidadSoporte.getSelectionModel().selectFirst();
        }
        if (showTipoConexion) {
            switch (tipoModulo) {
                case "TAO":
                    cargarComboTipoConexion(getListaConexion());
                    break;
                default:
                    cargarComboTipoConexion(Arrays.asList("Todos", "Cobre", "Satelital"));
                    break;
            }
            cmbTipoConexion.getSelectionModel().selectFirst();
        }
        if (showLocal) {
            cargarComboLocal(listLocal);
            cmbLocVotacion.getSelectionModel().selectFirst();
        }
        if (showTipoConsulta) {
            cargarComboTipoConsulta(Arrays.asList("Resumido", "Detallado"));
            cmbTipoConsulta.getSelectionModel().selectFirst();
        }
        if (showDistritoElectoral) {
            cargarComboDistritoElectoral(listDistritoElectoral);
            cmbDistritoElectoral.getSelectionModel().selectFirst();
        }

        if (showtextFieldAvance) {
            cargarTextfieldAvance("AL 0.000%");
        }

        if (showtextFieldNumMesa) {
            textFieldNumMesa = new TextField();
        }
        if (showTipoLote) {
            cargarComboLotes(listaLotes);
            cmbTipoLote.getSelectionModel().selectFirst();
        }
        if (showEstadoDigitacion) {
            cargarComboEstadoDigitacion(listEstaDig);
            cmbEstadoDigitacion.getSelectionModel().selectFirst();
        }
        if (showEstadoDigitacion2) {
            cargarComboEstadoDigitacion2(listEstaDig);
        }

    }

    public void setDefaultStyleCss() {
        this.getStylesheets().add("/pe/gob/onpe/suite/common/view/css/mainCommon.css");
    }

    private void cmbTipoEleccion_onAction(ActionEvent event) {
        System.out.println("[cmbTipoEleccion_onAction] CodCentroComputo " + codCentroComputo);
        CatEleccion eleccion = cmbTipoElec.getValue();
        List<CatUbigeo> listDepartment = null;
        List<CatUbigeo> listDistritoElectoral = null;
        List<CatUbigeo> listCargoElectoral = null;
        List<RcEstadoActaDigitacion> listUsuario;
        List<RcEstadoActaDigitacion> listAgrupaPolitica = null;

        if (eleccion != null) {
            switch (tipoModulo) {
                case LISTADO_ACTASPOR_DEPARTAMENTO:
                    ListadoActaPorDepartamentoController beanCtrl = context.getBean(ListadoActaPorDepartamentoController.class);
                    System.out.println("Titpo de eleccion [LAD] :" + eleccion.getNombreEleccion());
                    beanCtrl.setLblCabecera(eleccion.getNombreEleccion());
                    break;
                case RESUMEN_AVANCEACTAS_CONTABILIZADAS:
                    System.out.println("Tipo de eleccion [RAAC] :" + eleccion.getNombreEleccion());
                    break;
                case RESUMEN_ESTADO_ACTAS:
                    System.out.println("Tipo de eleccion [REA] :" + eleccion.getNombreEleccion());
                    ResumenEstadoActasController beanCtrol = context.getBean(ResumenEstadoActasController.class);
                    beanCtrol.limpiarTabla();
                    break;
                case RESULTADO_ACTAS_CONTABILIZADAS:
                    cargarComboCC(getListCentroComputoXEleccion(eleccion.getTipoElec(), codCentroComputo));
                    cmbCC.getSelectionModel().selectFirst();
                    resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                    resumActasCon.limpiarEsquemaRegistro(eleccion.getTipoElec());
                    break;
                case SUFRAGANTES_VOTOS_EMITIDOS:
                    CatCentroComputo cc = cmbCC.getValue();
                    if (cc != null) {
                        listDepartment = getListDepartamentosXEleccion("", cc.getCentCompu(), eleccion.getTipoElec());
                        listDepartment.add(0, cargarDefaultComboUbigeo());
                        cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                        cargarComboDepa(listDepartment);
                        cmbDepa.getSelectionModel().selectFirst();
                    }
                    break;
                case PROCESOS_CIFRA_REPARTIDORA:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        listDepartment = getListDistritoElectoralCR("");
                        ProcesosCifraRepartidoraController beanCtrlCR = context.getBean(ProcesosCifraRepartidoraController.class);
//                       beanCtrlCR.ableRadioGroupInicial();
                    } else if (eleccion.getTipoElec().equals(TIPOELECCION_PARLAMENTOANDINO)) {
                        listDepartment = getListDistritoElectoralCR(TODOS_DISTELECT_ENUM);
                        ProcesosCifraRepartidoraController beanCtrlCR = context.getBean(ProcesosCifraRepartidoraController.class);
//                        beanCtrlCR.disableRadioGroupInicial();
                    }
                    cargarComboDistritoElectoral(listDepartment);
                    break;
                case AVANCE_RESULTADOS_ELECTORALES:
                    AvanceResultadosElectoralesController beanContr = context.getBean(AvanceResultadosElectoralesController.class);
                    listDistritoElectoral = getListDistritoElectoralCN(codCentroComputo, "");
                    listDistritoElectoral.add(0, cargarDefaultComboUbigeo());
                    beanContr.Cleaner();
                    cargarComboCC(getListaCentroComputo(codCentroComputo, ""));
                    cmbDistritoElectoral.getSelectionModel().selectFirst();
                    break;
                case REPORTE_BARRERA_ELECTORAL:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        //Set rs = getListDistritoElectoralPCR("")
                        listDepartment = getListDistritoElectoralPCR(TODOS_DISTELECT_ENUM);
                    } else if (eleccion.getTipoElec().equals(TIPOELECCION_PARLAMENTOANDINO)) {
                        //Set rs = getListDistritoElectoralPCR("D80000")
                        listDepartment = getListDistritoElectoralPCR(TODOS_DISTELECT_ENUM);
                    }
                    cargarComboDistritoElectoral(listDepartment);
                    break;

                case AVANCE_ESTADO_ACTA_X_CENTROCOMPUTO:
                    AEACtrl.limpiarObjetos();
                    break;

                case PROBABLES_CANDIDATOS_ELECTOS:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        listDistritoElectoral = getListDistritoElectoralCR(VACIO);
                        listCargoElectoral = getCargoElectoral(eleccion.getTipoElec());
                        listCargoElectoral.add(0, cargarDefaultComboUbigeo());
                        listAgrupaPolitica = getOrganizacionPoliticaPCE(eleccion.getTipoElec());
                        listAgrupaPolitica.add(0, DefaultComboEstadoDigitacion());

                    } else if (eleccion.getTipoElec().equals(TIPOELECCION_PARLAMENTOANDINO)) {
                        listDistritoElectoral = getListDistritoElectoralCR(TODOS_DISTELECT_ENUM);
                        listCargoElectoral = getCargoElectoral(eleccion.getTipoElec());
                        listCargoElectoral.add(0, cargarDefaultComboUbigeo());
                        listAgrupaPolitica = getOrganizacionPoliticaPCE(eleccion.getTipoElec());
                        listAgrupaPolitica.add(0, DefaultComboEstadoDigitacion());
                    }

                    cargarComboDistritoElectoral(listDistritoElectoral);
                    cargarComboDepa(listCargoElectoral);
                    cargarComboEstadoDigitacion(listAgrupaPolitica);
                    cmbDistritoElectoral.getSelectionModel().selectFirst();
                    cmbDepa.getSelectionModel().selectFirst();
                    cmbEstadoDigitacion.getSelectionModel().selectFirst();
                    break;

                case PRODUCTIVIDAD_POR_DIGITADOR:
                    cc = cmbCC.getValue();
                    listUsuario = getUsuarioXCC(cc.getCentCompu()); //carga por default todos los departamentos
                    listUsuario.add(0, DefaultComboEstadoDigitacion());
                    cargarComboCC(getListCentroComputoXEleccion(eleccion.getTipoElec(), codCentroComputo));
                    cmbDepa.getItems().clear();
                    cmbCC.getSelectionModel().selectFirst();
                    break;
                case AVANCE_MESAPORMESA:
                    if (avanceMesaXMesaCon == null) {
                        avanceMesaXMesaCon = context.getBean(AvanceMesaxMesaController.class);
                    }
                    cmbDepa.getItems().clear();
                    cargarComboCC(getListCentroComputoXEleccion(eleccion.getTipoElec(), codCentroComputo));
                    cmbCC.getSelectionModel().selectFirst();
                    break;
                default:
                    cmbDepa.getItems().clear();
                    cargarComboCC(getListCentroComputoXEleccion(eleccion.getTipoElec(), codCentroComputo));
                    cmbCC.getSelectionModel().selectFirst();
                    break;
            }
        }
    }

    private void cmbCC_onAction(ActionEvent event) {
        System.out.println("[cmbCC_onAction] CodCentroComputo " + codCentroComputo);
        List<CatUbigeo> listDepartment;
        List<CatUbigeo> listDistritoElectoral;
        List<RcEstadoActaDigitacion> listUsuario;
        CatCentroComputo cc = cmbCC.getValue();
        CatEleccion eleccion = cmbTipoElec.getValue();
        System.out.println("[cmbCC_onAction] Centro de computo seleccionado : " + cc);
        System.out.println("[cmbCC_onAction] Eleccion  seleccionado : " + eleccion);
        if (cc != null) {
            switch (tipoModulo) {
                case LISTADO_ACTASPOR_DEPARTAMENTO:
                    break;
                case RESULTADO_ACTAS_CONTABILIZADAS:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        listDepartment = getListDepartamentosCN(cc.getCentCompu(), "");
                    } else {
                        listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    }

                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                    resumActasCon.limpiarEsquemaRegistro(eleccion.getTipoElec());
                    break;
                case RESULTADO_ACTASCONTAB_DISTRITOELECT:
                    listDistritoElectoral = getListDistritoElectoralCN(cc.getCentCompu(), "");
                    listDistritoElectoral.add(0, cargarDefaultComboUbigeo());
                    cargarComboDistritoElectoral(listDistritoElectoral);
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    resumActasConDE = context.getBean(ResultadoActasContabDistritoElectoralController.class);
                    resumActasConDE.limpiarPantalla();
                    break;
                case MONITOREO_INFORMACION_OFICIAL:
                    listDepartment = getListDepartamentosXEleccion("", cc.getCentCompu(), eleccion.getTipoElec());
                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    break;
                case AVANCE_MESAPORMESA:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        listDepartment = getListDepartamentosCN(cc.getCentCompu(), "");
                    } else {
                        listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    }
                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    break;
                case RESUMEN_ESTADO_ACTAS:
                    ResumenEstadoActasController beanCtrl = context.getBean(ResumenEstadoActasController.class);
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    beanCtrl.limpiarTabla();
                    break;
                case RESUMEN_RESULTADOS:
                    ResumenResultadosController RERCtrl = context.getBean(ResumenResultadosController.class);
                    RERCtrl.cleaner();
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));

                    break;
                case RESULTADO_LOTIZACION_ACTAS:
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    break;
                case AUDITORIA_DIGITACION:
                    if (eleccion.getTipoElec().equals(TIPOELECCION_CONGRESAL)) {
                        listDepartment = getListDepartamentosCN(cc.getCentCompu(), "");
                    } else {
                        listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    }

                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    break;
                case SUFRAGANTES_VOTOS_EMITIDOS:
                    listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    break;

                case "TAO":
                case "ARD":
                case "TDA":
                case MESA_ESTADO_MESA:
                    listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    listDepartment.add(0, cargarDefaultComboUbigeo());

                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboDepa(listDepartment);
                    cmbDepa.getSelectionModel().selectFirst();
                    break;
                case RESUMEN_TOTAL_PORCC:
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    break;

                case PRODUCTIVIDAD_POR_DIGITADOR:
                    listDepartment = getListDepartamentos(cc.getCentCompu(), "");
                    listDepartment.add(0, cargarDefaultComboUbigeo());
                    listUsuario = getUsuarioXCC(cc.getCentCompu()); //carga por default todos los departamentos
                    listUsuario.add(0, DefaultComboEstadoDigitacion());

                    cargarComboDepa(listDepartment);
                    cargarComboOdpes(getListaODPEsXCC(cc.getCentCompu()));
                    cargarComboEstadoDigitacion(listUsuario);

                    cmbEstadoDigitacion.getSelectionModel().selectFirst();
                    cmbDepa.getSelectionModel().selectFirst();
                    break;

                default:
                    break;
            }
        }
    }

    private void cmbDistritoElectoral_onAction(ActionEvent event) {
        System.out.println("[cmbDistritoElectoral_onAction] CodCentroComputo " + codCentroComputo);
        CatUbigeo distritoElect = cmbDistritoElectoral.getSelectionModel().getSelectedItem();
        CatEleccion eleccion = cmbTipoElec.getValue();
        String DistritoElecto;
        switch (tipoModulo) {
            case RESULTADO_ACTASCONTAB_DISTRITOELECT:

                if (distritoElect != null) {
                    if (!distritoElect.getDescDepartamento().equals("<Todos>")) {
//                        System.out.println("@@@@@ NCANDIDATOS :" + mapNcandidatos.get(distritoElect.getCodDistritoElectoral()));
                        resumActasConDE = context.getBean(ResultadoActasContabDistritoElectoralController.class);
                        resumActasConDE.setNcandidatos(Float.parseFloat(mapNcandidatos.get(distritoElect.getCodDistritoElectoral())));

                    } else {
                        System.out.println("No se selecciono ningun distrito");
                    }
                }
                if (resumActasConDE == null) {
                    resumActasConDE = context.getBean(ResultadoActasContabDistritoElectoralController.class);
                }
                resumActasConDE.limpiarPantalla();

                break;
            case AVANCE_RESULTADOS_ELECTORALES:
                AvanceResultadosElectoralesController beanContr = context.getBean(AvanceResultadosElectoralesController.class);
                beanContr.Cleaner();
                break;

            case REPORTE_BARRERA_ELECTORAL:

                if (distritoElect != null) {
                    if (!distritoElect.getCodDistritoElectoral().equals("<Todos>")) {
                        if (distritoElect.getCodDistritoElectoral().equals(TODOS_DISTELECT_ENUM)) {
                            DistritoElecto = "";
                        } else {
                            DistritoElecto = distritoElect.getCodDistritoElectoral();
                        }
                        List<RcEstadoActaDigitacion> listAgrupacion = rcProcesoService.ObtenerAgrupacionPolitica(eleccion.getTipoElec(), DistritoElecto);
                        listAgrupacion.add(0, DefaultComboEstadoDigitacion());
                        cargarComboEstadoDigitacion(listAgrupacion);
                        cmbEstadoDigitacion.getSelectionModel().selectFirst();
                    }
                }
                break;

            case AVANCE_VOTACION_PREFERENCIAL:
                if (distritoElect != null) {
                    if (!distritoElect.getCodDistritoElectoral().equals("<Todos>")) {
                        if (distritoElect.getCodDistritoElectoral().equals(TODOS_DISTELECT_ENUM)) {
                            DistritoElecto = "";
                        } else {
                            DistritoElecto = distritoElect.getCodDistritoElectoral();
                        }
                        List<RcEstadoActaDigitacion> listaAgrupacion = preferencialService.getListadoOrgPoliticaXDE(eleccion.getTipoElec(), DistritoElecto);
                        cargarComboEstadoDigitacion(listaAgrupacion);
                        listaAgrupacion.add(0, DefaultComboEstadoDigitacion());
                        cmbEstadoDigitacion.getSelectionModel().selectFirst();
                    }
                }
                break;
        }

    }

    private void cmbDepa_onAction(ActionEvent event) {
        System.out.println("[cmbDepa_onAction] CodCentroComputo " + codCentroComputo);
        CatUbigeo depa = cmbDepa.getSelectionModel().getSelectedItem();
        Integer index = cmbDepa.getSelectionModel().getSelectedIndex();
        CatEleccion eleccion;
        switch (tipoModulo) {
            case LISTADO_ACTASPOR_DEPARTAMENTO: /*Listado de Actas por Deparatamento*/

                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        List<CatCentroComputo> listCentroComputo = getListCentroComputoXDepartamento(depa.getCodDepartamento(), codCentroComputo);
                        listCentroComputo.add(0, cargarDefaultComboCC());
                        cargarComboCC(listCentroComputo);
                        cmbCC.getSelectionModel().selectFirst();
                    } else {
                        cargarComboCC(Arrays.asList(cargarDefaultComboCC()));
                        cmbCC.getSelectionModel().selectFirst();
                    }
                }
                break;
            case RESUMEN_AVANCEACTAS_CONTABILIZADAS: /*Reporte de Avanze de Actas contabilizadas*/

                for (Node node : grid.getChildren()) {
                    if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 1) {
                        GridPane gridAux = (GridPane) node;
                        for (Node nodes : gridAux.getChildren()) {
                            if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 0) {
                                if (depa.getCodDepartamento().startsWith("9")) {
                                    ((Label) nodes).setText("Continente:");
                                } else {
                                    ((Label) nodes).setText("Departamento:");
                                }
                            }
                        }
                    }

                }
                break;
            case RESULTADO_ACTAS_CONTABILIZADAS: //Resumen de Actas contabilizadas
                if (resumActasCon == null) {
                    resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                }

                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        if (mapNcandidatos != null && (!mapNcandidatos.isEmpty())) {
                            System.out.println("@@@@@ NCANDIDATOS :" + mapNcandidatos.get(depa.getCodDepartamento()));
                            resumActasCon.setNcandidatos(Float.parseFloat(mapNcandidatos.get(depa.getCodDepartamento())));
                        } else {
                            System.out.println("@@@@@ mapNcandidatos es nulo");
                        }
                        List<CatUbigeo> listProvincia = new ArrayList<>();
                        if (depa.getDescDepartamento().equals(ConstantReportes.nameLimaCentro)) {
                            listProvincia = getListProvinciasLimaC(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        } else if (depa.getDescDepartamento().equals(ConstantReportes.nameLimaProvincia)) {
                            listProvincia = getListProvinciasLimaP(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        } else {
                            listProvincia = getListProvincias(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        }
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();
                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }

                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3) { //POSICION DE EL GRID UBIGEO
                            System.out.println("Nodo especifico :" + node);
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                                System.out.println("Sub Nodo especifico " + nodes + "--" + GridPane.getColumnIndex(nodes) + "--" + GridPane.getRowIndex(nodes));

                            }

                        }
                    }

                }
                resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                resumActasCon.limpiarEsquemaRegistro(eleccion.getTipoElec());
                break;

            case AUDITORIA_DIGITACION:
                if (auditoriaDigCon == null) {
                    auditoriaDigCon = context.getBean(AuditoriaDigitacionController.class);
                }
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        if (mapNcandidatos != null && (!mapNcandidatos.isEmpty())) {
                            System.out.println("@@@@@ NCANDIDATOS :" + mapNcandidatos.get(depa.getCodDepartamento()));
                            auditoriaDigCon.setNcandidatos(Float.parseFloat(mapNcandidatos.get(depa.getCodDepartamento())));
                        } else {
                            System.out.println("@@@@@ mapNcandidatos es nulo");
                        }

                        List<CatUbigeo> listProvincia = getListProvinciasXEleccion("", depa.getCodDepartamento(), eleccion.getTipoElec());
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();

                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }

                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3) { //POSICION DE EL GRID UBIGEO
                            System.out.println("Nodo especifico :" + node);
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                                System.out.println("Sub Nodo especifico " + nodes + "--" + GridPane.getColumnIndex(nodes) + "--" + GridPane.getRowIndex(nodes));

                            }

                        }
                    }

                }
                break;

            case SUFRAGANTES_VOTOS_EMITIDOS:
            case MONITOREO_INFORMACION_OFICIAL:
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {

                        List<CatUbigeo> listProvincia = new ArrayList<>();

                        listProvincia = getListProvincias(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());

                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();
                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }
                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3) { //POSICION DE EL GRID UBIGEO
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                            }

                        }
                    }

                }
                break;
            case AVANCE_MESAPORMESA:
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        if (avanceMesaXMesaCon == null) {
                            avanceMesaXMesaCon = context.getBean(AvanceMesaxMesaController.class);
                        }
                        if (mapNcandidatos != null && (!mapNcandidatos.isEmpty())) {
                            System.out.println("@@@@@ NCANDIDATOS :" + mapNcandidatos.get(depa.getCodDepartamento()));
                            avanceMesaXMesaCon.setNcandidatos(Float.parseFloat(mapNcandidatos.get(depa.getCodDepartamento())));
                        } else {
                            System.out.println("@@@@@ mapNcandidatos es nulo");
                        }
                        List<CatUbigeo> listProvincia = new ArrayList<>();
                        if (depa.getDescDepartamento().equals(ConstantReportes.nameLimaCentro)) {
                            listProvincia = getListProvinciasLimaC(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        } else if (depa.getDescDepartamento().equals(ConstantReportes.nameLimaProvincia)) {
                            listProvincia = getListProvinciasLimaP(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        } else {
                            listProvincia = getListProvincias(cmbCC.getSelectionModel().getSelectedItem().getCentCompu(), depa.getCodDepartamento());
                        }
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();
                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }
                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3) { //POSICION DE EL GRID UBIGEO
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                            }

                        }
                    }

                }
                break;
            case TIEMPO_DIGITACION_ACTA:
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        List<CatUbigeo> listProvincia = getListProvinciasXEleccion("", depa.getCodDepartamento(), eleccion.getTipoElec());
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();

                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }

                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3) { //POSICION DE EL GRID UBIGEO
                            System.out.println("Nodo especifico :" + node);
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                                System.out.println("Sub Nodo especifico " + nodes + "--" + GridPane.getColumnIndex(nodes) + "--" + GridPane.getRowIndex(nodes));

                            }
                        }
                    }

                }
                break;

            case MESA_ESTADO_MESA:
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        List<CatUbigeo> listProvincia = getListProvinciasXEleccion("", depa.getCodDepartamento(), eleccion.getTipoElec());
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();
                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }

                    for (Node node : grid.getChildren()) {
                        if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 4) { //POSICION DE EL GRID UBIGEO
                            System.out.println("Nodo especifico :" + node);
                            GridPane gridAux = (GridPane) node;
                            for (Node nodes : gridAux.getChildren()) {
                                if (GridPane.getColumnIndex(nodes) == 0 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Continente:");
                                    } else {
                                        ((Label) nodes).setText("Departamento:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 2 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Pais:");
                                    } else {
                                        ((Label) nodes).setText("Provincia:");
                                    }
                                }
                                if (GridPane.getColumnIndex(nodes) == 4 && GridPane.getRowIndex(nodes) == 3) {
                                    System.out.println(" Label 0-3 :" + ((Label) nodes).getText());
                                    if (depa.getCodDepartamento().startsWith("9")) {
                                        ((Label) nodes).setText("Estado:");
                                    } else {
                                        ((Label) nodes).setText("distrito:");
                                    }
                                }
                                System.out.println("Sub Nodo especifico " + nodes + "--" + GridPane.getColumnIndex(nodes) + "--" + GridPane.getRowIndex(nodes));

                            }

                        }
                    }

                }
                break;

            default:
                System.out.println("Deafult Eleccion Departamento " + cmbDepa.getSelectionModel().getSelectedIndex());
                eleccion = cmbTipoElec.getValue();
                if (depa != null) {
                    if (!depa.getDescDepartamento().equals("<Todos>")) {
                        List<CatUbigeo> listProvincia = getListProvinciasXEleccion("", depa.getCodDepartamento(), eleccion.getTipoElec());
                        listProvincia.add(0, cargarDefaultComboUbigeo());
                        cargarComboProvincia(listProvincia);
                        cmbProv.getSelectionModel().selectFirst();
                    } else {
                        cargarComboProvincia(Arrays.asList(cargarDefaultComboUbigeo()));
                        cmbProv.getSelectionModel().selectFirst();
                    }
                }
                break;
        }
    }

    private void cmbProv_onAction(ActionEvent event) {
        System.out.println("[cmbProv_onAction] CodCentroComputo " + codCentroComputo);
        CatUbigeo prov = cmbProv.getValue();
        CatEleccion eleccion = cmbTipoElec.getValue();
        System.out.println("prov :" + prov + " eleccion :" + eleccion);
        if (prov != null) {
            if (!prov.getDescProvincia().equals("<Todos>")) {
                List<CatUbigeo> listDistrito = getListDistritosXEleccion("", prov.getCodProvincia(), eleccion.getTipoElec());
                listDistrito.add(0, cargarDefaultComboUbigeo());
                cargarComboDistrito(listDistrito);
                cmbDistrito.getSelectionModel().selectFirst();
            } else {
                cargarComboDistrito(Arrays.asList(cargarDefaultComboUbigeo()));
                cmbDistrito.getSelectionModel().selectFirst();
            }
        }
        /*i-kleon 31-08-2015 : validando acciones correspondientes a cada modulo*/
        switch (tipoModulo) {
            case RESULTADO_ACTAS_CONTABILIZADAS:
                if (resumActasCon == null) {
                    resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                }
                resumActasCon.limpiarEsquemaRegistro(eleccion.getTipoElec());
                break;
        }
        /*f-kleon 31-08-2015 */

    }

    private void cmbDistrito_onAction(ActionEvent event) {
        CatUbigeo dist = cmbDistrito.getValue();
        CatEleccion eleccion = cmbTipoElec.getValue();
        switch (tipoModulo) {
            case SUFRAGANTES_VOTOS_EMITIDOS:
                List<CatLocal> listLocal = new ArrayList<>();
                if (dist != null) {
                    if (!dist.getDescDistrito().equals("<Todos>")) {
                        listLocal = getLocalXUbigeo("", dist.getCodDistrito());
                        listLocal.add(0, DefaultComboLocal());
                        cargarComboLocal(listLocal);
                        cmbLocVotacion.getSelectionModel().selectFirst();
                    } else {
                        listLocal.add(0, DefaultComboLocal());
                        cargarComboLocal(listLocal);
                        cmbLocVotacion.getSelectionModel().selectFirst();
                    }
                }
                break;
            case RESULTADO_ACTAS_CONTABILIZADAS:
                /*i-kleon 31-08-2015 : limpiamos el grid por cada tipo de consulta*/
                if (resumActasCon == null) {
                    resumActasCon = context.getBean(ResumenResultadoActasContabilizadasController.class);
                }
                resumActasCon.limpiarEsquemaRegistro(eleccion.getTipoElec());
                break;
            default:
                break;

        }

    }

    private void cmbTipoConsulta_onAction(ActionEvent event) {

        switch (tipoModulo) {
            case RESULTADO_ACTASCONTAB_DISTRITOELECT:
                if (resumActasConDE == null) {
                    resumActasConDE = context.getBean(ResultadoActasContabDistritoElectoralController.class);
                }
                resumActasConDE.limpiarPantalla();
                break;
        }

    }

    private void cmbOdpe_onAction(ActionEvent event) {

        switch (tipoModulo) {
            case RESULTADO_ACTASCONTAB_DISTRITOELECT:
                if (resumActasConDE == null) {
                    resumActasConDE = context.getBean(ResultadoActasContabDistritoElectoralController.class);
                }
                resumActasConDE.limpiarPantalla();
                break;
        }

    }

    private void cmbUnidadSoporte_onAction(ActionEvent event) {

        AEACtrl.limpiarObjetos();
        CatUnidadSoporte und = cmbUnidadSoporte.getValue();
        List<CatUnidadSoporte> lista;

        if (und != null) {
            if (!und.getDescripcion().equals("<Todos>")) {
                if (isSelector()) {
                    lista = rcAvanceEstadoActaService.ObtenerCComputo_X_USoporte(und.getCodigo());

                    lista.add(0, DefaultCombo());
                    cargarComboCCUnidadSoporte(lista);
                    cmbCCxUnidadSoporte.getSelectionModel().selectFirst();
                } else {
                    lista = rcAvanceEstadoActaService.Obtenerodpe_x_usoporte(und.getCodigo());
                    lista.add(0, DefaultCombo());
                    cargarComboODPEUnidadSoporte(lista);
                    cmbODPExUnidadSoporte.getSelectionModel().selectFirst();
                }
            } else {
                System.out.println("@@@@@ bean unidad nulo");
                cargarComboCCUnidadSoporte(Arrays.asList(DefaultCombo()));
                cargarComboODPEUnidadSoporte(Arrays.asList(DefaultCombo()));
                cmbCCxUnidadSoporte.getSelectionModel().selectFirst();
                cmbODPExUnidadSoporte.getSelectionModel().selectFirst();
            }
        }
    }

    private void CmbCCxUnidadSoporte_onAction(ActionEvent event) {
        AEACtrl.limpiarObjetos();
    }

    private void CmbEstado_onAction(ActionEvent event) {
        switch (tipoModulo) {
            case AVANCE_ESTADO_ACTA_X_CENTROCOMPUTO:
                AEACtrl.limpiarObjetos();
                break;
            case TOTAL_ACTA_OBSERVADA:
                String value = cmbEstado.getSelectionModel().getSelectedItem();
                int key;
                if (value.equals("DATA HISTÃƒâ€œRICA")) {
                    key = 1;
                    cmbTipoConexion.setDisable(Boolean.TRUE);
                } else {
                    key = 2;
                    cmbTipoConexion.setDisable(Boolean.FALSE);
                }
                System.out.println("key : " + key + " value:  " + value);
                break;
        }
    }

    private void cmbEstadoDigitacion_onAction(ActionEvent event) {
        RcEstadoActaDigitacion read = cmbEstadoDigitacion.getValue();
        CatUbigeo distritoElect = cmbDistritoElectoral.getSelectionModel().getSelectedItem();
        CatEleccion eleccion = cmbTipoElec.getValue();
        List<RcEstadoActaDigitacion> lista;
        String Codigo;
        switch (tipoModulo) {
            case AVANCE_VOTACION_PREFERENCIAL:
                if (read != null) {
                    if (!read.getDescripcion().equals("<Todos>")) {
                        Codigo = read.getCodigo();
                    } else {
                        Codigo = "";
                    }
                    List<CatLocal> listaCandidatos = preferencialService.getListaCandidatosxOrgPol(eleccion.getTipoElec(), distritoElect.getCodDistritoElectoral(), Codigo);
                    cargarComboLocal(listaCandidatos);
                    listaCandidatos.add(0, DefaultComboLocal());
                    cmbLocVotacion.getSelectionModel().selectFirst();
                }
                break;
            default:
                if (read != null) {
                    System.out.println(" Codigo : " + read.getCodigo());
                    if (!read.getDescripcion().equals("<Todos>")) {
                        if (!read.getCodigo().equals("A")) {
                            if (read.getCodigo().equals("B")) {
                                lista = rcMesaEstadoActaService.getObtenerestadosacta("B0");
                                lista.add(0, DefaultComboEstadoDigitacion());
                                cargarComboEstadoDigitacion2(lista);
                                cmbEstadoDigitacion2.getSelectionModel().selectFirst();
                                cmbEstadoDigitacion2.setVisible(true);
//                                System.out.println("Lista B :  " + lista);
                            }
                            if (read.getCodigo().equals("C")) {
                                lista = rcMesaEstadoActaService.getObtenerestadosacta("EA");
                                lista.add(0, DefaultComboEstadoDigitacion());
                                cargarComboEstadoDigitacion2(lista);
                                cmbEstadoDigitacion2.getSelectionModel().selectFirst();
                                cmbEstadoDigitacion2.setVisible(true);
//                                System.out.println("Lista C : " + lista);
                            }
                        } else {
//                            System.out.println("Lista A : ");
                            cmbEstadoDigitacion2.setVisible(false);
                        }
                    } else {
                        cmbEstadoDigitacion2.setVisible(false);
                    }
                } else {
                    System.out.println(" Valor Nullo");
                }
                break;
        }
    }

    private List<CatEleccion> getListaEleccionXCC(String codCC) {
        return eleccionService.getListaEleccionXCC(codCC);
    }

    private List<CatOdpe> getListaODPEsXCC(String centroComputo) {
        return odpeService.getListaODPEsXCC(centroComputo);
    }

    private List<CatCentroComputo> getListaCentroComputo(String centroComputo, String odpe) {
        return centroComputoService.getListaCentroComputo(centroComputo, odpe);
    }

    private List<CatLote> getListaLotes() {
        return rcResultadoActasContabilizadasDEService.getLotes();
    }

    private List<CatUbigeo> getListDepartamentosXEleccion(String codDep, String codCC, String codTipoElec) {
        return ubigeoService.getListDepartamentosXEleccion(codDep, codCC, codTipoElec);
    }

    private List<CatUbigeo> getListDepartamentoCN(String codDep, String codCC, String codTipoElec) {
        return ubigeoService.getListDepartamentoCN(codCC, codDep);
    }

    private List<CatUbigeo> getListDepartamentos(String codCC, String codDep) {
        return ubigeoService.getListDepartamento(codCC, codDep);
    }

    private List<CatUbigeo> getListDepartamentosCN(String codCC, String codDep) {
        return ubigeoService.getListDepartamentoCN(codCC, codDep);
    }

    private List<CatUbigeo> getListProvinciasXEleccion(String codCC, String codDep, String tipoElec) {
        return ubigeoService.getListProvinciasXEleccion(codCC, codDep, tipoElec);
    }

    private List<CatUbigeo> getListProvinciasLimaC(String codCC, String codDep) {
        return ubigeoService.getListProvinciaLimaC(codCC, codDep);
    }

    private List<CatUbigeo> getListProvinciasLimaP(String codCC, String codDep) {
        return ubigeoService.getListProvinciaLimaP(codCC, codDep);
    }

    private List<CatUbigeo> getListProvincias(String codCC, String codDep) {
        return ubigeoService.getListProvincia(codCC, codDep);
    }

    private List<CatCentroComputo> getListCentroComputoXDepartamento(String codDepart, String centroComputo) {
        return centroComputoService.getListCentroComputoXDepartamento(codDepart, centroComputo);
    }

    private List<CatUbigeo> getListDistritosXEleccion(String codCC, String codProv, String tipoElec) {
        return ubigeoService.getListDistritosXEleccion(codCC, codProv, tipoElec);
    }

    private List<CatUbigeo> getListDistritoElectoralCN(String codCC, String codDepart) {
        return rcResultadoActasContabilizadasService.getDistritoElectoralCN(codCC, codDepart);
    }

    private List<CatCentroComputo> getListCentroComputoXEleccion(String tipoElec, String centroComputo) {
        return centroComputoService.getListCentroComputoXEleccion(tipoElec, centroComputo);
    }

    private List<CatLocal> getLocalXUbigeo(String codLocal, String codUbigeo) {
        return localService.getLocalXUbigeo(codLocal, codUbigeo);
    }

    private List<CatUbigeo> getListDistritoElectoralCR(String distElectoral) {
        return rcProbablesCandidatosElectosService.obtenerDistElectoral(distElectoral);
    }

    private List<CatUbigeo> getListDistritoElectoralPCR(String distElectoral) {
        return rcProcesoService.obtenerDistritoElectoral(distElectoral);
    }

    private List<CatUbigeo> getCargoElectoral(String TipoEleccion) {
        return rcProbablesCandidatosElectosService.ObtenerCargo(TipoEleccion);
    }

    private List<RcEstadoActaDigitacion> getOrganizacionPoliticaPCE(String TipoEleccion) {
        return rcProbablesCandidatosElectosService.obtenerOrgPolitica(TipoEleccion);
    }

    private List<RcEstadoActaDigitacion> getUsuarioXCC(String CentroComputo) {
        RcEstadisticaService servicio = context.getBean(RcEstadisticaService.class);
        return servicio.get_usuarioxcc(CentroComputo);
    }

    public void cargarComboTipoEleccionXCC(List<CatEleccion> listaElec) {
        cmbTipoElec.setItems(FXCollections.observableList(listaElec));
        cmbTipoElec.getSelectionModel().selectFirst();
        cmbTipoElec.setButtonCell(new EleccionListCell());
        cmbTipoElec.setCellFactory((ListView<CatEleccion> param) -> new EleccionListCell());
    }

    public void cargarComboOdpes(List<CatOdpe> odpes) {
        cmbOdpes.getItems().clear();
        cmbOdpes.setItems(FXCollections.observableList(odpes));
        cmbOdpes.getSelectionModel().selectFirst();
        cmbOdpes.setButtonCell(new OdpeListCell());
        cmbOdpes.setCellFactory((ListView<CatOdpe> param) -> new OdpeListCell());
    }

    public void cargarComboCC(List<CatCentroComputo> listaCC) {
        cmbCC.getSelectionModel().clearSelection();
        if (!listaCC.isEmpty()) {

            cmbCC.setItems(FXCollections.observableList(listaCC));

            cmbCC.setButtonCell(new CentroComputoListCell());
            cmbCC.setCellFactory((ListView<CatCentroComputo> param) -> new CentroComputoListCell());
            cmbCC.getSelectionModel().selectFirst();
        }
    }

    public void cargarComboDepa(List<CatUbigeo> listaDepas) {
        mapNcandidatos = new HashMap<>();
        for (CatUbigeo iteratorList : listaDepas) {
            if (iteratorList.getCodDepartamento() != null && (!iteratorList.getCodDepartamento().equals(""))) {
                mapNcandidatos.put(iteratorList.getCodDepartamento(), iteratorList.getNcandidatos());
            }
        }
        cmbDepa.getSelectionModel().clearSelection();
        cmbDepa.setItems(FXCollections.observableList(listaDepas));
        cmbDepa.setButtonCell(new DepaListCell());
        cmbDepa.setCellFactory((ListView<CatUbigeo> param) -> new DepaListCell());
    }

    public void cargarComboProvincia(List<CatUbigeo> listaProvincias) {
        cmbProv.getSelectionModel().clearSelection();
        cmbProv.setItems(FXCollections.observableList(listaProvincias));
        cmbProv.setButtonCell(new ProvinciaListCell());
        cmbProv.setCellFactory((ListView<CatUbigeo> param) -> new ProvinciaListCell());
    }

    public void cargarComboDistrito(List<CatUbigeo> listaDistritos) {
        cmbDistrito.getSelectionModel().clearSelection();
        cmbDistrito.setItems(FXCollections.observableList(listaDistritos));
        cmbDistrito.setButtonCell(new DistritoListCell());
        cmbDistrito.setCellFactory((ListView<CatUbigeo> param) -> new DistritoListCell());
    }

    public void cargarComboLotes(List<CatLote> listaLotes) {
        cmbTipoLote.getSelectionModel().clearSelection();
        cmbTipoLote.setItems(FXCollections.observableList(listaLotes));
        cmbTipoLote.setButtonCell(new LoteListCell());
        cmbTipoLote.setCellFactory((ListView<CatLote> param) -> new LoteListCell());
    }

    public void cargarComboDistritoElectoral(List<CatUbigeo> listaDistritoElectoral) {
        mapNcandidatos = new HashMap<>();
        for (CatUbigeo iteratorList : listaDistritoElectoral) {
            if (iteratorList.getCodDistritoElectoral() != null && (!iteratorList.getCodDistritoElectoral().equals(""))) {
                mapNcandidatos.put(iteratorList.getCodDistritoElectoral(), iteratorList.getNcandidatos());
            }
        }

        cmbDistritoElectoral.getSelectionModel().clearSelection();
        cmbDistritoElectoral.setItems(FXCollections.observableList(listaDistritoElectoral));
        cmbDistritoElectoral.setButtonCell(new DistritoElectoralListCell());
        cmbDistritoElectoral.setCellFactory((ListView<CatUbigeo> param) -> new DistritoElectoralListCell());
        cmbDistritoElectoral.getSelectionModel().selectFirst();
    }

    public void cargarComboLocal(List<CatLocal> listaLocales) {
        cmbLocVotacion.getItems().clear();
        cmbLocVotacion.setItems(FXCollections.observableList(listaLocales));
        cmbLocVotacion.setButtonCell(new LocalListCell());
        cmbLocVotacion.setCellFactory((ListView<CatLocal> param) -> new LocalListCell());
    }

    public void cargarComboEstado(List<String> listaEstados) {
        cmbEstado.getItems().clear();
        cmbEstado.setItems(FXCollections.observableList(listaEstados));
        cmbEstado.setButtonCell(new EstadoListCell());
        cmbEstado.setCellFactory((ListView<String> param) -> new EstadoListCell());
    }

    public void cargarComboTipoConexion(List<String> listaTipoConexion) {
        cmbTipoConexion.getItems().clear();
        cmbTipoConexion.setItems(FXCollections.observableList(listaTipoConexion));
        cmbTipoConexion.setButtonCell(new TipoConexionListCell());
        cmbTipoConexion.setCellFactory((ListView<String> param) -> new TipoConexionListCell());
    }

    public void cargarComboUnidadSoporte(List<CatUnidadSoporte> listaUnidadSoporte) {
        cmbUnidadSoporte.getSelectionModel().clearSelection();
        cmbUnidadSoporte.setItems(FXCollections.observableList(listaUnidadSoporte));
        cmbUnidadSoporte.setButtonCell(new UnidadSoporteListCell());
        cmbUnidadSoporte.setCellFactory((ListView<CatUnidadSoporte> param) -> new UnidadSoporteListCell());
    }

    public void cargarComboCCUnidadSoporte(List<CatUnidadSoporte> listaCCxUnidadSoporte) {
        cmbCCxUnidadSoporte.getSelectionModel().clearSelection();
        cmbCCxUnidadSoporte.setItems(FXCollections.observableList(listaCCxUnidadSoporte));
        cmbCCxUnidadSoporte.setButtonCell(new CentroCUnidadSoporteListCell());
        cmbCCxUnidadSoporte.setCellFactory((ListView<CatUnidadSoporte> param) -> new CentroCUnidadSoporteListCell());
    }

    public void cargarComboODPEUnidadSoporte(List<CatUnidadSoporte> listaCCxUnidadSoporte) {
        cmbODPExUnidadSoporte.getSelectionModel().clearSelection();
        cmbODPExUnidadSoporte.setItems(FXCollections.observableList(listaCCxUnidadSoporte));
        cmbODPExUnidadSoporte.setButtonCell(new CentroCUnidadSoporteListCell());
        cmbODPExUnidadSoporte.setCellFactory((ListView<CatUnidadSoporte> param) -> new CentroCUnidadSoporteListCell());
    }

    public void cargarComboTipoConsulta(List<String> listaTipoConsultas) {
        cmbTipoConsulta.getItems().clear();
        cmbTipoConsulta.setItems(FXCollections.observableList(listaTipoConsultas));
        cmbTipoConsulta.setButtonCell(new TipoConsultaListCell());
        cmbTipoConsulta.setCellFactory((ListView<String> param) -> new TipoConsultaListCell());
    }

    public void cargarComboEstadoDigitacion(List<RcEstadoActaDigitacion> listaEstadoDigitacion) {
        cmbEstadoDigitacion.getItems().clear();
        cmbEstadoDigitacion.setItems(FXCollections.observableList(listaEstadoDigitacion));
        cmbEstadoDigitacion.setButtonCell(new EstadoDigitacionListCell());
        cmbEstadoDigitacion.setCellFactory((ListView<RcEstadoActaDigitacion> param) -> new EstadoDigitacionListCell());
    }

    public void cargarComboEstadoDigitacion2(List<RcEstadoActaDigitacion> listaEstadoDigitacion) {
        cmbEstadoDigitacion2.getItems().clear();
        cmbEstadoDigitacion2.setItems(FXCollections.observableList(listaEstadoDigitacion));
        cmbEstadoDigitacion2.setButtonCell(new EstadoDigitacionListCell());
        cmbEstadoDigitacion2.setCellFactory((ListView<RcEstadoActaDigitacion> param) -> new EstadoDigitacionListCell());
    }

    public void cargarTextfieldAvance(String defaultValue) {
        textFieldAvance.setText(defaultValue);
        textFieldAvance.setEditable(false);
        textFieldAvance.setDisable(true);
    }

    class OdpeListCell extends ListCell<CatOdpe> {

        @Override
        protected void updateItem(CatOdpe item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodiOdpe() + " " + item.getDescOdpe());
            } else {
                setGraphic(null);
            }
        }
    }

    class CentroComputoListCell extends ListCell<CatCentroComputo> {

        @Override
        protected void updateItem(CatCentroComputo item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCentCompu() + " " + item.getDescCompu());
            } else {
                setGraphic(null);
            }
        }
    }

    class DepaListCell extends ListCell<CatUbigeo> {

        @Override
        protected void updateItem(CatUbigeo item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodDepartamento() + " " + item.getDescDepartamento());
            } else {
                setGraphic(null);
            }
        }
    }

    class EleccionListCell extends ListCell<CatEleccion> {

        @Override
        protected void updateItem(CatEleccion item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getTipoElec() + " " + item.getNombreEleccion());
            } else {
                setGraphic(null);
            }
        }
    }

    class ProvinciaListCell extends ListCell<CatUbigeo> {

        @Override
        protected void updateItem(CatUbigeo item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodProvincia() + " " + item.getDescProvincia());
            } else {
                setGraphic(null);
            }
        }
    }

    class DistritoListCell extends ListCell<CatUbigeo> {

        @Override
        protected void updateItem(CatUbigeo item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodDistrito() + " " + item.getDescDistrito());
            } else {
                setGraphic(null);
            }
        }
    }

    class LoteListCell extends ListCell<CatLote> {

        @Override
        protected void updateItem(CatLote item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getTipoLote() + " " + item.getDescLote());
            } else {
                setGraphic(null);
            }
        }
    }

    class DistritoElectoralListCell extends ListCell<CatUbigeo> {

        @Override
        protected void updateItem(CatUbigeo item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodDistritoElectoral() + " " + item.getDistElec());
            } else {
                setGraphic(null);
            }
        }
    }

    class LocalListCell extends ListCell<CatLocal> {

        @Override
        protected void updateItem(CatLocal item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodiLocal() + " " + item.getNombreLocal());
            } else {
                setGraphic(null);
            }
        }
    }

    class TipoConsultaListCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item);
            } else {
                setGraphic(null);
            }
        }
    }

    class EstadoListCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item);
            } else {
                setGraphic(null);
            }
        }
    }

    class TipoConexionListCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item);
            } else {
                setGraphic(null);
            }
        }
    }

    class UnidadSoporteListCell extends ListCell<CatUnidadSoporte> {

        @Override
        protected void updateItem(CatUnidadSoporte item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodigo() + " " + item.getDescripcion());
            } else {
                setGraphic(null);
            }
        }
    }

    class CentroCUnidadSoporteListCell extends ListCell<CatUnidadSoporte> {

        @Override
        protected void updateItem(CatUnidadSoporte item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodigo() + " " + item.getDescripcion());
            } else {
                setGraphic(null);
            }
        }
    }

    class EstadoDigitacionListCell extends ListCell<RcEstadoActaDigitacion> {

        @Override
        protected void updateItem(RcEstadoActaDigitacion item, boolean bln) {
            super.updateItem(item, bln);
            if (item != null) {
                setText(item.getCodigo() + " " + item.getDescripcion());
            } else {
                setGraphic(null);
            }
        }
    }

    /*Carga de lista por defecto de Elecciones*/
    public List<CatEleccion> CargalistaElecciones() {
        List<CatEleccion> listaTipoEleccion = new ArrayList<>();
        CatEleccion catEleccion;
        /*if (tipoModulo.equals("PCR")) {
         catEleccion = new CatEleccion();
         catEleccion.setTipoElec("10");
         catEleccion.setNombreEleccion("PRESIDENTES Y VICEPRESIDENTE");
         listaTipoEleccion.add(catEleccion);
         }*/

        switch (tipoModulo) {
            case AVANCE_VOTACION_PREFERENCIAL:
            case PROBABLES_CANDIDATOS_ELECTOS:
            case REPORTE_BARRERA_ELECTORAL:
                catEleccion = new CatEleccion();
                catEleccion.setTipoElec("11");
                catEleccion.setNombreEleccion("CONGRESALES");
                listaTipoEleccion.add(catEleccion);
                catEleccion = new CatEleccion();
                catEleccion.setTipoElec("12");
                catEleccion.setNombreEleccion("PARLAMENTO ANDINO");
                listaTipoEleccion.add(catEleccion);
                break;

            default:

                catEleccion = new CatEleccion();
                catEleccion.setTipoElec("10");
                catEleccion.setNombreEleccion("PRESIDENTES Y VICEPRESIDENTE");
                listaTipoEleccion.add(catEleccion);
                catEleccion = new CatEleccion();
                catEleccion.setTipoElec("11");
                catEleccion.setNombreEleccion("CONGRESALES");
                listaTipoEleccion.add(catEleccion);
                catEleccion = new CatEleccion();
                catEleccion.setTipoElec("12");
                catEleccion.setNombreEleccion("PARLAMENTO ANDINO");
                listaTipoEleccion.add(catEleccion);

                if (tipoModulo.equals("AEA")) {
                    catEleccion = new CatEleccion();
                    catEleccion.setTipoElec("CO");
                    catEleccion.setNombreEleccion("CONSOLIDADO");
                    listaTipoEleccion.add(catEleccion);
                }
                break;
        }

        return listaTipoEleccion;
    }

    public List<CatEleccion> cargaEleccionCongresal() {
        CatEleccion catEleccion;
        List<CatEleccion> listaTipoEleccion = new ArrayList<>();
        catEleccion = new CatEleccion();
        catEleccion.setTipoElec("11");
        catEleccion.setNombreEleccion("CONGRESALES");
        listaTipoEleccion.add(catEleccion);
        return listaTipoEleccion;
    }

    /*Metodo para aÃƒÂ±adir la opcion de <Todos> a los combos de depart,dist y prov.
     */
    public CatUbigeo cargarDefaultComboUbigeo() {
        CatUbigeo newBeanAll = new CatUbigeo();
        newBeanAll.setDescDepartamento("<Todos>");
        newBeanAll.setCodDepartamento("");
        newBeanAll.setDescProvincia("<Todos>");
        newBeanAll.setCodProvincia("");
        newBeanAll.setDescDistrito("<Todos>");
        newBeanAll.setCodDistrito("");
        newBeanAll.setDistElec("<Todos>");
        newBeanAll.setCodDistritoElectoral("");
        return newBeanAll;
    }

    public CatCentroComputo cargarDefaultComboCC() {
        CatCentroComputo newBeanCC = new CatCentroComputo();
        newBeanCC.setCentCompu("");
        newBeanCC.setDescCompu("<Todos>");
        return newBeanCC;
    }

    public CatUnidadSoporte DefaultCombo() {
        CatUnidadSoporte newBeanAll = new CatUnidadSoporte();
        newBeanAll.setDescripcion("<Todos>");
        newBeanAll.setCodigo("");
        return newBeanAll;
    }

    public CatLocal DefaultComboLocal() {
        CatLocal newBeanAll = new CatLocal();
        newBeanAll.setNombreLocal("<Todos>");
        newBeanAll.setCodiLocal("");
        return newBeanAll;
    }

    public RcEstadoActaDigitacion DefaultComboEstadoDigitacion() {
        RcEstadoActaDigitacion newBeanAll = new RcEstadoActaDigitacion();
        newBeanAll.setDescripcion("<Todos>");
        newBeanAll.setCodigo("");
        return newBeanAll;
    }

    public boolean isShowTipoEleccion() {
        return showTipoEleccion;
    }

    public void setShowTipoEleccion(boolean showTipoEleccion) {
        this.showTipoEleccion = showTipoEleccion;
    }

    public boolean isShowODPE() {
        return showODPE;
    }

    public void setShowODPE(boolean showODPE) {
        this.showODPE = showODPE;
    }

    public boolean isShowCentroComputo() {
        return showCentroComputo;
    }

    public void setShowCentroComputo(boolean showCentroComputo) {
        this.showCentroComputo = showCentroComputo;
    }

    public boolean isShowDepartamento() {
        return showDepartamento;
    }

    public void setShowDepartamento(boolean showDepartamento) {
        this.showDepartamento = showDepartamento;
    }

    public boolean isShowProvincia() {
        return showProvincia;
    }

    public void setShowProvincia(boolean showProvincia) {
        this.showProvincia = showProvincia;
    }

    public boolean isShowDistrito() {
        return showDistrito;
    }

    public void setShowDistrito(boolean showDistrito) {
        this.showDistrito = showDistrito;
    }

    public boolean isShowLocal() {
        return showLocal;
    }

    public void setShowLocal(boolean showLocal) {
        this.showLocal = showLocal;
    }

    public CatEleccion getTipoEleccionSelected() {
        return cmbTipoElec.getValue();
    }

    public CatOdpe getODPESelected() {
        return cmbOdpes.getValue();
    }

    public CatCentroComputo getCentroComputoSelected() {
        return cmbCC.getValue();
    }

    public CatUbigeo getDepartamentoSelected() {
        return cmbDepa.getValue();
    }

    public CatUbigeo getProvinciaSelected() {
        return cmbProv.getValue();
    }

    public CatUbigeo getDistritoSelected() {
        return cmbDistrito.getValue();
    }

    public CatLocal getLocalSelected() {
        return cmbLocVotacion.getValue();
    }

    public String getCodCentroComputo() {
        return codCentroComputo;
    }

    public void setCodCentroComputo(String codCentroComputo) {
        this.codCentroComputo = codCentroComputo;
    }

    /*Getter y Setter de los combos*/
    public ComboBox<CatEleccion> getCmbTipoElec() {
        return cmbTipoElec;
    }

    public void setCmbTipoElec(ComboBox<CatEleccion> cmbTipoElec) {
        this.cmbTipoElec = cmbTipoElec;
    }

    public ComboBox<CatOdpe> getCmbOdpes() {
        return cmbOdpes;
    }

    public void setCmbOdpes(ComboBox<CatOdpe> cmbOdpes) {
        this.cmbOdpes = cmbOdpes;
    }

    public ComboBox<CatCentroComputo> getCmbCC() {
        return cmbCC;
    }

    public void setCmbCC(ComboBox<CatCentroComputo> cmbCC) {
        this.cmbCC = cmbCC;
    }

    public ComboBox<CatUbigeo> getCmbDepa() {
        return cmbDepa;
    }

    public void setCmbDepa(ComboBox<CatUbigeo> cmbDepa) {
        this.cmbDepa = cmbDepa;
    }

    public ComboBox<CatUbigeo> getCmbProv() {
        return cmbProv;
    }

    public void setCmbProv(ComboBox<CatUbigeo> cmbProv) {
        this.cmbProv = cmbProv;
    }

    public ComboBox<CatUbigeo> getCmbDistrito() {
        return cmbDistrito;
    }

    public void setCmbDistrito(ComboBox<CatUbigeo> cmbDistrito) {
        this.cmbDistrito = cmbDistrito;
    }

    public ComboBox<CatLocal> getCmbLocVotacion() {
        return cmbLocVotacion;
    }

    public void setCmbLocVotacion(ComboBox<CatLocal> cmbLocVotacion) {
        this.cmbLocVotacion = cmbLocVotacion;
    }

    public ComboBox<String> getCmbEstado() {
        return cmbEstado;
    }

    public void setCmbEstado(ComboBox<String> cmbEstado) {
        this.cmbEstado = cmbEstado;
    }

    public ComboBox<String> getCmbTipoConexion() {
        return cmbTipoConexion;
    }

    public void setCmbTipoConexion(ComboBox<String> cmbTipoConexion) {
        this.cmbTipoConexion = cmbTipoConexion;
    }

    public ComboBox<CatUnidadSoporte> getCmbUnidadSoporte() {
        return cmbUnidadSoporte;
    }

    public void setCmbUnidadSoporte(ComboBox<CatUnidadSoporte> cmbUnidadSoporte) {
        this.cmbUnidadSoporte = cmbUnidadSoporte;
    }

    public ComboBox<CatUnidadSoporte> getCmbCCxUnidadSoporte() {
        return cmbCCxUnidadSoporte;
    }

    public void setCmbCCxUnidadSoporte(ComboBox<CatUnidadSoporte> cmbCCxUnidadSoporte) {
        this.cmbCCxUnidadSoporte = cmbCCxUnidadSoporte;
    }

    public ComboBox<CatUnidadSoporte> getCmbODPExUnidadSoporte() {
        return cmbODPExUnidadSoporte;
    }

    public void setCmbODPExUnidadSoporte(ComboBox<CatUnidadSoporte> cmbODPExUnidadSoporte) {
        this.cmbODPExUnidadSoporte = cmbODPExUnidadSoporte;
    }

    public IEleccionService getEleccionService() {
        return eleccionService;
    }

    public void setEleccionService(IEleccionService eleccionService) {
        this.eleccionService = eleccionService;
    }

    public IOdpeService getOdpeService() {
        return odpeService;
    }

    public void setOdpeService(IOdpeService odpeService) {
        this.odpeService = odpeService;
    }

    public ICentroComputoService getCentroComputoService() {
        return centroComputoService;
    }

    public void setCentroComputoService(ICentroComputoService centroComputoService) {
        this.centroComputoService = centroComputoService;
    }

    public IUbigeoService getUbigeoService() {
        return ubigeoService;
    }

    public void setUbigeoService(IUbigeoService ubigeoService) {
        this.ubigeoService = ubigeoService;
    }

    public ILocalService getLocalService() {
        return localService;
    }

    public void setLocalService(ILocalService localService) {
        this.localService = localService;
    }

    public boolean isShowEstado() {
        return showEstado;
    }

    public void setShowEstado(boolean showEstado) {
        this.showEstado = showEstado;
    }

    public boolean isShowTipoConexion() {
        return showTipoConexion;
    }

    public void setShowTipoConexion(boolean showTipoConexion) {
        this.showTipoConexion = showTipoConexion;
    }

    public List<String> getListaEstado() {
        if (listaEstado == null) {
            listaEstado = new ArrayList<>();
        }
        return listaEstado;
    }

    public void setListaEstado(List<String> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public List<String> getListaConexion() {
        if (listaConexion == null) {
            listaConexion = new ArrayList<>();
        }
        return listaConexion;
    }

    public void setListaConexion(List<String> listaConexion) {
        this.listaConexion = listaConexion;
    }

    public boolean isShowUnidadSoporte() {
        return showUnidadSoporte;
    }

    public void setShowUnidadSoporte(boolean showUnidadSoporte) {
        this.showUnidadSoporte = showUnidadSoporte;
    }

    public List<CatUnidadSoporte> getListaUnidadSoporte() {
        return listaUnidadSoporte;
    }

    public void setListaUnidadSoporte(List<CatUnidadSoporte> listaUnidadSoporte) {
        listaUnidadSoporte.add(0, DefaultCombo());
        this.listaUnidadSoporte = listaUnidadSoporte;
    }

    public List<CatUnidadSoporte> getListaCCxUnidadSoporte() {
        return listaCCxUnidadSoporte;
    }

    public void setListaCCxUnidadSoporte(List<CatUnidadSoporte> listaCCxUnidadSoporte) {
        listaCCxUnidadSoporte.add(0, DefaultCombo());
        this.listaCCxUnidadSoporte = listaCCxUnidadSoporte;
    }

    public List<RcEstadoActaDigitacion> getListaEstadoDigitacion() {
        return listaEstadoDigitacion;
    }

    public void setListaEstadoDigitacion(List<RcEstadoActaDigitacion> listaEstadoDigitacion) {
        this.listaEstadoDigitacion = listaEstadoDigitacion;
    }

    public boolean isShowCCUnidadSoporte() {
        return showCCUnidadSoporte;
    }

    public void setShowCCUnidadSoporte(boolean showCCUnidadSoporte) {
        this.showCCUnidadSoporte = showCCUnidadSoporte;
    }

    public boolean isShowODPEUnidadSoporte() {
        return showODPEUnidadSoporte;
    }

    public void setShowODPEUnidadSoporte(boolean showODPEUnidadSoporte) {
        this.showODPEUnidadSoporte = showODPEUnidadSoporte;
    }

    public String getTipoModulo() {
        return tipoModulo;
    }

    public void setTipoModulo(String tipoModulo) {
        this.tipoModulo = tipoModulo;
    }

    public void cargarPropiedadesCombobox() {
        cmbTipoElec = new ComboBox<>();
        cmbOdpes = new ComboBox<>();
        cmbCC = new ComboBox<>();
        cmbDepa = new ComboBox<>();
        cmbDistrito = new ComboBox<>();
        cmbLocVotacion = new ComboBox<>();
        cmbEstado = new ComboBox<>();
        cmbTipoConexion = new ComboBox<>();
        cmbUnidadSoporte = new ComboBox<>();
        cmbCCxUnidadSoporte = new ComboBox<>();
        cmbODPExUnidadSoporte = new ComboBox<>();
        cmbTipoConsulta = new ComboBox<>();
        textFieldAvance = new TextField();
        cmbDistritoElectoral = new ComboBox<>();
        textFieldNumMesa = new TextField();
        firstCheckInDatePicker = new DatePicker();
        lastCheckInDatePicker = new DatePicker();
        cmbTipoLote = new ComboBox<>();
        cmbEstadoDigitacion = new ComboBox<>();
        cmbEstadoDigitacion2 = new ComboBox<>();
        textFieldTimeInit = new TextField();
        textFieldTimeFin = new TextField();

        lastCheckInDatePicker.setEditable(false);
        lastCheckInDatePicker.setPrefWidth(150);
        lastCheckInDatePicker.setValue(LocalDate.now());//checkInDatePicker.getValue()

        firstCheckInDatePicker.setEditable(false);
        firstCheckInDatePicker.setPrefWidth(150);
        firstCheckInDatePicker.setValue(lastCheckInDatePicker.getValue().minusDays(1));

        Calendar time = new GregorianCalendar();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        textFieldTimeInit.setEditable(false);
        textFieldTimeInit.setDisable(true);
        textFieldTimeInit.setText(hour + ":" + minute);
        textFieldTimeFin.setEditable(false);
        textFieldTimeFin.setDisable(true);
        textFieldTimeFin.setText(hour + ":" + minute);

        switch (tipoModulo) {

            case REPORTE_BARRERA_ELECTORAL:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbDistritoElectoral.setMaxWidth(450);
                cmbDistritoElectoral.setMinWidth(450);
                cmbDistritoElectoral.setPrefWidth(450);

                cmbEstadoDigitacion.setMaxWidth(450);
                cmbEstadoDigitacion.setMinWidth(450);
                cmbEstadoDigitacion.setPrefWidth(450);
                break;

            case AVANCE_ESTADO_ACTA_X_CENTROCOMPUTO:
                cmbTipoElec.setMinWidth(400);
                cmbTipoElec.setMaxWidth(400);
                cmbTipoElec.setPrefWidth(400);

                cmbUnidadSoporte.setMinWidth(400);
                cmbUnidadSoporte.setMaxWidth(400);
                cmbUnidadSoporte.setPrefWidth(400);

                cmbCCxUnidadSoporte.setMinWidth(400);
                cmbCCxUnidadSoporte.setMaxWidth(400);
                cmbCCxUnidadSoporte.setPrefWidth(400);

                cmbODPExUnidadSoporte.setMinWidth(400);
                cmbODPExUnidadSoporte.setMaxWidth(400);
                cmbODPExUnidadSoporte.setPrefWidth(400);

                cmbEstado.setMaxWidth(200);
                cmbEstado.setMinWidth(200);
                cmbEstado.setPrefWidth(200);
                break;

            case PROBABLES_CANDIDATOS_ELECTOS:
                cmbTipoElec.setMinWidth(500);
                cmbTipoElec.setMaxWidth(500);
                cmbTipoElec.setPrefWidth(500);

                cmbDistritoElectoral.setMinWidth(500);
                cmbDistritoElectoral.setMaxWidth(500);
                cmbDistritoElectoral.setPrefWidth(500);

                cmbDepa.setMaxWidth(500);
                cmbDepa.setMinWidth(500);
                cmbDepa.setPrefWidth(500);

                cmbEstadoDigitacion.setMaxWidth(500);
                cmbEstadoDigitacion.setMinWidth(500);
                cmbEstadoDigitacion.setPrefWidth(500);
                break;

            case RESUMEN_AVANCEACTAS_CONTABILIZADAS:
                cmbTipoElec.setMinWidth(400);
                cmbTipoElec.setMaxWidth(400);
                cmbTipoElec.setPrefWidth(400);

                cmbDepa.setMaxWidth(400);
                cmbDepa.setMinWidth(400);
                cmbDepa.setPrefWidth(400);
//                getCmbDepa
                break;

            case ACTA_REDIGITADA:
            case PRODUCTIVIDAD_POR_DIGITADOR:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbOdpes.setMinWidth(450);
                cmbOdpes.setMaxWidth(450);
                cmbOdpes.setPrefWidth(450);

                cmbCC.setMinWidth(450);
                cmbCC.setMaxWidth(450);
                cmbCC.setPrefWidth(450);

                cmbEstadoDigitacion.setMaxWidth(450);
                cmbEstadoDigitacion.setMinWidth(450);
                cmbEstadoDigitacion.setPrefWidth(450);
                break;

            case LISTADO_ACTASPOR_DEPARTAMENTO:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbDepa.setMaxWidth(450);
                cmbDepa.setMinWidth(450);
                cmbDepa.setPrefWidth(450);

                cmbCC.setMinWidth(450);
                cmbCC.setMaxWidth(450);
                cmbCC.setPrefWidth(450);

                cmbEstado.setMinWidth(450);
                cmbEstado.setMaxWidth(450);
                cmbEstado.setPrefWidth(450);
                break;

            case TOTAL_ACTA_OBSERVADA:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbOdpes.setMinWidth(450);
                cmbOdpes.setMaxWidth(450);
                cmbOdpes.setPrefWidth(450);

                cmbCC.setMinWidth(450);
                cmbCC.setMaxWidth(450);
                cmbCC.setPrefWidth(450);

                cmbEstado.setMinWidth(450);
                cmbEstado.setMaxWidth(450);
                cmbEstado.setPrefWidth(450);

                cmbTipoConexion.setMinWidth(450);
                cmbTipoConexion.setMaxWidth(450);
                cmbTipoConexion.setPrefWidth(450);
                break;

            case AVANCE_VOTACION_PREFERENCIAL:
                cmbTipoElec.setMinWidth(400);
                cmbTipoElec.setMaxWidth(400);
                cmbTipoElec.setPrefWidth(400);

                cmbCC.setMinWidth(400);
                cmbCC.setMaxWidth(400);
                cmbCC.setPrefWidth(400);

                cmbDistritoElectoral.setMaxWidth(400);
                cmbDistritoElectoral.setMinWidth(400);
                cmbDistritoElectoral.setPrefWidth(400);

                cmbEstadoDigitacion.setMaxWidth(400);
                cmbEstadoDigitacion.setMinWidth(400);
                cmbEstadoDigitacion.setPrefWidth(400);

                cmbLocVotacion.setPrefWidth(400);
                cmbLocVotacion.setMaxWidth(400);
                cmbLocVotacion.setMinWidth(400);
                break;

            case RESULTADO_ACTASCONTAB_DISTRITOELECT:
            case RESULTADO_ACTAS_CONTABILIZADAS:

                cmbTipoElec.setMinWidth(300);
                cmbTipoElec.setMaxWidth(300);
                cmbTipoElec.setPrefWidth(300);

                cmbOdpes.setMinWidth(300);
                cmbOdpes.setMaxWidth(300);
                cmbOdpes.setPrefWidth(300);

                cmbCC.setMinWidth(300);
                cmbCC.setMaxWidth(300);
                cmbCC.setPrefWidth(300);

                cmbDepa.setMaxWidth(180);
                cmbDepa.setMinWidth(180);
                cmbDepa.setPrefWidth(180);

                cmbProv.setMaxWidth(180);
                cmbProv.setMinWidth(180);
                cmbProv.setPrefWidth(180);

                cmbDistrito.setMaxWidth(180);
                cmbDistrito.setMinWidth(180);
                cmbDistrito.setPrefWidth(180);

                cmbTipoConsulta.setMinWidth(300);
                cmbTipoConsulta.setMaxWidth(300);
                cmbTipoConsulta.setPrefWidth(300);

                textFieldAvance.setMaxWidth(300);
                textFieldAvance.setMinWidth(300);
                textFieldAvance.setPrefWidth(300);
                textFieldAvance.setStyle("-fx-background-color: #FFC087;");

                cmbDistritoElectoral.setMaxWidth(300);
                cmbDistritoElectoral.setMinWidth(300);
                cmbDistritoElectoral.setPrefWidth(300);
                break;

            case MONITOREO_INFORMACION_OFICIAL:
                cmbTipoElec.setMinWidth(300);
                cmbTipoElec.setMaxWidth(300);
                cmbTipoElec.setPrefWidth(300);

                cmbOdpes.setMinWidth(300);
                cmbOdpes.setMaxWidth(300);
                cmbOdpes.setPrefWidth(300);

                cmbCC.setMinWidth(300);
                cmbCC.setMaxWidth(300);
                cmbCC.setPrefWidth(300);

                cmbDepa.setMaxWidth(180);
                cmbDepa.setMinWidth(180);
                cmbDepa.setPrefWidth(180);

                cmbProv.setMaxWidth(180);
                cmbProv.setMinWidth(180);
                cmbProv.setPrefWidth(180);

                cmbDistrito.setMaxWidth(180);
                cmbDistrito.setMinWidth(180);
                cmbDistrito.setPrefWidth(180);

                cmbEstado.setMaxWidth(300);
                cmbEstado.setMinWidth(300);
                cmbEstado.setPrefWidth(300);
                break;

            case RESUMEN_RESULTADOS:
                cmbOdpes.setMinWidth(660);
                cmbOdpes.setMaxWidth(660);
                cmbOdpes.setPrefWidth(660);

                cmbCC.setMinWidth(660);
                cmbCC.setMaxWidth(660);
                cmbCC.setPrefWidth(660);
                break;

            case TIEMPO_DIGITACION_ACTA:
            case AUDITORIA_DIGITACION:
            case SUFRAGANTES_VOTOS_EMITIDOS:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbOdpes.setMinWidth(450);
                cmbOdpes.setMaxWidth(450);
                cmbOdpes.setPrefWidth(450);

                cmbCC.setMinWidth(450);
                cmbCC.setMaxWidth(450);
                cmbCC.setPrefWidth(450);

                cmbDepa.setMaxWidth(150);
                cmbDepa.setMinWidth(150);
                cmbDepa.setPrefWidth(150);

                cmbProv.setMaxWidth(150);
                cmbProv.setMinWidth(150);
                cmbProv.setPrefWidth(150);

                cmbDistrito.setMaxWidth(150);
                cmbDistrito.setMinWidth(150);
                cmbDistrito.setPrefWidth(150);

                cmbLocVotacion.setPrefWidth(603);
                cmbLocVotacion.setMaxWidth(603);
                cmbLocVotacion.setMinWidth(603);
                break;

            case AVANCE_RESULTADOS_ELECTORALES:
                cmbTipoElec.setMinWidth(300);
                cmbTipoElec.setMaxWidth(300);
                cmbTipoElec.setPrefWidth(300);

                cmbDistritoElectoral.setMaxWidth(300);
                cmbDistritoElectoral.setMinWidth(300);
                cmbDistritoElectoral.setPrefWidth(300);
                break;

            case MESA_ESTADO_MESA:
                cmbTipoElec.setMinWidth(450);
                cmbTipoElec.setMaxWidth(450);
                cmbTipoElec.setPrefWidth(450);

                cmbOdpes.setMinWidth(450);
                cmbOdpes.setMaxWidth(450);
                cmbOdpes.setPrefWidth(450);

                cmbCC.setMinWidth(450);
                cmbCC.setMaxWidth(450);
                cmbCC.setPrefWidth(450);

                cmbEstadoDigitacion.setMaxWidth(450);
                cmbEstadoDigitacion.setMinWidth(450);
                cmbEstadoDigitacion.setPrefWidth(450);

                cmbDepa.setMaxWidth(150);
                cmbDepa.setMinWidth(150);
                cmbDepa.setPrefWidth(150);

                cmbProv.setMaxWidth(150);
                cmbProv.setMinWidth(150);
                cmbProv.setPrefWidth(150);

                cmbDistrito.setMaxWidth(150);
                cmbDistrito.setMinWidth(150);
                cmbDistrito.setPrefWidth(150);
                break;

            default:

                cmbTipoElec.setMinWidth(300);
                cmbTipoElec.setMaxWidth(300);
                cmbTipoElec.setPrefWidth(300);

                cmbOdpes.setMinWidth(300);
                cmbOdpes.setMaxWidth(300);
                cmbOdpes.setPrefWidth(300);

                cmbCC.setMinWidth(300);
                cmbCC.setMaxWidth(300);
                cmbCC.setPrefWidth(300);

                cmbTipoLote.setMaxWidth(150);
                cmbTipoLote.setMinWidth(150);
                cmbTipoLote.setPrefWidth(150);

                cmbEstadoDigitacion.setMaxWidth(200);
                cmbEstadoDigitacion.setMinWidth(200);
                cmbEstadoDigitacion.setPrefWidth(200);

                cmbEstadoDigitacion2.setMaxWidth(372);
                cmbEstadoDigitacion2.setMinWidth(372);
                cmbEstadoDigitacion2.setPrefWidth(372);

                cmbDepa.setMaxWidth(150);
                cmbDepa.setMinWidth(150);
                cmbDepa.setPrefWidth(150);

                cmbProv.setMaxWidth(150);
                cmbProv.setMinWidth(150);
                cmbProv.setPrefWidth(150);

                cmbDistrito.setMaxWidth(150);
                cmbDistrito.setMinWidth(150);
                cmbDistrito.setPrefWidth(150);

                cmbDistritoElectoral.setMaxWidth(150);
                cmbDistritoElectoral.setMinWidth(150);
                cmbDistritoElectoral.setPrefWidth(150);

                cmbLocVotacion.setPrefWidth(600);
                cmbLocVotacion.setMaxWidth(600);
                cmbLocVotacion.setMinWidth(600);

                cmbEstado.setMaxWidth(150);
                cmbEstado.setMinWidth(150);
                cmbEstado.setPrefWidth(150);

                cmbTipoConexion.setMaxWidth(150);
                cmbTipoConexion.setMinWidth(150);
                cmbTipoConexion.setPrefWidth(150);

                cmbUnidadSoporte.setMinWidth(300);
                cmbUnidadSoporte.setMaxWidth(300);
                cmbUnidadSoporte.setPrefWidth(300);

                cmbCCxUnidadSoporte.setMinWidth(300);
                cmbCCxUnidadSoporte.setMaxWidth(300);
                cmbCCxUnidadSoporte.setPrefWidth(300);

                cmbODPExUnidadSoporte.setMinWidth(300);
                cmbODPExUnidadSoporte.setMaxWidth(300);
                cmbODPExUnidadSoporte.setPrefWidth(300);

                cmbTipoConsulta.setMinWidth(110);
                cmbTipoConsulta.setMaxWidth(110);
                cmbTipoConsulta.setPrefWidth(110);

                textFieldAvance.setMaxWidth(300);
                textFieldAvance.setMinWidth(300);
                textFieldAvance.setPrefWidth(300);
                textFieldAvance.setStyle("-fx-background-color: #FFC087;");

                textFieldNumMesa.setMaxWidth(140);
                textFieldNumMesa.setMinWidth(140);
                textFieldNumMesa.setPrefWidth(140);

                break;
        }

    }

    public void cargarActionsComboBox() {
        cmbTipoElec.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                cmbTipoEleccion_onAction(event);
            }
        });

        cmbTipoElec.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                switch (tipoModulo) {
                    case RESULTADO_LOTIZACION_ACTAS:
                        if (firstCheckInDatePicker.getValue().isAfter(lastCheckInDatePicker.getValue()) && showMessageFirstDP) {
                            String msg = "La fecha final no puede ser menor que la fecha inicial";
                            resultadoLotizacionActasController.mostrarMensaje(msg);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        cmbCC.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                cmbCC_onAction(event);
            }
        });
        cmbDepa.setOnAction((ActionEvent event) -> {
            cmbDepa_onAction(event);
        });
        cmbDistritoElectoral.setOnAction((ActionEvent event) -> {
            cmbDistritoElectoral_onAction(event);
        });
        cmbProv.setOnAction((ActionEvent event) -> {
            cmbProv_onAction(event);
        });
        cmbOdpes.setOnAction((ActionEvent event) -> {
            cmbOdpe_onAction(event);
        });
        cmbDistrito.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                cmbDistrito_onAction(event);
            }
        });

        cmbUnidadSoporte.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cmbUnidadSoporte_onAction(event);
            }
        });

        cmbCCxUnidadSoporte.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                CmbCCxUnidadSoporte_onAction(event);
            }
        });

        cmbEstado.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                CmbEstado_onAction(event);
            }
        });
        cmbTipoConsulta.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cmbTipoConsulta_onAction(event);
            }
        });

        cmbEstadoDigitacion.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                cmbEstadoDigitacion_onAction(event);
            }
        });

        firstCheckInDatePicker.setOnMouseClicked(event -> {
            if (firstCheckInDatePicker.getValue().isAfter(lastCheckInDatePicker.getValue()) && showMessageFirstDP) {
                String msg = "La fecha final no puede ser menor que la fecha inicial";
                resultadoLotizacionActasController.mostrarMensaje(msg);
            }
        });
        firstCheckInDatePicker.setOnAction(event -> {
            showMessageLastDP = firstCheckInDatePicker.getValue().isAfter(lastCheckInDatePicker.getValue());
        });
        lastCheckInDatePicker.setOnMouseClicked(event -> {
            if (firstCheckInDatePicker.getValue().isAfter(lastCheckInDatePicker.getValue()) && showMessageLastDP) {
                String msg = "La fecha inicial no puede ser mayor que la fecha final";
                resultadoLotizacionActasController.mostrarMensaje(msg);
            }
        });
        lastCheckInDatePicker.setOnAction(event -> {
            showMessageFirstDP = firstCheckInDatePicker.getValue().isAfter(lastCheckInDatePicker.getValue());
        });

    }

    public boolean isSelector() {
        return showSelector;
    }

    public void setSelector(boolean selector) {
        this.showSelector = selector;
    }

    public List<Object[]> getListaDeFiltros() {
        return listaDeFiltros;
    }

    public void setListaDeFiltros(List<Object[]> listaDeFiltros) {
        this.listaDeFiltros = listaDeFiltros;
    }

    public boolean isShowTipoConsulta() {
        return showTipoConsulta;
    }

    public void setShowTipoConsulta(boolean showTipoConsulta) {
        this.showTipoConsulta = showTipoConsulta;
    }

    public ComboBox<String> getCmbTipoConsulta() {
        return cmbTipoConsulta;
    }

    public void setCmbTipoConsulta(ComboBox<String> cmbTipoConsulta) {
        this.cmbTipoConsulta = cmbTipoConsulta;
    }

    public TextField getTextFieldAvance() {
        return textFieldAvance;
    }

    public void setTextFieldAvance(TextField textFieldAvance) {
        this.textFieldAvance = textFieldAvance;
    }

    public boolean isShowtextFieldAvance() {
        return showtextFieldAvance;
    }

    public void setShowtextFieldAvance(boolean showtextFieldAvance) {
        this.showtextFieldAvance = showtextFieldAvance;
    }

    public ComboBox<CatUbigeo> getCmbDistritoElectoral() {
        return cmbDistritoElectoral;
    }

    public void setCmbDistritoElectoral(ComboBox<CatUbigeo> cmbDistritoElectoral) {
        this.cmbDistritoElectoral = cmbDistritoElectoral;
    }

    public boolean isShowDistritoElectoral() {
        return showDistritoElectoral;
    }

    public void setShowDistritoElectoral(boolean showDistritoElectoral) {
        this.showDistritoElectoral = showDistritoElectoral;
    }

    public TextField getTextFieldNumMesa() {
        return textFieldNumMesa;
    }

    public void setTextFieldNumMesa(TextField textFieldNumMesa) {
        this.textFieldNumMesa = textFieldNumMesa;
    }

    public boolean isShowtextFieldNumMesa() {
        return showtextFieldNumMesa;
    }

    public void setShowtextFieldNumMesa(boolean showtextFieldNumMesa) {
        this.showtextFieldNumMesa = showtextFieldNumMesa;
    }

    public DatePicker getFirstCheckInDatePicker() {
        return firstCheckInDatePicker;
    }

    public void setFirstCheckInDatePicker(DatePicker firstCheckInDatePicker) {
        this.firstCheckInDatePicker = firstCheckInDatePicker;
    }

    public DatePicker getLastCheckInDatePicker() {
        return lastCheckInDatePicker;
    }

    public void setLastCheckInDatePicker(DatePicker lastCheckInDatePicker) {
        this.lastCheckInDatePicker = lastCheckInDatePicker;
    }

    public ComboBox<CatLote> getCmbTipoLote() {
        return cmbTipoLote;
    }

    public void setCmbTipoLote(ComboBox<CatLote> cmbTipoLote) {
        this.cmbTipoLote = cmbTipoLote;
    }

    public boolean isShowTipoLote() {
        return showTipoLote;
    }

    public void setShowTipoLote(boolean showTipoLote) {
        this.showTipoLote = showTipoLote;
    }

    public ComboBox<RcEstadoActaDigitacion> getCmbEstadoDigitacion() {
        return cmbEstadoDigitacion;
    }

    public void setCmbEstadoDigitacion(ComboBox<RcEstadoActaDigitacion> cmbEstadoDigitacion) {
        this.cmbEstadoDigitacion = cmbEstadoDigitacion;
    }

    public boolean isShowEstadoDigitacion() {
        return showEstadoDigitacion;
    }

    public void setShowEstadoDigitacion(boolean showEstadoDigitacion) {
        this.showEstadoDigitacion = showEstadoDigitacion;
    }

    public ComboBox<RcEstadoActaDigitacion> getCmbEstadoDigitacion2() {
        return cmbEstadoDigitacion2;
    }

    public void setCmbEstadoDigitacion2(ComboBox<RcEstadoActaDigitacion> cmbEstadoDigitacion2) {
        this.cmbEstadoDigitacion2 = cmbEstadoDigitacion2;
    }

    public boolean isShowEstadoDigitacion2() {
        return showEstadoDigitacion2;
    }

    public void setShowEstadoDigitacion2(boolean showEstadoDigitacion2) {
        this.showEstadoDigitacion2 = showEstadoDigitacion2;
    }

    public TextField getTextFieldTimeInit() {
        return textFieldTimeInit;
    }

    public void setTextFieldTimeInit(TextField textFieldTimeInit) {
        this.textFieldTimeInit = textFieldTimeInit;
    }

    public TextField getTextFieldTimeFin() {
        return textFieldTimeFin;
    }

    public void setTextFieldTimeFin(TextField textFieldTimeFin) {
        this.textFieldTimeFin = textFieldTimeFin;
    }

    public void dibujarCombos(List<Object[]> listaObject) {
        listaObjectFiltros = listaObject;
        GridPane gridUbigeo;
        HBox combosRow;
        Label label;
        int rowGridpane = 0;
        Integer lengthElement = 150;
        try {
            for (Object[] iterate : listaObject) { //numero de filas
                combosRow = new HBox(10);
                combosRow.setStyle("-fx-background-color: blanchedalmond;");
                gridUbigeo = new GridPane();
                gridUbigeo.setHgap(10);
                for (int i = 0; i < iterate.length; i++) { // cantidad de elementos por filas

                    ComboBox cmbBox = new ComboBox();
                    TextField txtField = new TextField();
                    DatePicker datePicker = new DatePicker();
                    Object[] objectArray = (Object[]) iterate[i];
                    String nombre = (String) objectArray[0]; //nombre del elemento

                    if (objectArray[2] != null) {
                        lengthElement = (Integer) objectArray[2];//longtitud del componente
                    }

                    Integer columna = (Integer) objectArray[3]; //columna del gridpane
                    Integer fila = (Integer) objectArray[4]; //fila del gridpane
                    label = new Label();
                    label.getStyleClass().clear();
                    label.getStyleClass().add("lbl_color");
                    label.setText(nombre);
                    if (!nombre.equals(FILTRO_PROVINCIA) && !nombre.equals(FILTRO_DISTRITO)) {
                        if (nombre.equals("")) { //solo habilitado para datepicker
                            label.setMinWidth(10);
                            label.setMaxWidth(10);
                            label.setPrefWidth(10);
                        } else {
                            label.setMinWidth(110);
                            label.setMaxWidth(150);
                            label.setPrefWidth(110);
                        }
                    } else {
                        switch (tipoModulo) {
                            case MONITOREO_INFORMACION_OFICIAL:
                                label.setText("          " + nombre);
                                break;
                        }
                    }

                    gridUbigeo.add(label, columna, fila);
                    //Validando el objeto tipo ComboBox o TextField
                    if (objectArray[1] instanceof ComboBox) {
                        cmbBox = (ComboBox) objectArray[1]; // objeto combo
                        if (objectArray[2] != null) {
                            cmbBox.setMaxWidth(lengthElement);
                            cmbBox.setMinWidth(lengthElement);
                            cmbBox.setPrefWidth(lengthElement);
                        }
                        gridUbigeo.add(cmbBox, columna + 1, fila);
                    } else if (objectArray[1] instanceof TextField) {
                        txtField = (TextField) objectArray[1];
                        if (objectArray[2] != null) {
                            txtField.setMaxWidth(lengthElement);
                            txtField.setMinWidth(lengthElement);
                            txtField.setPrefWidth(lengthElement);
                        }
                        gridUbigeo.add(txtField, columna + 1, fila);
//                        combosRow.getChildren().add(txtField);
                    } else if (objectArray[1] instanceof DatePicker) {
                        datePicker = (DatePicker) objectArray[1];
                        gridUbigeo.add(datePicker, columna + 1, fila);
                    }

                }
                grid.addRow(rowGridpane, gridUbigeo);
//                grid.addRow(rowGridpane, combosRow);
                rowGridpane++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Excepcion General :" + ex);
        }
        this.getChildren().add(grid);

    }

}
