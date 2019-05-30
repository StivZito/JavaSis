/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws.produccion.off;

import com.gruposantorun.comprobantes.util.ArchivoUtil;
import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

public class EnvioComprobantesWSOFFPRO {
    
    private static RecepcionComprobantesOfflineService service;
    private RecepcionComprobantesOffline port;
    
    public EnvioComprobantesWSOFFPRO(String wsdlLocation) throws MalformedURLException, WebServiceException {
       URL url = new URL(wsdlLocation);
       QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
       service = new RecepcionComprobantesOfflineService(url, qname);
       port = service.getRecepcionComprobantesOfflinePort();
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
    
}
