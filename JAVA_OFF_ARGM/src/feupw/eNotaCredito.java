/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import com.gruposantorun.comprobantes.modelo.modelo.*;
import com.gruposantorun.comprobantes.modelo.modelo.notacredito.*;
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
import java.text.DecimalFormat;
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
public class eNotaCredito {
    
    boolean pasar = false;
    int contador = -1;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    String tipo = "";
    LeerSaiFact fac = new LeerSaiFact();
    VerificarExistenciaAutorizado ver = new VerificarExistenciaAutorizado();
    VerificarDisponibilidadOFFPRU exi_pru  = new VerificarDisponibilidadOFFPRU();
    VerificarDisponibilidadOFFPRO exi_pro  = new VerificarDisponibilidadOFFPRO();
    private static final File archivo = null;
    String auxcod = "";
    String auxnum = "";
    String codemp = "";
    String codsuc = "";
    String tip = "";
    String cod_str = "";
    String num_str = "";
    Double cod = 0.0;
    Double num = 0.0;
    BigDecimal num_bd;
    BigDecimal cod_bd;
    ClaveAcceso clv = new ClaveAcceso();
    String nomclave = "";
    String fecAutorizacion = "";
    String numeAutorizacion = "";
    String estadoAutorizacion = "AUTORIZADO";
    String estado = "";
    String sqltipo = "";
    String tip_aplic = "";
    boolean save = false;
    boolean pasaAut = false;
    boolean procesamiento = false;
    boolean disponibilidad = false;
    boolean contingencia = false;
    boolean existeAutorizado = false;
    boolean obtenerAutorizado = false;
    boolean recibido = false;
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
    
    String  rucEmpresa   = "";
    
