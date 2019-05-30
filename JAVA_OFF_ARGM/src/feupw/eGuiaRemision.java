/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import com.gruposantorun.comprobantes.modelo.modelo.CampoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.DetAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import com.gruposantorun.comprobantes.modelo.modelo.guia.Destinatario;
import com.gruposantorun.comprobantes.modelo.modelo.guia.GuiaDetallesDetalle;
import com.gruposantorun.comprobantes.modelo.modelo.guia.InfoGuiaRemision;
import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import com.gruposantorun.comprobantes.seguridad.Signer;
import com.gruposantorun.comprobantes.util.ClaveAcceso;
import com.gruposantorun.comprobantes.util.XMLUtil;

//--paquetes del online que se utilizan para recuperar la autorizacion
import com.gruposantorun.comprobantes.ws.AutorizacionComprobantesWS;
import com.gruposantorun.comprobantes.ws.VerificarExistenciaAutorizado;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

//--paquetes para la autorizacion de comprobantes off line en produccion
import com.gruposantorun.comprobantes.ws.produccion.off.EnvioComprobantesWSOFFPRO;
import com.gruposantorun.comprobantes.ws.produccion.off.VerificarDisponibilidadOFFPRO;

//--paquetes para la autorizacion de comprobantes off line en pruebas 
import com.gruposantorun.comprobantes.ws.pruebas.off.EnvioComprobantesWSOFFPRU;
import com.gruposantorun.comprobantes.ws.pruebas.off.VerificarDisponibilidadOFFPRU;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author progrmador
 */
public class eGuiaRemision {
    
    LeerSaiFact fac = new LeerSaiFact();
    VerificarExistenciaAutorizado ver      = new VerificarExistenciaAutorizado();
    VerificarDisponibilidadOFFPRU exi_pru  = new VerificarDisponibilidadOFFPRU();
    VerificarDisponibilidadOFFPRO exi_pro  = new VerificarDisponibilidadOFFPRO();
    ClaveAcceso clv = new ClaveAcceso();
    
    boolean pasar = false;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    int contador = -1;
    String tipo = "";    
    
    private static File archivo = null;
    private static FileWriter fichero = null;
    private static FileReader fr = null;
    private static BufferedReader br = null;
    private static PrintWriter escrive = null;
    String auxcod = "";
    String auxnum = "";
    String codemp = "";
    String codsuc = "";
    String tip = "";
    Double cod = 0.0;
    Double num = 0.0;
    
    String nomclave = "";
    String fecAutorizacion = "";
    String numeAutorizacion = "";
    String estadoAutorizacion = "AUTORIZADO";
    String estado = "";
    boolean save = false;
    boolean pasaAut = false;
    boolean procesamiento = false;
    boolean disponibilidad = false;
    boolean contingencia = false;
    boolean existeAutorizado = false;
    boolean obtenerAutorizado = false;
    boolean recibido = false;
    Date date;
    String identificador = "";
    String mensaje = "";
    String informacionAdicional = "";
    String tipo_m = "";
    String Servidor_emp = "";
    String ambiente = "";
    String certificado = "";
    String clavecertificado = "";
    String urlwebService = "";
    String urlwebService1 = "";
    int buscar_aut = 0;
    boolean bandera = false;
    boolean band = false;
    boolean exfccompelec = false;
    String nomAmbiente = "";
    
    String reenviaSRI  ="0";

    String envio_compe ="";
    
    int     envioSRI     = 0;
    boolean recupera_aut = false;
    boolean enviaDoc     = false;
    String  rucEmpresa   = "";
/////////////////////////////////////////////////////////////////////////////

    public eGuiaRemision() {
        primerPaso();
        procesarGuiaRemision();
    }
    
