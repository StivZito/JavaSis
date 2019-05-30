/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws.pruebas.off;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class VerificarDisponibilidadOFFPRU {

    private RecepcionComprobantesOffline port;

    public static Object getWebService(String wsdlLocation) {
        try {
            URL url = new URL(wsdlLocation);
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(url, qname);
            return null;

        } catch (MalformedURLException ex) {
            Logger.getLogger(VerificarDisponibilidadOFFPRU.class.getName()).log(Level.SEVERE, null, ex);
            return ex;
        } catch (WebServiceException ws) {
            return ws;
        }
    }
    
      public static boolean existeConexionOFF(String ambiente) {
        int i = 0;
        String url = "";
        if (ambiente.equals("1")) {
            url = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
        }
        if (ambiente.equals("2")) {
            url = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
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
            if ((obj instanceof MalformedURLException)) {
                respuesta = false;
            }
            i++;
        }
        return respuesta;
    }
}
