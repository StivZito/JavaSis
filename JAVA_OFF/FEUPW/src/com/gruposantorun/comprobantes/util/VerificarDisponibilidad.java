/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.util;

import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Programador3
 */
public class VerificarDisponibilidad {

    public static Object getWebService(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
            URL url = new URL(wsdlLocation);
            RecepcionComprobantesService service = new RecepcionComprobantesService(url, qname);
            return null;
        } catch (MalformedURLException ex) {
            return ex;
        } catch (WebServiceException ws) {
            return ws;
        }
    }

    public static boolean existeConexion(String url) {
        int i = 0;
        boolean respuesta = false;
        while (i < 3) {
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
//    public static void main(String[] args) {
//      
//        CertificadosSSL.instalarCertificados();
//             
//        if(existeConexion("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantes?wsdl")){
//            
//            System.out.println("El WebService esta:"+true);
//        
//        }else {
//            System.out.println("El WebService esta:"+false);
//        
//        }
//        
//    }
    
}