    private void primerPaso() {
        String tipa = "";
        String emp = "";
        String suc = "";
        String num = "";
        String fact = "";
        String existe = "";        
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        num = fac.getNumero();
        fact = fac.getFactura();            
        rucEmpresa = fac.getRuc();
        try {
            String cont = "";
            existe = "SELECT COUNT(tipo) AS CANTIDAD FROM FCCOMPELEC WITH(NOLOCK) "
                    + " WHERE  TIPO=" + "'" + tipa + "'"
                    + " AND    CODEMP=" + emp
                    + " AND    CODSUC=" + suc
                    + " AND    NUMERO_TRANS=" + num
                    + " AND    NUMERO_FACT=" + fact;
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
            PreparedStatement pstm = con.prepareStatement(existe);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                cont = rs.getString("CANTIDAD");
            }
            contador = Integer.parseInt(cont);
            System.out.println("cantidad " + contador);
            
            //mando a recuperar el envio sri de la tabla fccompelec
            envio_compe = "SELECT case when  isnull(envioSRI,'') = '' then '0' else envioSRI end AS envio FROM FCCOMPELEC WITH(NOLOCK) "
                    + " WHERE  TIPO=" + "'" + tipa + "'"
                    + " AND    CODEMP=" + emp
                    + " AND    CODSUC=" + suc
                    + " AND    NUMERO_TRANS=" + num
                    + " AND    NUMERO_FACT=" + fact;
            PreparedStatement pstmenvio = con.prepareStatement(envio_compe);
            ResultSet rsenvio = pstmenvio.executeQuery();
            while (rsenvio.next()) {
                reenviaSRI = rsenvio.getString("envio");
            }
            
            if ( (reenviaSRI == null) || (reenviaSRI.equals("")) ) {
                 reenviaSRI = "0";
            }
            
        } catch (SQLException | NumberFormatException e) {
            System.out.println(e.getMessage());
            contingencia = true;
            obtenerAutorizado = false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        if (contador == 0) {
            exfccompelec = true;
            System.out.println("existe en compelec?" + exfccompelec);
        } else {
            exfccompelec = false;
            System.out.println("existe en compelec?" + exfccompelec);
        }

    }// primer paso
    
