/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws.pruebas.off;
import java.net.URL;

import javax.xml.namespace.QName;

public class AutorizacionComprobantesWSOFFPRU {
    
    private AutorizacionComprobantesOfflineService service;
    
    public AutorizacionComprobantesWSOFFPRU(String wsdlLocation) {
        try {
            service = new AutorizacionComprobantesOfflineService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public RespuestaComprobante autorizarComprobante(String claveDeAcceso) {
        RespuestaComprobante response = null;
        try {
            AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
            response = port.autorizacionComprobante(claveDeAcceso);
            //
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}
