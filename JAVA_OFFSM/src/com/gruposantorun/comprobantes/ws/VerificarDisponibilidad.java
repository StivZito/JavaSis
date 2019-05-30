/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws;

import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Progrmador3
 */
public class VerificarDisponibilidad {

    public static Object getWebService(String wsdlLocation) {
        try {
            URL url = new URL(wsdlLocation);
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
            RecepcionComprobantesService service = new RecepcionComprobantesService(url, qname);
            return null;

        } catch (MalformedURLException ex) {
            Logger.getLogger(VerificarDisponibilidad.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        } catch (WebServiceException ws) {
            return ws;
        }
    }

    public static boolean existeConexion(String ambiente) {
        int i = 0;
        String url = "";
        if (ambiente.equals("1")) {
            url = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
        }
        if (ambiente.equals("2")) {
            url = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl";
        }
        boolean respuesta = false;
        while (i < 2) {
            Object obj = getWebService(url);
            if (obj == null) {
                return true;
            }
            if ((obj instanceof WebServiceException)) {
                respuesta = false;
            }
            i++;
        }
        return respuesta;
    }

}