    private void procesarGuiaRemision() {
        String tipa = "";
        String emp = "";
        String suc = "";
        String numguia = "";
        String fact = "";
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        numguia = fac.getNumero();
        fact = fac.getFactura(); 
        rucEmpresa = fac.getRuc();
        
        ParametrosExternos p = new ParametrosExternos();
        try {
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } //try

        
        cod = 0.0;
        num = 0.0;
        File dir = new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/");
        //------------------------------      
        int fila = 0;
        int paso = 0;
        int tm = 0;
        String clave_aux = "";
        String auxfecha = "";
        String nuevo = "";
        String[] fec;
        boolean tranferencia = false;
        boolean envio1 = false;
        boolean envio2 = false;
        auxcod = numguia;
        auxnum = fact;

        if (auxcod.equals("")) {
            cod = 0.0;
        }
        if (auxnum.equals("")) {
            num = 0.0;
        }
        tip = "FA";
        codemp = emp;
        codsuc = suc;
        cod = Double.parseDouble(auxcod);
        num = Double.parseDouble(auxnum);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        p.Parametros(codemp, codsuc);
        Servidor_emp = p.getRutaXmlAutorizado();
        ambiente = p.getTipoAmbiente();
        certificado = p.getRutaCertificado();
        clavecertificado = p.getClaveAcceso();
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        try {
            cst = con.prepareCall("{call buscar_comp_guiaRemision_xml(?,?,?,?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            cst.setString(3, tip);
            cst.setDouble(4, cod);
            cst.setDouble(5, num);

            rs = cst.executeQuery();
            if (rs.next()) {
                com.gruposantorun.comprobantes.modelo.modelo.guia.GuiaRemision g = new com.gruposantorun.comprobantes.modelo.modelo.guia.GuiaRemision();
                //--------------------------------------------------------------------------------------
                g.setVersion("1.1.0");
                g.setId("comprobante");
                clave_aux = clv.generarClaveAcceso(rs.getString("clave").trim());
                if (clave_aux == null) {
                    System.out.println("CLAVE ACCESO NO GENERADA, EL ARCHIVO NO SE PUEDE VALIDARSE");
                    nomclave = "guiaRemision_error" + rs.getString("secuencial").trim();
                    pasar = false;
                } else {
                    nomclave = clave_aux;
                    pasar = true;
                }//if
                InfoTributaria it = new InfoTributaria();
                it.setAmbiente(rs.getString("ambiente").trim());
                it.setTipoEmision(rs.getString("tipoEmision").trim());
                it.setRazonSocial(rs.getString("razonsocial").trim());
                it.setNombreComercial(rs.getString("nombreComercial").trim());
                it.setRuc(rs.getString("ruc").trim());
                it.setClaveAcceso(clave_aux);
                it.setCodDoc(rs.getString("codDoc").trim());
                it.setEstab(rs.getString("ESTAB").trim());
                it.setPtoEmi(rs.getString("PTOEMI"));
                it.setSecuencial(rs.getString("secuencial").trim());
                it.setDirMatriz(rs.getString("dirMatriz").trim());
                g.setInfoTributaria(it);

                InfoGuiaRemision iff = new InfoGuiaRemision();
                iff.setDirEstablecimiento(rs.getString("dirEstablecimiento").trim());
                iff.setDirPartida(rs.getString("dirPartida").trim());
                iff.setRazonSocialTransportista(rs.getString("razonSocialTransportista").trim());
                iff.setTipoIdentificacionTransportista(rs.getString("tipoIdentificacionTransportista").trim());
                iff.setRucTransportista(rs.getString("rucTransportista").trim());
                iff.setRise(rs.getString("rise").trim());
                iff.setObligadoContabilidad(rs.getString("obligadoContabilidad").trim());
                iff.setContribuyenteEspecial(rs.getString("contribuyenteEspecial").trim());
                iff.setFechaIniTransporte(rs.getString("fechaIniTransporte").trim());
                iff.setFechaFinTransporte(rs.getString("fechaFinTransporte").trim());
                iff.setPlaca(rs.getString("placa").trim());
                g.setInfoGuiaRemision(iff);


                List<Destinatario> lstdes = new ArrayList<>();
                Destinatario t1 = new Destinatario();
                t1.setIdentificacionDestinatario(rs.getString("identificacionDestinatario").trim());
                t1.setRazonSocialDestinatario(rs.getString("razonSocialDestinatario").trim());
                t1.setDirDestinatario(rs.getString("dirDestinatario").trim());
                t1.setMotivoTraslado(rs.getString("motivoTraslado").trim());
                t1.setDocAduaneroUnico(rs.getString("docUnicoAduanero").trim());
                t1.setCodEstabDestino(rs.getString("codEstabDestino").trim());
                t1.setRuta(rs.getString("ruta").trim());
                t1.setCodDocSustento(rs.getString("codDocSustento").trim());
                t1.setNumDocSustento(rs.getString("numDocSustento").trim());
                t1.setNumAutDocSustento(rs.getString("numDocAutSustento").trim());
                t1.setFechaEmisionDocSustento(rs.getString("fechaEmisionDocSustento").trim());

                //-----------------------------------------------------------------------DETALLE DE LA GUIA DE REMISION
                List<GuiaDetallesDetalle> lstDetGuia = new ArrayList();
                try {

                    cst = con.prepareCall("{call buscar_detalle_guia_remision_xml(?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, cod);
                    cst.setDouble(5, num);
                    rs = cst.executeQuery();

                    while (rs.next()) {
                        Object[] fila1 = new Object[5];
                        for (int i = 0; i < 5; i++) {
                            fila1[i] = rs.getObject(i + 1);
                        }// for
                        //md.addRow(fila1);
                        GuiaDetallesDetalle detGuia = new GuiaDetallesDetalle();
                        detGuia.setCodigoInterno(fila1[0].toString().trim());
                        detGuia.setCodigoAdicional(fila1[1].toString().trim());
                        detGuia.setDescripcion(fila1[2].toString().trim());
                        detGuia.setCantidad(new BigDecimal(fila1[3].toString().trim()));

                        List<DetAdicional> lsta = new ArrayList();
                        DetAdicional dea = new DetAdicional();
                        dea.setNombre("LINEA");
                        dea.setValor(fila1[4].toString().trim());
                        lsta.add(dea);
                        detGuia.setDetAdicional(lsta);
                        lstDetGuia.add(detGuia);
                    }// while


                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    System.exit(0);
                }// try 2

                t1.setDetalle(lstDetGuia);
                lstdes.add(t1);
                g.setDestinatario(lstdes);

                //-----------------------------------------------------------------------
                InfoAdicional infad = new InfoAdicional();
                List<CampoAdicional> lstifa = new ArrayList();
                CampoAdicional cmpad = new CampoAdicional();
                cmpad.setNombre("Proveedor");
                cmpad.setValue("Grupo Santorun");
                lstifa.add(cmpad);
                infad.setCampoAdicional(lstifa);
                g.setInfoAdicional(infad);

         
       //--------------------------------------------------------------------------------------
                if (dir.exists()) {
                    XMLUtil.marshall(g, "/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                } else {
                    dir.mkdirs();
                    XMLUtil.marshall(g, "/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                }
            } else {
                System.out.println("Cliente No Econtrado");
                tranferencia = false;
                System.exit(0);
            }
            cst.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            tranferencia = false;
            contingencia = true;
            obtenerAutorizado = false;
        }// fin de la generacion del xml
        
        
        /////------------------------------------------------ VALIDA EL XML GENERADO POR MEDIO DEL XSD
        if (tranferencia) {
                    File dirXSD = new File("xsd/");
                    //File dirXSD = new File("/home/asfact/public_html/api/jar/xsd/");
                    if (dirXSD.exists()) {
                        String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("xsd/guiaRemision.xsd"));
                        //String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("/home/asfact/public_html/api/jar/xsd/guiaRemision.xsd"));
                        if (mensajes == null) {
                            tranferencia = true;
                        } else {
                            System.out.println( "El archivo no se pudo Validar");
                            tranferencia = false;
                            //System.exit(0);
                        }//if
                    } else {
                        System.out.println("La carpeta XSD para validar los comprobantes no exixte ");
                        tranferencia = false;
                        System.exit(0);
                    }
                } else {
                    System.out.println("Guia de Remisión No Generada");
                    tranferencia = false;
                    System.exit(0);
                }// VALIDA EL XML fin de tranferencia
        
        
        //////---------------------------------------------------------FIRMA ELECTRONICA
        if (tranferencia) {
            File dirff = new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionFirmada/" + rucEmpresa + "/");
            String ruta2=new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
            if (dirff.exists()) {
                Signer s = new Signer(ruta2 ,"/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
                s.parSignGen(certificado, clavecertificado);
                try {
                    s.firmarFactura();
                    envio1 = true;

                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    tranferencia = false;
                    envio1 = false;
                    System.exit(0);
                }//trycatch
            } else {
                dirff.mkdirs();
                String ruta=new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
                Signer s = new Signer(ruta, "/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
                s.parSignGen(certificado, clavecertificado);
                try {
                    s.firmarFactura();
                    envio1 = true;
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    envio1 = false;
                    tranferencia = false;
                    System.exit(0);
                }//trycatch
            }// if si existe folio
        } else {
            System.out.println("La Guia de Remisión No Paso la validacion XSD");
            envio1 = false;
            System.exit(0);
        }
        
        //////--------------------------------------------ENVIO AL SRI A LOS DIFERENTES WEB SERVICES
        System.out.println("envio1 " + envio1); 
         if (envio1) {
             
            //---validamos el ambiente, conforme a eso se invoco los ws
            if (ambiente.equals("1")) {
            disponibilidad = exi_pru.existeConexionOFF(ambiente);
            }
            
            if (ambiente.equals("2")) {
            disponibilidad = exi_pro.existeConexionOFF(ambiente);
            }
            
            System.out.println("web service disponible " + disponibilidad);           
                
            //revisamos si el documento esta autorizado
            if (disponibilidad) {
                existeAutorizado = ver.VerificaWebServ(nomclave, ambiente);               
                if (existeAutorizado) {
                    System.out.println("documento autorizado ? " + existeAutorizado);
                    recupera_aut     = true;
                    envioSRI        = 1;
                }else {
                    System.out.println("documento autorizado ? " + existeAutorizado);
                    existeAutorizado = false;
                    enviaDoc = true;
                    recupera_aut = true;
                }
            }
            
            
            //------si el documento si tene autorizacion no entra al proceso de envio o tiene envioSRI en 0
            if (reenviaSRI.equals("0"))   {
                
                //------si el documento tiene autorizacion no entra al proceso de envio        
                if (disponibilidad) {
                    try {

                        //---procesamos con los ws expuestos para las pruebas 
                        if (ambiente.equals("1")) {

                            urlwebService = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                            CertificadosSSL.instalarCertificados();
                            EnvioComprobantesWSOFFPRU wsa = new EnvioComprobantesWSOFFPRU(urlwebService.trim());                                         
                            com.gruposantorun.comprobantes.ws.pruebas.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
                            System.out.println("primero: " + respuesta.getEstado());

                                if (respuesta.getEstado().equals("RECIBIDA")) {
                                    System.out.println("El documento ha sido enviada al SRI: ");
                                    recibido = true;
                                    envioSRI = 1;              
                                } else {
                                    recibido = false;

                                if (respuesta.getEstado().equals("DEVUELTA")) {
                                    if (existeAutorizado) {  
                                        envioSRI = 1;
                                        reenviaSRI = "1";
                                    }
                                    else
                                    {
                                        System.out.println("El documento no ha sido recibida por el SRI: ");
                                        envioSRI = 0;
                                        List<com.gruposantorun.comprobantes.ws.pruebas.off.Comprobante> lst = respuesta.getComprobantes().getComprobante();
                                        System.out.println("ichi" + lst.isEmpty() );

                                            for (com.gruposantorun.comprobantes.ws.pruebas.off.Comprobante comprobante : lst) {
                                            List<com.gruposantorun.comprobantes.ws.pruebas.off.Mensaje> lstMensaje = comprobante.getMensajes().getMensaje();

                                                for (com.gruposantorun.comprobantes.ws.pruebas.off.Mensaje mensaje : lstMensaje) {
                                                    if (mensaje.getMensaje().equals("CLAVE DE ACCESO EN PROCESAMIENTO")) {
                                                        System.out.println("ENVIO: " + respuesta.getEstado()
                                                        + "\n Mensage: " + mensaje.getMensaje()
                                                        + "\n Motivo: " + mensaje.getInformacionAdicional()
                                                        + "\n Generación de contingencia...");
                                                        envioSRI = 0;
                                                    } else {

                                                        if (mensaje.getMensaje().equals("CLAVE ACCESO REGISTRADA")) {
                                                            envioSRI = 1;
                                                        } else {
                                                            System.out.println("estado: " + respuesta.getEstado());
                                                            System.out.println("identificador: " + mensaje.getIdentificador());
                                                            System.out.println("tipo: " + mensaje.getTipo()) ;
                                                            System.out.println("infoad: " + mensaje.getInformacionAdicional()) ;
                                                            System.out.println("ENVIO: " + respuesta.getEstado()
                                                            + "\n Mensage: " + mensaje.getMensaje()
                                                            + "\n Motivo: " + mensaje.getInformacionAdicional()
                                                            + "\n Corrija el error y vuelva a enviarla.");
                                                            envioSRI = 0;
                                                        } // fin de la clave en procesamiento
                                                    } // IF
                                                }//for
                                            }//for
                                        }//fin de else autorizado
                                    }// fin de devuelta
                                }// fin de recibida
                        }

                        //---procesamos con los ws expuestos para  el ambiente produccion 
                        if (ambiente.equals("2")) {

                            urlwebService = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                            CertificadosSSL.instalarCertificados();
                            EnvioComprobantesWSOFFPRO wsa = new EnvioComprobantesWSOFFPRO(urlwebService.toString().trim());
                            com.gruposantorun.comprobantes.ws.produccion.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
                            System.out.println("primero: " + respuesta.getEstado());

                            if (respuesta.getEstado().equals("RECIBIDA")) {
                                System.out.println("El documento ha sido enviada al SRI: " );
                                    recibido = true;
                                    envioSRI = 1;              
                                } else {
                                    recibido = false;

                                if (respuesta.getEstado().equals("DEVUELTA")) {
                                    if (existeAutorizado) {  
                                        envioSRI = 1;
                                        reenviaSRI = "1";
                                    }
                                    else
                                    {
                                        System.out.println("El documento no ha sido recibida por el SRI: ");
                                        envioSRI = 0;
                                        List<com.gruposantorun.comprobantes.ws.produccion.off.Comprobante> lst = respuesta.getComprobantes().getComprobante();
                                        System.out.println("ichi" + lst.isEmpty() );

                                            for (com.gruposantorun.comprobantes.ws.produccion.off.Comprobante comprobante : lst) {
                                            List<com.gruposantorun.comprobantes.ws.produccion.off.Mensaje> lstMensaje = comprobante.getMensajes().getMensaje();

                                                for (com.gruposantorun.comprobantes.ws.produccion.off.Mensaje mensaje : lstMensaje) {
                                                    if (mensaje.getMensaje().equals("CLAVE DE ACCESO EN PROCESAMIENTO")) {
                                                        System.out.println("ENVIO: " + respuesta.getEstado()                                                        
                                                        + "\n Mensage: " + mensaje.getMensaje()
                                                        + "\n Motivo: " + mensaje.getInformacionAdicional()
                                                        + "\n Generación de contingencia...");
                                                        envioSRI = 0;
                                                    } else {
                                                        if (mensaje.getMensaje().equals("CLAVE ACCESO REGISTRADA")) {
                                                            envioSRI = 1;
                                                        } else {
                                                            System.out.println("estado: " + respuesta.getEstado());
                                                            System.out.println("identificador: " + mensaje.getIdentificador());
                                                            System.out.println("tipo: " + mensaje.getTipo()) ;
                                                            System.out.println("infoad: " + mensaje.getInformacionAdicional()) ;
                                                            System.out.println("ENVIO: " + respuesta.getEstado()
                                                            + "\n Mensage: " + mensaje.getMensaje()
                                                            + "\n Motivo: " + mensaje.getInformacionAdicional()
                                                            + "\n Corrija el error y vuelva a enviarla.");
                                                            envioSRI = 0;
                                                        } // fin de la clave en procesamiento
                                                    } // IF
                                                }//for
                                            }//for
                                        }//fin de else autorizado
                                    }// fin de devuelta
                                }// fin de recibida
                        }

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FEUPW_FACT.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("ENVIO(MalformedURLException) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no enviado... ");

                    } catch (WebServiceException ex) {
                        Logger.getLogger(FEUPW_FACT.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("ENVIO(WebServiceException) \nSin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no enviado... ");
                    }// fin try catch  
                }
            }

            

        }// fin envio1 
        System.out.println("envioSRI: " +envioSRI);
        
        //----------------------si el documento esta autorizado manda a recuperar la autorizacion
        if (reenviaSRI.equals("1"))   {
            File ret_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionAutorizada/" + rucEmpresa + "/");
            File ret_no_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionNoAutorizada/" + rucEmpresa + "/");
            try {
                if (ambiente.equals("1")) {
                    urlwebService1 = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
                }
                if (ambiente.equals("2")) {
                    urlwebService1 = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
                }
                CertificadosSSL.instalarCertificados();
                List<String> lst_at = new ArrayList();
                AutorizacionComprobantesWS ws = new AutorizacionComprobantesWS(urlwebService1.trim());
                RespuestaComprobante respuesta = ws.autorizarComprobante(nomclave);
                List<Autorizacion> lstAutorizacion = respuesta.getAutorizaciones().getAutorizacion();
                int contador = 0;
                int tam = lstAutorizacion.size();
                for (Autorizacion autorizacion : lstAutorizacion) {
                    try {
                        A aut = new A();
                        if (autorizacion.getEstado().equals("AUTORIZADO")) {
                            bandera = true;
                            band = true;
                        } 
                        contador++;
                        if (bandera && band) {
                            band = false;
                            aut.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");
                            aut.setEstado(autorizacion.getEstado());
                            Calendar calendar = autorizacion.getFechaAutorizacion().toGregorianCalendar();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");//yyyy/MM/dd
                            String fechaAutorizacion = sdf.format(calendar.getTime());
                            //--
                            if (ambiente.equals("1")) {
                                nomAmbiente = "PRUEBAS";
                            }

                            if (ambiente.equals("2")) {
                                nomAmbiente = "PRODUCCION";
                            }
                            
                            aut.setFechaAutorizacion(fechaAutorizacion);
                            aut.setMensajes(autorizacion.getMensajes());
                            aut.setAmbiente(nomAmbiente);
                            aut.setNumeroAutorizacion(autorizacion.getNumeroAutorizacion());

                            if (aut.getEstado().equals(estadoAutorizacion)) {
                                fecAutorizacion = aut.getFechaAutorizacion();
                                numeAutorizacion = aut.getNumeroAutorizacion();
                                estado = aut.getEstado();
                                save = true;
                                envio2 = true;
                                buscar_aut++;
                            } 
                            
                            JAXBContext jc = JAXBContext.newInstance(A.class);
                            Marshaller marshaller = jc.createMarshaller();
                            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                            //-------------------------------------------------------------
                            if (ret_aut.exists()) {
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();
                            } else {
                                ret_aut.mkdirs();
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();
                            }
                            /////////////////////////////////////////////////////////////////////
                            if (estado.equals("AUTORIZADO")) {
                                try {
                                    File file_server = new File("/home/asfact/public_html/api/xml/");
                                    if (file_server.exists()) {
                                        Writer writer = new FileWriter("/home/asfact/public_html/api/xml" + "/" + rucEmpresa + "/" + nomclave + ".xml");
                                        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                        marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                        writer.close();
                                    } else {
                                        file_server.mkdirs();
                                        Writer writer = new FileWriter("/home/asfact/public_html/api/xml" + "/" + rucEmpresa + "/" + nomclave + ".xml");
                                        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                        marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                        writer.close();
                                    }
                                } catch (Exception ex) {
                                    System.out.println("El directorio no existe: \n" + ex.getMessage());
                                }// catch ex  
                            }
                            /////////////////////////////////////////////////////////////////////// 
                        } else {
                            System.out.println("El documento no ha sido autorizado revisar el XML en la ruta: " + ret_no_aut);
                            if (!bandera && contador == tam) {
                                aut.setComprobante("<![CDATA[" + autorizacion.getComprobante() + "]]>");
                                aut.setEstado(autorizacion.getEstado());
                                Calendar calendar = autorizacion.getFechaAutorizacion().toGregorianCalendar();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");//yyyy/MM/dd
                                String fechaAutorizacion = sdf.format(calendar.getTime());
                                //--
                                if (ambiente.equals("1")) {
                                    nomAmbiente = "PRUEBAS";
                                }

                                if (ambiente.equals("2")) {
                                    nomAmbiente = "PRODUCCION";
                                }

                                aut.setFechaAutorizacion(fechaAutorizacion);
                                aut.setMensajes(autorizacion.getMensajes());
                                aut.setAmbiente(nomAmbiente);
                                aut.setNumeroAutorizacion(autorizacion.getNumeroAutorizacion());
                                //
                                if (aut.getEstado().equals(estadoAutorizacion)) {
                                    fecAutorizacion = aut.getFechaAutorizacion();
                                    numeAutorizacion = aut.getNumeroAutorizacion();
                                    estado = aut.getEstado();
                                    save = true;
                                    envio2 = true;
                                    buscar_aut++;
                                } 
                                
                                JAXBContext jc = JAXBContext.newInstance(A.class);
                                Marshaller marshaller = jc.createMarshaller();
                                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                                //-------------------------------------------------------------
                                if (ret_no_aut.exists()) {
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                } else {
                                    ret_no_aut.mkdirs();
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/GUIAREMISION/guiaRemisionNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                }
                            }//fin de bandera
                        }// no autorizado
                    } catch (Exception ex) {
                        System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no Generado..!");  
                    }
                }//for
            } catch (Exception ex) {
                System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no Generado..!");
            }
        }// fin de recupera_aut 
    
        //---------------------------hacemos el insert para el fccompelec
        try {

            /////////////////////////////////////////////////////////////////////// 
            save = true;
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////      
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
            if (save) {
                try {
                    pruebanet bd = new pruebanet();
                    con = bd.getConexion();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
                try {
                    cst = con.prepareCall("{call buscar_insertar_notaCredito_xml(?,?,?,?,?,?,?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, "GR");
                    cst.setDouble(4, cod);
                    cst.setDouble(5, num);
                    cst.setString(6, nomclave);
                    cst.setString(7, nomclave);
                    cst.setString(8, fecAutorizacion);
                    cst.setString(9, estado);
                    cst.setLong(10, 2);
                    cst.setLong(11,envioSRI);
                    rs = cst.executeQuery();
                    if (rs == null) {
                    }
                    cst.close();
                    con.close();
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {
            System.exit(0);
        }
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
        System.exit(0);
            


    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
