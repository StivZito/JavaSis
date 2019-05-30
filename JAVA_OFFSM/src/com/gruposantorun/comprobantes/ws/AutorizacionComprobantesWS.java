/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws;

import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author Programador3
 */
public class AutorizacionComprobantesWS {
    
    private AutorizacionComprobantesService service;
    
    public AutorizacionComprobantesWS(String wsdlLocation) {
        try {
            service = new AutorizacionComprobantesService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesService"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public RespuestaComprobante autorizarComprobante(String claveDeAcceso) {
        RespuestaComprobante response = null;
        try {
            AutorizacionComprobantes port = service.getAutorizacionComprobantesPort();
            response = port.autorizacionComprobante(claveDeAcceso);
            //
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
    
//    public static void main(String[] args) {
//        CertificadosSSL.instalarCertificados();
//        AutorizacionComprobantesWS ws = new AutorizacionComprobantesWS("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl");
//        RespuestaComprobante respuesta = ws.autorizarComprobante("1007201401099263550900110010010003110881234567817");
//        List<Autorizacion> lstAutorizacion = respuesta.getAutorizaciones().getAutorizacion();
//        for (Autorizacion autorizacion : lstAutorizacion) {
//            System.out.println(autorizacion.getEstado() + "\t" + autorizacion.getFechaAutorizacion());
//            List<Mensaje> lstMensajes = autorizacion.getMensajes().getMensaje();
//            for (Mensaje mensaje : lstMensajes) {
//                System.out.println(mensaje.getIdentificador() + "\t" + mensaje.getMensaje() + "\t" + mensaje.getInformacionAdicional()+ "\t" + mensaje.getTipo());
//                System.out.println("---------------------");
//            }//for
//        }//for
//        
//    }// void 
    
}
