/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import com.gruposantorun.comprobantes.modelo.modelo.*;
import com.gruposantorun.comprobantes.modelo.modelo.factura.FacturaDetalle;
import com.gruposantorun.comprobantes.modelo.modelo.factura.InfoFactura;
import com.gruposantorun.comprobantes.modelo.modelo.factura.TotalImpuesto;
import com.gruposantorun.comprobantes.modelo.modelo.factura.Pago;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
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
public class eFactura {
    LeerSaiFact fac = new LeerSaiFact();
    VerificarExistenciaAutorizado ver      = new VerificarExistenciaAutorizado();
    VerificarDisponibilidadOFFPRU exi_pru  = new VerificarDisponibilidadOFFPRU();
    VerificarDisponibilidadOFFPRO exi_pro  = new VerificarDisponibilidadOFFPRO();
    ClaveAcceso clv = new ClaveAcceso();
    DefaultTableModel md1 = new DefaultTableModel();
    
    boolean pasar = false;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    int contador = -1;
    String tipo = "";
    
    private static final File archivo = null;
    String hfactura = "";
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
    String estado_no = "";
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
    String ordenComp = "";
    String correo = "";
    String aux_comprobante = "";
    String[] auxserv = {};
    int buscar_aut = 0;
    boolean bandera = false;
    boolean band = false;
    boolean exfccompelec = false;
    String nomAmbiente = "";
    String  reenviaSRI   = "0";
    String  V_ambiente = "";
    String  dir_clie="";
    
    int     envioSRI     = 0;
    boolean recupera_aut = false;
    boolean enviaDoc     = false; 
    
    String  rucEmpresa   = "";
    
    

    
    /////////////////////////////////////////////////////////////////////////////
    public eFactura() {
        primerPaso();
        procesarFactura();
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
        hfactura = fact;
        
        System.out.println("ruc empresa" + rucEmpresa);


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
            System.out.println("si existe en fccompelec " + contador);

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
    }
    
