/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws.produccion.off;

import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import feupw.Exepcion_try;
import java.util.List;

public class VerificarExistenciaAutorizadoOFFPRO {

    String urlwebService1 = "";
    String nume = "";
    Integer cant = 0;
    int conta = 0, contano = 0;
    boolean bueno = false, salir = true;

    Exepcion_try pasar= new Exepcion_try();

    public VerificarExistenciaAutorizadoOFFPRO() {
    }

    public boolean VerificaWebServ(String clave, String ambiente) {
        if (ambiente.equals("1")) {
                urlwebService1 = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
            }
            if (ambiente.equals("2")) {
                urlwebService1 = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
            }
        try {
            CertificadosSSL.instalarCertificados();
            AutorizacionComprobantesWSOFFPRO ws = new AutorizacionComprobantesWSOFFPRO(urlwebService1.toString().trim());
            RespuestaComprobante respuesta = ws.autorizarComprobante(clave);
            nume = respuesta.getNumeroComprobantes();
            cant = Integer.parseInt(nume);
            if (cant > 0) {
                List<Autorizacion> lstAutorizacion = respuesta.getAutorizaciones().getAutorizacion();
                for (Autorizacion autorizacion : lstAutorizacion) {
                    if (autorizacion.getEstado().equals("AUTORIZADO")) {
                        conta++;
                        salir = false;
                        bueno = true;
                    } else {
                        if (salir) {
                            contano++;
                            bueno = false;
                        }
                    }//if
                } // for de buqueda
            } else {
                bueno = false;
            }
        } catch (Exception ex) {
            bueno=false;
            pasar.setTryExp(1); 
        }
        return bueno;
    }
}
