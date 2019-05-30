/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import com.gruposantorun.comprobantes.modelo.modelo.CampoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.Impuesto;
import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import com.gruposantorun.comprobantes.modelo.modelo.notadebito.InfoNotaDebito;
import com.gruposantorun.comprobantes.modelo.modelo.notadebito.Motivo;
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
import java.awt.HeadlessException;
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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author progrmador
 */
public class eNotaDebito {
    
    boolean pasar = false;
    int contador = -1;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    String tipo = "";
    LeerSaiFact fac = new LeerSaiFact();
    VerificarExistenciaAutorizado ver      = new VerificarExistenciaAutorizado();
    VerificarDisponibilidadOFFPRU exi_pru  = new VerificarDisponibilidadOFFPRU();
    VerificarDisponibilidadOFFPRO exi_pro  = new VerificarDisponibilidadOFFPRO();
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
    ClaveAcceso clv = new ClaveAcceso();
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
    String  reenviaSRI   = "0";
    String nomAmbiente = "";
    String  V_ambiente = "";
    int     envioSRI     = 0;
    boolean recupera_aut = false;
    boolean enviaDoc     = false;
    String  rucEmpresa   = "";
    
    public eNotaDebito() {
        primerPaso();
        procesarNotaDebito();
    }
    
    private void primerPaso() {
        String tipa = "";
        String emp = "";
        String suc = "";
        String num = "";
        String fact = "";
        String existe = "";
        String envio_compe = "";
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        num = fac.getNumero();
        fact = fac.getFactura();
        rucEmpresa = fac.getRuc();

        existe = "SELECT COUNT(tipo) FROM FCCOMPELEC WITH(NOLOCK) "
                + " WHERE  TIPO=" + tipa
                + " AND    CODEMP=" + emp
                + " AND    CODSUC=" + suc
                + " AND    NUMERO_TRANS=" + num
                + " AND    NUMERO_FACT=" + fact;
        try {
            String cont = "";
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
            PreparedStatement pstm = con.prepareStatement(existe);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                cont = rs.getString("CANTIDAD");
            }

            contador = Integer.parseInt(cont);
            
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
            System.out.println("Error en FACT no se pudo conectar " + e.getMessage());
            contingencia = true;
            obtenerAutorizado = false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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

    }//  fin de primer paso
    
