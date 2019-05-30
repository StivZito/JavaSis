/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import com.gruposantorun.comprobantes.modelo.modelo.CampoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import com.gruposantorun.comprobantes.modelo.modelo.retencion.Impuesto;
import com.gruposantorun.comprobantes.modelo.modelo.retencion.InfoCompRetencion;
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
public class eRetencion {
    
    ////////////////////// DECLARACIONM DE VARIABLES /////////////////////////
    boolean pasar = false;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    int contador = -1;
    String tipo = "";
    LeerSaiFact fac = new LeerSaiFact();
    VerificarExistenciaAutorizado ver = new VerificarExistenciaAutorizado();
    VerificarDisponibilidadOFFPRU exi_pru  = new VerificarDisponibilidadOFFPRU();
    VerificarDisponibilidadOFFPRO exi_pro  = new VerificarDisponibilidadOFFPRO();
    private static File archivo = null;
    String hfactura = "";
    String auxcod = "";
    String auxnum = "";
    String codemp = "";
    String codsuc = "";
    String tip = "";
    String tipo_pasar = "";
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
    String nomAmbiente = "";
    String  reenviaSRI   = "0";
    
    int     envioSRI     = 0;
    boolean recupera_aut = false;
    boolean enviaDoc     = false;
    String  rucEmpresa  = "";

    public eRetencion() {
        primerPaso();
        try{
        procesarRetencion();}
        catch (SQLException e){System.out.println(e.getMessage());}
    }
    