    public eNotaCredito() {
        primerPaso();
        procesarNotacredito();
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
            if (cont.isEmpty()) {
                cont = "0";
            }

            contador = Integer.parseInt(cont);
            System.out.println("contador" + cont);
            
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
        } else {
            exfccompelec = false;
        }

    }// fin de primer paso
    
    
    private void procesarNotacredito() {
        
        String tipa = "";
        String emp = "";
        String suc = "";
        String numde = "";
        String fact = "";     
        tipa = fac.getTipo();
        emp = fac.getEmpresa();
        suc = fac.getSucursal();
        numde = fac.getNumero();
        fact = fac.getFactura();
        rucEmpresa = fac.getRuc();
        
        ParametrosExternos p = new ParametrosExternos();
        DecimalFormat df = new DecimalFormat("############");

        sqltipo = "SELECT TIP_APLIC FROM FCDOCFAC WITH(NOLOCK) "
                + " WHERE  CODEMP=" + emp
                + " AND    CODSUC=" + suc
                + " AND    NUM_APLIC = " + fact
                + " AND NUMERO = " + numde;
        try {
            pruebanet bd = new pruebanet();
            con = bd.getConexion();
            PreparedStatement pstm = con.prepareStatement(sqltipo);
            rs = pstm.executeQuery();
            while (rs.next()) {
                tip_aplic = rs.getString("TIP_APLIC");
            }

        } catch (Exception e) {
            System.out.println("Error No se puede conectar : " + e.getMessage());
            System.exit(0);
        } finally {
            if (rs != null) {
                try {
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    System.out.println("conexion abierta");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        cod = 0.0;
        num = 0.0;
        File dir = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/");
        
        ////------------------------------      
        String clave_aux = "";
        boolean tranferencia = false;
        boolean envio1 = false;
        boolean envio2 = false;
        auxcod = fact;
        auxnum = numde;
        if (auxcod.equals("")) {
            cod = 0.0;
        }
        if (auxnum.equals("")) {
            num = 0.0;
        }
        
        tip = tip_aplic;
        codemp = emp;
        codsuc = suc;
        cod_str = auxcod;
        num_str = auxnum;
        cod = Double.parseDouble(auxcod);
        num = Double.parseDouble(auxnum);
        cod_bd = new BigDecimal(auxcod);
        num_bd = new BigDecimal(auxnum);
        cod = Double.parseDouble(df.format(cod));
        num = Double.parseDouble(df.format(num));
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

        try {
            cst = con.prepareCall("{call buscar_comp_notaCredito_xml(?,?,?,?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            cst.setString(3, tip);
            cst.setDouble(4, num);
            cst.setDouble(5, cod);
            rs = cst.executeQuery();
            if (rs.next()) {
                com.gruposantorun.comprobantes.modelo.modelo.notacredito.NotaCredito nc = new com.gruposantorun.comprobantes.modelo.modelo.notacredito.NotaCredito();
                nc.setVersion("1.1.0");
                nc.setId("comprobante");
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
                it.setCodDoc(rs.getString("codDoc"));
                it.setEstab(rs.getString("estab").trim().toString());
                it.setPtoEmi(rs.getString("ptoEmi"));
                it.setSecuencial(rs.getString("secuencial").trim().toString());
                it.setDirMatriz(rs.getString("dirMatriz").trim().toString());
                nc.setInfoTributaria(it);
                InfoNotaCredito iff = new InfoNotaCredito();
                iff.setFechaEmision(rs.getString("fechaEmision").trim().toString());
                iff.setDirEstablecimiento(rs.getString("dirEstablecimiento").trim().toString());
                iff.setTipoIdentificacionComprador(rs.getString("tipoIdentificacionComprador").trim().toString());
                iff.setRazonSocialComprador(rs.getString("razonSocialComprador").trim().toString());
                iff.setIdentificacionComprador(rs.getString("identificacionComprador").trim().toString());
                iff.setContribuyenteEspecial(rs.getString("contribuyenteEspecial").trim().toString());
                iff.setObligadoContabilidad(rs.getString("obligadoContabilidad").trim().toString());
                iff.setRise(rs.getString("rise"));
                iff.setCodDocModificado(rs.getString("codDocModificado").trim().toString());
                iff.setNumDocModificado(rs.getString("numDocModificado").trim().toString());
                iff.setFechaEmisionDocSustento(rs.getString("fechaEmisionDocSustento").trim().toString());
                iff.setTotalSinImpuestos(new BigDecimal(rs.getString("totalSinImpuesto").trim().toString()));
                iff.setValorModificacion(new BigDecimal(rs.getString("valorModificado").trim().toString()));
                iff.setMoneda(rs.getString("moneda"));
                //----------------------------------------------------------------------IMPUESTOS
                TotalConImpuestos timp = new TotalConImpuestos();
                List<TotalImpuesto> lst_imp = new ArrayList();
                try {
                    cst = con.prepareCall("{call buscar_comp_impuestos_notaCredito_xml(?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, num);
                    cst.setDouble(5, cod);
                    rs = cst.executeQuery();

                    while (rs.next()) {
                        Object[] fila1 = new Object[4];
                        for (int i = 0; i < 4; i++) {
                            fila1[i] = rs.getObject(i + 1);
                            //System.out.println(fila1[i]);
                        }// for
                        TotalImpuesto imp_1 = new TotalImpuesto();
                        imp_1.setCodigo(fila1[0].toString().trim());
                        imp_1.setCodigoPorcentaje(fila1[1].toString().trim());
                        imp_1.setBaseImponible(new BigDecimal(fila1[2].toString().trim()));
                        imp_1.setValor(new BigDecimal(fila1[3].toString().trim()));
                        lst_imp.add(imp_1);
                        timp.setTotalImpuesto(lst_imp);
                    }
                    iff.setTotalConImpuestos(timp);
                    iff.setMotivo("DEVOLUCIÓN");
                    nc.setInfoNotaCredito(iff);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //-----------------------------------------------------------------------DETALLE DE LA FACTURA
                Detalles dts = new Detalles();
                List<Detalle> detalle = new ArrayList();
                try {
                    cst = con.prepareCall("{call buscar_detalle_notaCredito_xml(?,?,?,?,?)}");
                    cst.setString(1, codemp);
                    cst.setString(2, codsuc);
                    cst.setString(3, tip);
                    cst.setDouble(4, num);
                    cst.setDouble(5, cod);
                    rs = cst.executeQuery();
                    while (rs.next()) {
                        Object[] fila1 = new Object[13];
                        for (int i = 0; i < 13; i++) {
                            fila1[i] = rs.getObject(i + 1);
                            System.out.println(fila1[i]);
                        }// for
                        //md.addRow(fila1);
                        Detalle fd1 = new Detalle();

                        fd1.setCodigoInterno(fila1[0].toString().trim());
                        fd1.setCodigoAdicional(fila1[1].toString().trim());
                        fd1.setDescripcion(fila1[2].toString().trim());
                        fd1.setCantidad(new BigDecimal(fila1[3].toString().trim()));
                        fd1.setPrecioUnitario(new BigDecimal(fila1[4].toString().trim()));
                        fd1.setDescuento(new BigDecimal(fila1[5].toString().trim()));
                        fd1.setPrecioTotalSinImpuesto(new BigDecimal(fila1[6].toString().trim()));

                        List<DetAdicional> lsta = new ArrayList();
                        DetAdicional dea = new DetAdicional();
                        dea.setNombre("Vendedor");
                        dea.setValor(fila1[7].toString().trim());
                        lsta.add(dea);
                        fd1.setDetAdicional(lsta);

                        List<Impuesto> lstimp = new ArrayList();
                        Impuesto imp = new Impuesto();
                        imp.setCodigo(fila1[8].toString().trim());
                        imp.setCodigoPorcentaje(fila1[9].toString().trim());

                        imp.setTarifa(new BigDecimal(fila1[10].toString().trim()));
                        imp.setBaseImponible(new BigDecimal(fila1[11].toString().trim()));
                        imp.setValor(new BigDecimal(fila1[12].toString().trim()));
                        lstimp.add(imp);
                        fd1.setImpuesto(lstimp);
                        detalle.add(fd1);
                    }// while
                    dts.setDetalle(detalle);
                    nc.setDetalles(dts);
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }// try 2
                //-----------------------------------------------------------------------
                InfoAdicional infad = new InfoAdicional();
                List<CampoAdicional> lstifa = new ArrayList();
                CampoAdicional cmpad = new CampoAdicional();
                cmpad.setNombre("Proveedor");
                cmpad.setValue("Grupo Santorun");
                lstifa.add(cmpad);
                infad.setCampoAdicional(lstifa);
                nc.setInfoAdicional(infad);
                
                //--------------------------------------------------------------------------------------
                if (dir.exists()) {
                    XMLUtil.marshall(nc, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                } else {
                    dir.mkdirs();
                    XMLUtil.marshall(nc, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml");
                    tranferencia = true;
                }
                
                
            } else {
                System.out.println("Cliente No Econtrado");
                tranferencia = false;
                System.exit(0);
            }
            cst.close();
            con.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(0);
        }
        
        /////------------------------------------------------ VALIDA EL XML GENERADO POR MEDIO DEL XSD
               if (tranferencia) {
                    File dirXSD = new File("xsd/");
                    //File dirXSD = new File("/home/asfact/public_html/api/jar/xsd/");
                    if (dirXSD.exists()) {
                        String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("xsd/notaCredito.xsd"));
                        //String mensajes = XMLUtil.validarXML(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml"), new File("/home/asfact/public_html/api/jar/xsd/notaCredito.xsd"));
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
                }     else {
                        System.out.println("Nota Credito No Generada");
                      }
               
        //////---------------------------------------------------------FIRMA ELECTRONICA
                if (tranferencia) {
                    File dirff = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoFirmada/" + rucEmpresa + "/");
                    String ruta2F=new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();                    
                    
                    if (dirff.exists()) {                    
                        Signer s = new Signer(ruta2F, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                        String rutaF=new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoGenerada/" + rucEmpresa + "/" + nomclave + ".xml").getAbsolutePath();
                        Signer s = new Signer(rutaF, "/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoFirmada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                    System.out.println("Nota Credito No Paso la validacion XSD");
                    System.exit(0);
                } 
        
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
                
                if (disponibilidad) {
                  try {
                    if (ambiente.equals("1")) {
                        System.out.println("entro a procesar autorizado ? " + existeAutorizado);
                        urlwebService = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                        CertificadosSSL.instalarCertificados();
                        EnvioComprobantesWSOFFPRU wsa = new EnvioComprobantesWSOFFPRU(urlwebService.toString().trim());                      
                        com.gruposantorun.comprobantes.ws.pruebas.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
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
                    if (ambiente.equals("2")) {
                        
                        urlwebService = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
                        CertificadosSSL.instalarCertificados();
                        EnvioComprobantesWSOFFPRO wsa = new EnvioComprobantesWSOFFPRO(urlwebService.toString().trim());                      
                        com.gruposantorun.comprobantes.ws.produccion.off.RespuestaSolicitud respuesta = wsa.enviarComprobante(new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoFirmada/" + rucEmpresa + "/" + nomclave + ".xml"));
                        System.out.println("primero: " + respuesta.getEstado());
                        
                        if (respuesta.getEstado().equals("RECIBIDA")) {
                            System.out.println( "El documento ha sido enviada al SRI" );
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
                                                } // IF
                                            }//for
                                        }//for
                                    }
                                }// fin de devuelta
                            }// fin de recibida
                    }
                    
                    } catch (MalformedURLException ex) {
                        System.out.println("ENVIO(MalformedURLException) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no generado... ");
                        Logger.getLogger(FEUPW_NCRE.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WebServiceException ex) {
                        System.out.println("ENVIO(WebServiceException) \nSin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no generado... ");
                        Logger.getLogger(FEUPW_NCRE.class.getName()).log(Level.SEVERE, null, ex);
                    }// fin try catch  
                }//
                
            }
 
            }// fin de ENVIO 1
        
        System.out.println("envioSRI: " +envioSRI);
        //----------------------si el documento esta autorizado manda a recuperar la autorizacion
        if (reenviaSRI.equals("1"))   {
            File ret_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoAutorizada/" + rucEmpresa + "/");
            File ret_no_aut = new File("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoNoAutorizada/" + rucEmpresa + "/");
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
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                writer.close();

                            } else {
                                ret_aut.mkdirs();
                                Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
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
                                } catch (IOException | JAXBException ex) {
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
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                } else {
                                    ret_no_aut.mkdirs();
                                    Writer writer = new FileWriter("/home/asfact/public_html/api/ComprobanteElectronicos/NOTACREDITO/notaCreditoNoAutorizada/" + rucEmpresa + "/" + nomclave + ".xml");
                                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                    marshaller.marshal(new JAXBElement<A>(new QName("autorizacion"), A.class, aut), writer);
                                    writer.close();
                                }
                                /////////////////////////////////////////////////////////////////////// 
                            }// no autorizado
                        }// bandera 
                    } catch (Exception ex) {
                        System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no generado..!");
                    }//fin del try catch
                }// fin de for
            } catch (Exception ex) {
                System.out.println("ENVIO(Exception) Sin respuesta del servidor  del SRI: \n" + ex.getMessage() + "\nDocumento no generado..!");
            }
        }//fin de obtener
        
        //---------------------------hacemos el insert para el fccompelec
        save = true;
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
                cst.setString(3, "DE");
                cst.setBigDecimal(4, num_bd);
                cst.setBigDecimal(5, cod_bd);
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
                envio2 = true;
            } catch (SQLException | HeadlessException e) {
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
