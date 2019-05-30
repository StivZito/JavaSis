/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.net.URL;
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
            System.out.println(ex.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }
        return response;
    }
}