    private void primerPaso() {
        String tipa = "";
        String emp = "";
        String suc = "";
        String num = "";
        String fact = "";
        String existe = "";
        String sql_tipo = "";
        String envio_compe = "";        
        int pos = 100;
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

            sql_tipo = "SELECT TIPO_FAC FROM BPFACTUR_REOC "
                    + "  WHERE CODEMP =" + emp
                    + "    AND CODSUC =" + suc
                    + "    AND NUMDOC =" + num
                    + "    AND NUMERO_FAC=" + fact;
            PreparedStatement pstm1 = con.prepareStatement(sql_tipo);
            ResultSet rs1 = pstm1.executeQuery();
            while (rs1.next()) {
                tipo_pasar = rs1.getString("TIPO_FAC");
            }
            
            System.out.println("tipo doc " + tipo_pasar);
            
            //mando a recuperar el envio sri de la tabla fccompelec
            envio_compe = "SELECT case when  isnull(envioSRI,'') = '' then '0' else envioSRI end AS envio FROM FCCOMPELEC WITH(NOLOCK) "
                    + " WHERE  TIPO=" + "'RE'"
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

        } catch (Exception e) {
            System.out.println("Error No se puede conectar : " + e.getMessage());
            System.exit(0);
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
        } else {
            exfccompelec = false;
        }
    } //fin primer paso 
    
    private void procesarRetencion() throws SQLException {
        String tipa = "";
        String emp = "";
        String suc = "";
        String numgiro = "";
        String fact = "";
        String sql_tipo = "";
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        numgiro = fac.getNumero();
        fact = fac.getFactura();
        rucEmpresa = fac.getRuc();
        
        ParametrosExternos p = new ParametrosExternos();
        // la conexion 
        try {
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        cod = 0.0;
        num = 0.0;
        //-------------------------------
        File dir = new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/");
        //----------------------- generar
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
        auxcod = numgiro;
        auxnum = fact;
        if (auxcod.equals("")) {
            cod = 0.0;
        }
        if (auxnum.equals("")) {
            num = 0.0;
        }
        
        try
        {
        sql_tipo = "SELECT TIPO_FAC FROM BPFACTUR_REOC "
                    + "  WHERE CODEMP =" + emp
                    + "    AND CODSUC =" + suc
                    + "    AND NUMDOC =" + num
                    + "    AND NUMERO_FAC=" + fact;
            PreparedStatement pstm1 = con.prepareStatement(sql_tipo);
            ResultSet rs1 = pstm1.executeQuery();
            while (rs1.next()) {
                tipo_pasar = rs1.getString("TIPO_FAC");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        
        System.out.println("pasar2 " + tipo_pasar);
            
        tip = tipo_pasar;
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

       ////////////// GENERACION DE LA RETENCION /////////////////////////////////////////////////////
        try {
            cst = con.prepareCall("{call buscar_comp_retencion_xml(?,?,?,?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            cst.setString(3, tip);
            cst.setDouble(4, cod);
            cst.setDouble(5, num);
            rs = cst.executeQuery();
            if (rs.next()) {
                com.gruposantorun.comprobantes.modelo.modelo.retencion.ComprobanteRetencion ret = new com.gruposantorun.comprobantes.modelo.modelo.retencion.ComprobanteRetencion();
                ret.setVersion("1.0.0");
                ret.setId("comprobante");
                //JOptionPane.showMessageDialog(this, "Cliente Econtrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                //--------------------------------------------------------------------------------------
                clave_aux = clv.generarClaveAcceso(rs.getString("clave").toString().trim());
                if (clave_aux == null) {
                    System.out.println("CLAVE ACCESO NO GENERADA, EL ARCHIVO NO SE PUEDE VALIDARSE");
                    nomclave = "Factura_error" + rs.getString("secuencial").trim().toString();
                    pasar = false;
                    System.exit(0);
                } else {
                    nomclave = clave_aux;
                    pasar = true;
                }//if
                InfoTributaria it = new InfoTributaria();
                it.setAmbiente(rs.getString("ambiente").trim().toString());
                it.setTipoEmision(rs.getString("tipoEmision").trim().toString());
                it.setRazonSocial(rs.getString("razonsocial").trim().toString());
                it.setNombreComercial(rs.getString("nomComercial").trim().toString());
                it.setRuc(rs.getString("numeroruc").trim().toString());
                it.setClaveAcceso(clave_aux);
                it.setCodDoc("07");
                it.setEstab(rs.getString("estab").trim().toString());
                it.setPtoEmi(rs.getString("ptoEmi"));
                it.setSecuencial(rs.getString("secuencial").trim().toString());
                it.setDirMatriz(rs.getString("direccion").trim().toString());
                ret.setInfoTributaria(it);

                InfoCompRetencion ifr = new InfoCompRetencion();
                auxfecha = rs.getString("fechaEmision").trim().toString();
                auxfecha = auxfecha.substring(0, 10);
                fec = auxfecha.split("-");
                nuevo = fec[2] + "/" + fec[1] + "/" + fec[0];
                ifr.setFechaEmision(nuevo);
                ifr.setDirEstablecimiento(rs.getString("dirEstablecimiento").trim().toString());
                ifr.setContribuyenteEspecial(rs.getString("contribuyenteEspecial").trim().toString());
                ifr.setObligadoContabilidad(rs.getString("obligadoContabilidad").trim().toString());
                ifr.setTipoIdentificacionSujetoRetenido(rs.getString("tipoidentificacionsujetoretenido").trim().toString());
                ifr.setRazonSocialSujetoRetenido(rs.getString("razonSocialComprador").trim().toString());
                ifr.setIdentificacionSujetoRetenido(rs.getString("identificacionsujetoretenido").trim().toString());
                ifr.setPeriodoFiscal(rs.getString("ejerciciofiscal").trim().toString());
                ret.setInfoCompRetencion(ifr);
                //-----------------------------------------------------DETALLE DE LA RETENCION
                List<Impuesto> detRet = new ArrayList();
                try {
                    cst = con.prepareCall("{call buscar_detalle_retencion_xml(?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, cod);
                    cst.setDouble(5, num);
                    rs = cst.executeQuery();
                    while (rs.next()) {
                        Object[] fila1 = new Object[8];
                        for (int i = 0; i < 8; i++) {
                            fila1[i] = rs.getObject(i + 1);
                        }// for
                        //md.addRow(fila1);
                        Impuesto fd1 = new Impuesto();
                        fd1.setCodigo(fila1[0].toString().trim());
                        fd1.setCodigoRetencion(fila1[1].toString().trim());
                        fd1.setBaseImponible(new BigDecimal(fila1[2].toString().trim()));
                        fd1.setPorcentajeRetener(new BigDecimal(fila1[3].toString().trim()));
                        fd1.setValorRetenido(new BigDecimal(fila1[4].toString().trim()));
                        fd1.setCodDocSustento(fila1[5].toString().trim());
                        fd1.setNumDocSustento(fila1[6].toString().trim());
                        fd1.setFechaEmisionDocSustento(fila1[7].toString().trim());
                        detRet.add(fd1);
                    }// while

                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    System.exit(0);
                }// try 2
                cst.close();
                con.close();
                ret.setImpuesto(detRet);
                //-----------------------------------------------------------------------
                InfoAdicional infad = new InfoAdicional();
                List<CampoAdicional> lstifa = new ArrayList();
                CampoAdicional cmpad = new CampoAdicional();
                cmpad.setNombre("Proveedor");
                cmpad.setValue("Grupo Santorun");
                lstifa.add(cmpad);
                infad.setCampoAdicional(lstifa);
                ret.setInfoAdicional(infad);
                 
                    //-----------------------------------------------------------------------------------------------------------
                    if (dir.exists()) {
                        XMLUtil.marshall(ret,"/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                        tranferencia = true;
                    } else {
                        dir.mkdirs();
                        XMLUtil.marshall(ret,"/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                        tranferencia = true;
                    }
                    
            } else {
                System.out.println("Cliente No Econtrado");
                tranferencia = false;
                System.exit(0);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(0);
        }
            
            /////------------------------------------------------ VALIDA EL XML GENERADO POR MEDIO DEL XSD
            if (tranferencia) {
                    //File dirXSD = new File("xsd/");
                    File dirXSD = new File("/home/asfact/public_html/api/jar/xsd/");
                    if (dirXSD.exists()) {
                        //String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("xsd/comprobanteRetencion.xsd"));
                        String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("/home/asfact/public_html/api/jar/xsd/comprobanteRetencion.xsd"));
                        if (mensajes == null) {
                            tranferencia = true;
                        } else {
                            System.out.println("El archivo no se pudo Validar: " + mensajes);
                            tranferencia = false;
                            System.exit(0);
                        }//if
                    } else {
                        System.out.println("La carpeta XSD para validar los comprobantes no exixte ");
                        tranferencia = false;
                        System.exit(0);
                    }
            }else{
                System.out.println("Retencion No Generada");
                System.exit(0);
            }
            
            ////---------------------------------------------------------FIRMA ELECTRONICA
            if (tranferencia) {
                    File dirff = new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionFirmada/" + rucEmpresa + "/");
                    String ruta2=new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
                    if (dirff.exists()) {
                        Signer s = new Signer(ruta2, "/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                        String ruta=new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
                        Signer s = new Signer(ruta, "/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                System.out.println("Retencion No Paso la validacion XSD");
                envio1 = false;
                System.exit(0);
            }
        
        System.out.println("envio1 " + envio1);
       
        //-----------------empezamos el envio de documentos para los distintos web-services
        if (envio1) {            
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //------si el documento tiene autorizacion no entra al proceso de envio        
                if (disponibilidad) {
                    try {

                        if (ambiente.equals("1")) {

                            urlwebService = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                            CertificadosSSL.instalarCertificados();
                            EnvioComprobantesWSOFFPRU wsa = new EnvioComprobantesWSOFFPRU(urlwebService.toString().trim());
                            com.gruposantorun.comprobantes.ws.pruebas.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
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
                                            List<com.gruposantorun.comprobantes.ws.pruebas.off.Comprobante> lst = respuesta.getComprobantes().getComprobante();
                                            for (com.gruposantorun.comprobantes.ws.pruebas.off.Comprobante comprobante : lst) {
                                                System.out.println("entro 2");
                                                List<com.gruposantorun.comprobantes.ws.pruebas.off.Mensaje> lstMensaje = comprobante.getMensajes().getMensaje();
                                                for (com.gruposantorun.comprobantes.ws.pruebas.off.Mensaje mensaje : lstMensaje) {
                                                    System.out.println("estado: " + respuesta.getEstado());
                                                            System.out.println("identificador: " + mensaje.getIdentificador());
                                                            System.out.println("tipo: " + mensaje.getTipo()) ;
                                                            System.out.println("infoad: " + mensaje.getInformacionAdicional()) ;
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
                                                            + "\n Generación de contingencia...");
                                                            envioSRI = 0;
                                                        } // fin de la clave en procesamiento
                                                    } //IF
                                                }//for
                                            }//for
                                        }// fin de devuelta
                                    }
                            }// fin del recibida
                        }

                        if (ambiente.equals("2")) {

                                urlwebService = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                                CertificadosSSL.instalarCertificados();
                                EnvioComprobantesWSOFFPRO wsa = new EnvioComprobantesWSOFFPRO(urlwebService.toString().trim());
                                com.gruposantorun.comprobantes.ws.produccion.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
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
                                            System.out.println("El documento no ha sido recibida por el SRI");
                                            List<com.gruposantorun.comprobantes.ws.produccion.off.Comprobante> lst = respuesta.getComprobantes().getComprobante();
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
                                                            + "\n Generación de contingencia...");
                                                            envioSRI = 0;
                                                        } // fin de la clave en procesamiento
                                                    } //IF
                                                }//for
                                            }//for
                                    }// fin de devuelta
                                }
                            }// fin del recibida
                        }

                    } catch (MalformedURLException ex) {
                        System.out.println("ENVIO(MalformedURLException) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no Generado... ");
                        Logger.getLogger(FEUPW_NCRE.class.getName()).log(Level.SEVERE, null, ex);
                        contingencia = true;
                        obtenerAutorizado = false;
                } catch (WebServiceException ex) {
                    System.out.println("ENVIO(MalformedURLException) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no generado... ");
                    Logger.getLogger(FEUPW_NCRE.class.getName()).log(Level.SEVERE, null, ex);
                    contingencia = true;
                    obtenerAutorizado = false;
                    }// fin try catch  
                }// fin de envio1
                
                
            }
            

        }// existeAut
        System.out.println("envioSRI: " +envioSRI);
        if (reenviaSRI.equals("1"))   {
            File ret_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionAutorizada/" + rucEmpresa + "/");
            File ret_no_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionNoAutorizada/" + rucEmpresa + "/");
            if (ambiente.equals("1")) {
                urlwebService1 = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
            }
            if (ambiente.equals("2")) {
                urlwebService1 = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
            }
            try {
                CertificadosSSL.instalarCertificados();
                List<String> lst_at = new ArrayList();
                AutorizacionComprobantesWS ws = new AutorizacionComprobantesWS(urlwebService1.toString().trim());
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

                            if (ret_aut.exists()) {
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();
                            } else {
                                ret_aut.mkdirs();
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                                }// catch ex  
                            }

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
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                } else {
                                    ret_no_aut.mkdirs();
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/RETENCION/RetencionNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                }
                            }// no autorizado
                        }// bandera
                        /////////////////////////////////////////////////////////////////////// 
                    } catch (Exception ex) {
                        System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no Generado..!");
                        ex.printStackTrace();
                        System.exit(0);
                    }
                }
            } catch (Exception ex) {
                System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no Generado..!");
            }
        }// fin obtener
        
        save = true;
        if (save) {
            try {
                pruebanet bd = new pruebanet();
                con = bd.getConexion();
            } catch (Exception e) {
                System.out.println("Error No se puede conectar : " + e.getMessage());
                System.out.println(e.getMessage());
                System.exit(0);
            }
            try {
                cst = con.prepareCall("{call buscar_insertar_notaCredito_xml(?,?,?,?,?,?,?,?,?,?,?)}");
                cst.setString(1, codemp);
                cst.setString(2, codsuc);
                cst.setString(3, "RE");
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
            }
        }

    System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