    private void procesarNotaDebito() {
        ///////////////////////////////////////////////////////////////////////////  
        File dir = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/");
        String tipa = "";
        String emp = "";
        String suc = "";
        String numdeb = "";
        String fact = "";
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        numdeb = fac.getNumero();
        fact = fac.getFactura();  
        rucEmpresa = fac.getRuc();
        Comprobante_sin cps = new Comprobante_sin();
        ParametrosExternos p = new ParametrosExternos();
        Exepcion_try conti_pasa = new Exepcion_try();
        
        try {
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
        } catch (Exception e) {
            System.out.println("Error no se puede conectar " + e.getMessage());
            System.exit(0);
        }

        cod = 0.0;
        num = 0.0;        
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
        auxcod = numdeb;
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
        cod = Double.parseDouble(auxcod.toString());
        num = Double.parseDouble(auxnum.toString());
        codemp = codemp.trim().toString();
        codsuc = codsuc.trim().toString();
        tip = tip.trim().toString();
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        p.Parametros(codemp, codsuc);
        Servidor_emp = p.getRutaXmlAutorizado().toString();
        ambiente = p.getTipoAmbiente().toString();
        certificado = p.getRutaCertificado();
        clavecertificado = p.getClaveAcceso();
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            cst = con.prepareCall("{call buscar_comp_notaDebito_xml(?,?,?,?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            cst.setString(3, tip);
            cst.setDouble(4, cod);
            cst.setDouble(5, num);
            rs = cst.executeQuery();
            if (rs.next()) {
                com.gruposantorun.comprobantes.modelo.modelo.notadebito.NotaDebito ntd = new com.gruposantorun.comprobantes.modelo.modelo.notadebito.NotaDebito();
                ntd.setVersion("1.0.0");
                ntd.setId("comprobante");
                //JOptionPane.showMessageDialog(this, "Cliente Econtrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                //--------------------------------------------------------------------------------------
                clave_aux = clv.generarClaveAcceso(rs.getString("clave").toString().trim());
                if (clave_aux == null) {
              //      JOptionPane.showMessageDialog(this, "CLAVE ACCESO NO GENERADA, EL ARCHIVO NO SE PUEDE VALIDARSE", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    nomclave = "Debito_error" + rs.getString("secuencial").trim().toString();
                    pasar = false;
                } else {
                    nomclave = clave_aux;
                    pasar = true;
                }//if
                InfoTributaria it = new InfoTributaria();
                it.setAmbiente(rs.getString("ambiente").trim().toString());
                it.setTipoEmision(rs.getString("tipoEmision").trim().toString());
                it.setRazonSocial(rs.getString("razonsocial").trim().toString());
                it.setNombreComercial(rs.getString("nombreComercial").trim().toString());
                it.setRuc(rs.getString("numeroruc").trim().toString());
                it.setClaveAcceso(clave_aux);
                it.setCodDoc("05");
                it.setEstab(rs.getString("estab").trim().toString());
                it.setPtoEmi(rs.getString("ptoEmi"));
                it.setSecuencial(rs.getString("secuencial").trim().toString());
                it.setDirMatriz(rs.getString("dirMatriz").trim().toString());
                ntd.setInfoTributaria(it);

                InfoNotaDebito ifr = new InfoNotaDebito();
                ifr.setFechaEmision(rs.getString("fechaEmision").trim().toString());
                ifr.setDirEstablecimiento(rs.getString("dirEstablecimiento").trim().toString());
                ifr.setTipoIdentificacionComprador(rs.getString("tipoIdentificacionComprador").trim().toString());
                ifr.setRazonSocialComprador(rs.getString("razonSocialComprador").trim().toString());
                ifr.setIdentificacionComprador(rs.getString("identificacionComprador").trim().toString());
                ifr.setContribuyenteEspecial(rs.getString("contribuyenteEspecial").trim().toString());
                ifr.setObligadoContabilidad(rs.getString("obligadoContabilidad").trim().toString());
                ifr.setCodDocModificado(rs.getString("codDocModificado").trim().toString());
                ifr.setNumDocModificado(rs.getString("numDocModificado").trim().toString());
                ifr.setFechaEmisionDocSustento(rs.getString("fechaEmisionDocSustento").trim().toString());
                ifr.setTotalSinImpuestos(new BigDecimal(rs.getString("totalsinImpuesto").trim().toString()));

                //-----------------------------------------------------DETALLE DE LA NOTA DEBITO
                double total = 0.0;
                List<Impuesto> lstimp = new ArrayList();
                Impuesto imp = new Impuesto();
                imp.setCodigo(rs.getString("codigo").trim().toString());
                imp.setCodigoPorcentaje(rs.getString("codigoPorcentaje").trim().toString());
                imp.setTarifa(new BigDecimal(rs.getString("tarifa").trim().toString()));
                imp.setBaseImponible(new BigDecimal(rs.getString("baseImponible").trim().toString()));
                imp.setValor(new BigDecimal(rs.getString("valor").trim().toString()));
                lstimp.add(imp);
                ifr.setImpuesto(lstimp);
                ifr.setValorTotal(new BigDecimal(rs.getString("valorTotal").trim().toString()));
                ntd.setInfoNotaDebito(ifr);
                //-----------------------------------------------------------------------
                List<Motivo> lstmt = new ArrayList();
                Motivo mt = new Motivo();
                mt.setRazon(rs.getString("mrazon").trim().toString());
                mt.setValor(new BigDecimal(rs.getString("mvalor").trim().toString()));
                lstmt.add(mt);
                ntd.setMotivo(lstmt);
                //-----------------------------------------------------------------------   
                InfoAdicional infad = new InfoAdicional();
                List<CampoAdicional> lstifa = new ArrayList();
                CampoAdicional cmpad1 = new CampoAdicional();
                cmpad1.setNombre("Telefono");
                cmpad1.setValue(rs.getString("atelefono").trim().toString());

                CampoAdicional cmpad2 = new CampoAdicional();
                cmpad2.setNombre("Direccion");
                cmpad2.setValue(rs.getString("adireccion").trim().toString());

                CampoAdicional cmpad3 = new CampoAdicional();
                cmpad3.setNombre("Correo");
                cmpad3.setValue(rs.getString("acorreo").trim().toString());

                CampoAdicional cmpadp = new CampoAdicional();
                cmpadp.setNombre("Proveedor");
                cmpadp.setValue("Grupo Santorun");

                lstifa.add(cmpad1);
                lstifa.add(cmpad2);
                lstifa.add(cmpad3);
                lstifa.add(cmpadp);
                infad.setCampoAdicional(lstifa);
                ntd.setInfoAdicional(infad);

               //--------------------------------------------------------------------------------------
                if (dir.exists()) {
                    XMLUtil.marshall(ntd, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                } else {
                    dir.mkdirs();
                    XMLUtil.marshall(ntd, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                }
            } else {
                System.out.println("Cliente No Econtrado");
                tranferencia = false;
                contingencia = true;
                obtenerAutorizado = false;
            }
            cst.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            tranferencia = false;
            contingencia = true;
            obtenerAutorizado = false;
        } // fin de la generacion del xml
        
        /////------------------------------------------------ VALIDA EL XML GENERADO POR MEDIO DEL XSD
        if (tranferencia) {
            //File dirXSD = new File("xsd/");
            File dirXSD = new File("/home/asfact/public_html/api/jar/xsd/");
            if (dirXSD.exists()) {
                //String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("xsd/notaDebito.xsd"));
                String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("/home/asfact/public_html/api/jar/xsd/notaDebito.xsd"));
                if (mensajes == null) {
                    tranferencia = true;
                } else {
                    System.out.println("El archivo no se pudo Validar: " + mensajes);
                    tranferencia = false;
                    System.exit(0);

                }//if
            } else {
                System.out.println("La carpeta XSD para validar los comprobantes no existe ");
                tranferencia = false;
                System.exit(0);
            }// fin dirxsd

        } else {
            System.out.println("Factura No Generada");
            System.exit(0);
        } // VALIDA EL XML fin de tranferencia
        
        
        //////---------------------------------------------------------FIRMA ELECTRONICA
        if (tranferencia) {
            File dirff = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoFirmada/" + rucEmpresa + "/");
            String ruta2=new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
            if (dirff.exists()) {
                Signer s = new Signer(ruta2, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
                s.parSignGen(certificado, clavecertificado);
                try {
                    s.firmarFactura();
                    envio1 = true;
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    envio1 = false;
                    System.exit(0);
                }//trycatch
            } else {
                dirff.mkdirs();
                String ruta=new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();                
                Signer s = new Signer(ruta, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
                s.parSignGen(certificado, clavecertificado);
                try {
                    s.firmarFactura();
                    envio1 = true;
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    envio1 = false;
                    System.exit(0);
                }//trycatch
            }// if si existe folio
        } else {
            System.out.println("Nota Debito  No Paso la validacion XSD");
            envio1 = false;
            System.exit(0);
        } // LA FIRMA DIGITAL
        
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
                            com.gruposantorun.comprobantes.ws.pruebas.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
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
                                        envioSRI = 0;
                                        System.out.println( "El documento no ha sido recibida por el SRI:");
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
                                                                             + "\n Generación de contingencia...");  
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
                            com.gruposantorun.comprobantes.ws.produccion.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));                       
                            System.out.println("primero: " + respuesta.getEstado());

                            if (respuesta.getEstado().equals("RECIBIDA")) {
                                    System.out.println("El Documento ha sido enviada al SRI: ");
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
                                        System.out.println("El documento no ha sido recibida por el SRI");
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
                                                            + "\n Generación de contingencia..."); 
                                                            envioSRI = 0;
                                                        } // fin de la clave en procesamiento
                                                    } // IF
                                                }//for
                                            }//for
                                        }// fin de devuelta
                                    }
                                }// fin de recibida
                        }

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FEUPW_FACT.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("ENVIO(MalformedURLException) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nNo se envio al SRI... ");

                    } catch (WebServiceException ex) {
                        Logger.getLogger(FEUPW_FACT.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("ENVIO(WebServiceException) \nSin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nNo se envio al Sri... ");
                    }// fin try catch  
                }    
            }
        }// fin envio1 
        System.out.println("envioSRI: " +envioSRI);
        
        //----------------------si el documento esta autorizado manda a recuperar la autorizacion
        if (reenviaSRI.equals("1"))   {
            File ret_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoAutorizada/" + rucEmpresa + "/");
            File ret_no_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoNoAutorizada/" + rucEmpresa + "/");
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
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();
                            } else {
                                ret_aut.mkdirs();
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                                                              
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                } else {
                                    ret_no_aut.mkdirs();
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTADEBITO/notaDebitoNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                }
                            }//fin de bandera
                        }// no autorizado
                    } catch (JAXBException | IOException | HeadlessException ex) {
                        System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\n Envio no realizado..!");
                        
                    }
                }//for
            } catch (Exception ex) {
                System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\n Envio no realizado..!");
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
                }
                    
                try {
                    cst = con.prepareCall("{call buscar_insertar_notaCredito_xml(?,?,?,?,?,?,?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, "DB");
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
                        System.out.println("EL resultado es Nulo");
                    }
                    cst.close();
                    con.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }//SAVE

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
