/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.ws;
/**
 *
 * @author Progrmador3
 *
 */
import com.gruposantorun.comprobantes.seguridad.CertificadosSSL;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import feupw.Exepcion_try;
import java.util.List;

public class VerificarExistenciaAutorizado {

    String urlwebService1 = "";
    String nume = "";
    Integer cant = 0;
    int conta = 0, contano = 0;
    boolean bueno = false, salir = true;

    Exepcion_try pasar= new Exepcion_try();

    public VerificarExistenciaAutorizado() {
    }

    public boolean VerificaWebServ(String clave, String ambiente) {
        if (ambiente.equals("1")) {
                urlwebService1 = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
            }
            if (ambiente.equals("2")) {
                urlwebService1 = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes?wsdl";
            }
        try {
            CertificadosSSL.instalarCertificados();
            AutorizacionComprobantesWS ws = new AutorizacionComprobantesWS(urlwebService1.toString().trim());
            RespuestaComprobante respuesta = ws.autorizarComprobante(clave);
            nume = respuesta.getNumeroComprobantes();
            cant = Integer.parseInt(nume);
            if (cant > 0) {
                List<Autorizacion> lstAutorizacion = respuesta.getAutorizaciones().getAutorizacion();
                for (Autorizacion autorizacion : lstAutorizacion) {
                    if (autorizacion.getEstado().equals("AUTORIZADO")) {
                        System.out.println("MENSAJE: " + autorizacion.getEstado());
                        conta++;
                        salir = false;
                        bueno = true;
                    } else {
                        if (salir) {
                            System.out.println("MENSAJE: " + autorizacion.getEstado());
                            contano++;
                            bueno = false;
                        }
                    }//if
                } // for de buqueda
            } else {
                bueno = false;
            }

        } catch (Exception ex) {
            System.out.println("Sin conexion al Websrvice, \nIntente despues de unos minutos...!\nMensage:  " + ex.getMessage());
            bueno=false;
            pasar.setTryExp(1); 
            System.exit(0);
        }
        return bueno;
    }
}
