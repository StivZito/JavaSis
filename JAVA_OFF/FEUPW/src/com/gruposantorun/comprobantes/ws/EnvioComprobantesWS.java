/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws;

import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import com.gruposantorun.comprobantes.util.ArchivoUtil;
import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Programador3
 */
public class EnvioComprobantesWS {
    
    private static RecepcionComprobantesService service;
    private RecepcionComprobantes port;
    
    public EnvioComprobantesWS(String wsdlLocation) throws MalformedURLException, WebServiceException {
       URL url = new URL(wsdlLocation);
       //JOptionPane.showMessageDialog(null, "ENVIO:  0", "Aviso", JOptionPane.INFORMATION_MESSAGE);
       QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
       //JOptionPane.showMessageDialog(null, "ENVIO:01 " + qname.toString(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
       service = new RecepcionComprobantesService(url, qname);
       //JOptionPane.showMessageDialog(null, "ENVIO:03 " + service.toString(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
       port = service.getRecepcionComprobantesPort();
       ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 8000);
       ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 8000);
    }
    
    public RespuestaSolicitud enviarComprobante(File xmlFile) {
        RespuestaSolicitud response = null;
        try {
            byte[] archivoBytes = ArchivoUtil.convertirArchivoAByteArray(xmlFile);
            if (archivoBytes != null) {
                response = port.validarComprobante(archivoBytes);
            } else {
                response = new RespuestaSolicitud();
                response.setEstado("NOARCHIVO");
            }
        } catch (Exception e) {
            response = new RespuestaSolicitud();
            response.setEstado(e.getClass().getName());
        }
        return response;
    }
    
//    public static void main(String[] args) throws MalformedURLException {
//        CertificadosSSL.instalarCertificados();
//        EnvioComprobantesWS ws = new EnvioComprobantesWS("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl");
//        RespuestaSolicitud respuesta = ws.enviarComprobante(new File("D:\\FacturaF\\Fact-1007201401099263550900110010010003110881234567817.xml"));
//        System.out.println(respuesta.getEstado());
//        List<Comprobante> lst = respuesta.getComprobantes().getComprobante();
//        for (Comprobante comprobante : lst) {
//            List<Mensaje> lstMensaje = comprobante.getMensajes().getMensaje();
//            for (Mensaje mensaje : lstMensaje) {
//                System.out.println(mensaje.getMensaje() + "\t" + mensaje.getInformacionAdicional());
//            }
//        }
//    }
}