    private void procesarFactura() {
        String   tipa = "";
        String   emp = "";
        String   suc = "";
        String   numfac = "";
        tipa   = fac.getTipo();
        emp    = fac.getEmpresa();
        suc    = fac.getSucursal();
        numfac = fac.getNumero();
        rucEmpresa = fac.getRuc();
        
        cod = 0.0;
        num = 0.0;
        File dir = new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/");
        String clave_aux = "";
        String auxfecha = "";
        String nuevo = "";
        String[] fec;
        boolean tranferencia = false;
        boolean envio1 = false;
        boolean envio2 = false;
        // la conexion 
        ParametrosExternos p = new ParametrosExternos();

        try {
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
        } catch (Exception e) {
            System.out.println("Error no se puede conectar " + e.getMessage());
            tranferencia = false;
            contingencia = true;
            obtenerAutorizado = false;
        }// fin del try
        
        auxcod = numfac  ;
        ordenComp = "";
        if (auxcod.equals("")) {
            cod = 0.0;
        }
        if (auxnum.equals("")) {
            num = 0.0;
        }
        
        tip = tipa;
        codemp = emp;
        codsuc = suc;
        
        if (auxcod.isEmpty()) {
            tranferencia = false;
            contingencia = true;
            obtenerAutorizado = false;
        }
        if ( hfactura.isEmpty() ) {
            tranferencia = false;
            contingencia = true;
            obtenerAutorizado = false;
        }

        cod = Double.parseDouble(auxcod);
        num = Double.parseDouble(hfactura);
        codemp = codemp.trim();
        codsuc = codsuc.trim();
        tip = tip.trim();
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        p.Parametros(codemp, codsuc);
        Servidor_emp = p.getRutaXmlAutorizado();
        ambiente = p.getTipoAmbiente();
        certificado = p.getRutaCertificado();
        clavecertificado = p.getClaveAcceso();
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            cst = con.prepareCall("{call buscar_comp_facturaElectronica_xml(?,?,?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            cst.setString(3, tip);
            cst.setDouble(4, cod);
            rs = cst.executeQuery();
            if (rs.next()) {
                com.gruposantorun.comprobantes.modelo.modelo.factura.Factura f = new com.gruposantorun.comprobantes.modelo.modelo.factura.Factura();
                f.setVersion("1.1.0");
                f.setId("comprobante");
                //--------------------------------------------------------------------------------------
                clave_aux = clv.generarClaveAcceso(rs.getString("clave").trim());
                if (clave_aux == null) {
                    System.out.println("CLAVE ACCESO NO GENERADA, EL ARCHIVO NO SE PUEDE VALIDARSE");
                    nomclave = "Factura_error" + rs.getString("secuencial").trim();
                    pasar = false;
                    System.exit(0);

                } else {
                    nomclave = clave_aux;
                    pasar = true;
                }//if
                InfoTributaria it = new InfoTributaria();
                it.setAmbiente(rs.getString("ambiente").trim());
                it.setTipoEmision(rs.getString("tipoEmision").trim());
                it.setRazonSocial(rs.getString("razonsocial").trim());
                it.setNombreComercial(rs.getString("nomComercial").trim());
                it.setRuc(rs.getString("numeroruc").trim());
                it.setClaveAcceso(clave_aux);
                it.setCodDoc("01");
                it.setEstab(rs.getString("estab").trim());
                it.setPtoEmi(rs.getString("ptoEmi"));
                it.setSecuencial(rs.getString("secuencial").trim());
                it.setDirMatriz(rs.getString("direccion").trim());
                f.setInfoTributaria(it);

                InfoFactura iff = new InfoFactura();
                auxfecha = rs.getString("fechaEmision").trim();
                auxfecha = auxfecha.substring(0, 10);
                fec = auxfecha.split("-");
                nuevo = fec[2] + "/" + fec[1] + "/" + fec[0];
                iff.setFechaEmision(nuevo);
                iff.setDirEstablecimiento(rs.getString("dirEstablecimiento").trim());
                iff.setContribuyenteEspecial(rs.getString("contribuyenteEspecial").trim());
                iff.setObligadoContabilidad(rs.getString("obligadoContabilidad").trim());
                iff.setTipoIdentificacionComprador(rs.getString("TIPIDCLIENTE").trim());
                iff.setRazonSocialComprador(rs.getString("razonSocialComprador").trim());
                iff.setIdentificacionComprador(rs.getString("IDCLIENTE").trim());
                iff.setTotalSinImpuestos(new BigDecimal(rs.getString("totalSinImpuestos").trim()));
                iff.setTotalDescuento(new BigDecimal(rs.getString("totalDescuento").trim()));
                iff.setPropina(new BigDecimal("0.00"));
                iff.setImporteTotal(new BigDecimal(rs.getString("importeTotal").trim()));
                iff.setMoneda("Dolar");
                ordenComp = rs.getString("NOM_P").trim(); // nombre del cliente
                correo = rs.getString("CORREO_P").trim(); // correo del cliente
                dir_clie = rs.getString("DIR_P").trim(); // DIR_CLIENTE
                
                List<TotalImpuesto> lst = new ArrayList<>();
                try {
                    cst = con.prepareCall("{call buscar_comp_impuestos_factura_xml(?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, cod);
                    rs = cst.executeQuery();
                    while (rs.next()) {
                        Object[] fila1 = new Object[4];
                        for (int i = 0; i < 4; i++) {
                            fila1[i] = rs.getObject(i + 1);
                        }// for
                        TotalImpuesto t1 = new TotalImpuesto();
                        t1.setCodigo(fila1[0].toString().trim());
                        t1.setCodigoPorcentaje(fila1[1].toString().trim());
                        t1.setBaseImponible(new BigDecimal(fila1[2].toString().trim()));
                        t1.setValor(new BigDecimal(fila1[3].toString().trim()));
                        lst.add(t1);
                    }
                    iff.setTotalImpuesto(lst);
                    f.setInfoFactura(iff);
                } catch (Exception e) {
                    System.out.println("ERROR SP IMPUESTO " + e.getMessage());
                }
                
                 //--------------------------------datos de la forma de pago
                List<Pago> pag = new ArrayList<>();
                try {
                    cst = con.prepareCall("{call buscar_comp_formpago_factura_xml(?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, cod);
                    rs = cst.executeQuery();
                    while (rs.next()) {
                        Object[] fila2 = new Object[4];
                        for (int i = 0; i < 4; i++) {
                            fila2[i] = rs.getObject(i + 1);
                        }
                        Pago t2 = new Pago();
                        t2.setFormaPago((fila2[0].toString().trim()));
                        t2.setTotal(new BigDecimal(fila2[1].toString().trim()));
                        t2.setPlazo(new BigDecimal(fila2[2].toString().trim()));
                        t2.setUnidadTiempo(fila2[3].toString().trim());
                        pag.add(t2);                      
                    }
                    iff.setPago(pag);
                    f.setInfoFactura(iff);
                } catch (Exception e) {
                    System.out.println("ERROR SP FORMA DE PAGO " + e.getMessage());
                    tranferencia = false;
                    System.exit(0);
                }
                
                //-----------------------------------------------------------------------DETALLE DE LA FACTURA
                List<FacturaDetalle> detalle = new ArrayList();
                try {
                    cst = con.prepareCall("{call buscar_detalle_facturaElectronica_xml(?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, cod);
                    rs = cst.executeQuery();
                    while (rs.next()) {
                        Object[] fila1 = new Object[13];
                        for (int i = 0; i < 13; i++) {
                            fila1[i] = rs.getObject(i + 1);
                        }// for
                        //md.addRow(fila1);
                        FacturaDetalle fd1 = new FacturaDetalle();
                        fd1.setCodigoPrincipal(fila1[0].toString().trim());
                        fd1.setCodigoAuxiliar(fila1[1].toString().trim());
                        fd1.setDescripcion(fila1[2].toString().trim());
                        fd1.setCantidad(new BigDecimal(fila1[3].toString().trim()));
                        fd1.setPrecioUnitario(new BigDecimal(fila1[4].toString().trim()));
                        fd1.setDescuento(new BigDecimal(fila1[5].toString().trim()));
                        fd1.setPrecioTotalSinImpuesto(new BigDecimal(fila1[6].toString().trim()));
                        List<DetAdicional> lsta = new ArrayList();
                        DetAdicional dea = new DetAdicional();
                        dea.setNombre("Comentario");
                        dea.setValor(fila1[12].toString().trim());
                        lsta.add(dea);
                        fd1.setDetAdicional(lsta);
                        List<Impuesto> lstimp = new ArrayList();
                        Impuesto imp = new Impuesto();
                        imp.setCodigo(fila1[7].toString().trim());
                        imp.setCodigoPorcentaje(fila1[8].toString().trim());
                        imp.setTarifa(new BigDecimal(fila1[9].toString().trim()));
                        imp.setBaseImponible(new BigDecimal(fila1[10].toString().trim()));
                        imp.setValor(new BigDecimal(fila1[11].toString().trim()));
                        lstimp.add(imp);
                        fd1.setImpuesto(lstimp);
                        detalle.add(fd1);
                    }// while
                } catch (Exception e) {
                    System.out.println("ERROR det: " + e.getMessage());
                    System.exit(0);
                }// try 2
                
                //-----------------------------------------------------------------------
                InfoAdicional infad = new InfoAdicional();
                List<CampoAdicional> lstifa = new ArrayList();
                CampoAdicional cmpad = new CampoAdicional();
                cmpad.setNombre("Cliente");
                cmpad.setValue(ordenComp);
                
                CampoAdicional cmpad01 = new CampoAdicional();
                cmpad01.setNombre("Direccion");
                cmpad01.setValue(dir_clie);                
                
                CampoAdicional cmpad0 = new CampoAdicional();
                cmpad0.setNombre("Email");
                cmpad0.setValue(correo);

                lstifa.add(cmpad);
                lstifa.add(cmpad01);
                lstifa.add(cmpad0);

                infad.setCampoAdicional(lstifa);
                f.setInfoAdicional(infad);
                f.setDetalle(detalle);
                
                //--------------------------------------------------------------------------------------
                if (dir.exists()) {
                    XMLUtil.marshall(f, "/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                } else {
                    dir.mkdirs();
                    XMLUtil.marshall(f, "/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
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
            File dirXSD = new File("xsd/");
            //File dirXSD = new File("/home/asfact/public_html/api/jar/xsd/");
            if (dirXSD.exists()) {
                String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("xsd/factura.xsd"));
                //String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("/home/asfact/public_html/api/jar/xsd/factura.xsd"));
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
            File dirff = new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaFirmada/"  + rucEmpresa + "/" );
            String ruta2=new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
            if (dirff.exists()) {
                Signer s = new Signer(ruta2, "/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                String ruta=new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
                Signer s = new Signer(ruta, "/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
            System.out.println("Factura No Paso la validacion XSD");
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
                            com.gruposantorun.comprobantes.ws.pruebas.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
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
                            com.gruposantorun.comprobantes.ws.produccion.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));                       
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
            File ret_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaAutorizada/" + rucEmpresa + "/" );
            File ret_no_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaNoAutorizada/"+ rucEmpresa + "/" );
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
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();
                            } else {
                                ret_aut.mkdirs();
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                } else {
                                    ret_no_aut.mkdirs();
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/FACTURA/FacturaNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                    System.out.println("Error No se puede conectar : " + e.getMessage());
                    System.exit(0);
                }
                try {
                    cst = con.prepareCall("{call buscar_insertar_notaCredito_xml(?,?,?,?,?,?,?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, "FA");
                    cst.setDouble(4, num);
                    cst.setDouble(5, cod);
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
                } catch (SQLException | HeadlessException e) {
                    System.out.println(e.getMessage());
                }
            }

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
