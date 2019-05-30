/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

/**
 *
 * @author Programador3
 */
public class ClaveAcceso {
     
    public  ClaveAcceso (){}
    public String generarClaveAcceso(String claveaux) {
        int verificador = 0;
       
        int po=0;
        StringBuilder clave = new StringBuilder(claveaux);
        
        verificador = generarDigitoModulo11(claveaux.toString());
        System.out.println(verificador);
        clave.append(Integer.toString(verificador));
        System.out.println(clave);
        po=clave.toString().length();
        System.out.println("longitud "+po);
        String claveGenerada = clave.toString();
        if (clave.toString().length() != 49) {
            claveGenerada = null;
        }
        return claveGenerada;
    }

    public static String generarClaveAcceso(Date fechaEmision, String tipoComprobante,
            String ruc, String ambiente, String serie, String numeroComprobante,
            String codigoNumerico, String tipoEmision) {
        int verificador = 0;
        if (ruc != null && ruc.length() < 13) {
            ruc = String.format("%013d", new Object[]{ruc});
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String fecha = dateFormat.format(fechaEmision);
        StringBuilder clave = new StringBuilder(fecha);
        clave.append(tipoComprobante);
        clave.append(ruc);
        clave.append(ambiente);
        clave.append(serie);
        clave.append(numeroComprobante);
        clave.append(codigoNumerico);
        clave.append(tipoEmision);
        verificador = generarDigitoModulo11(clave.toString());
        clave.append(Integer.valueOf(verificador));
        String claveGenerada = clave.toString();
        if (clave.toString().length() != 49) {
            claveGenerada = null;
        }
        return claveGenerada;
    }

    public String generaClaveContingencia(String fechaEmision, String tipoComprobante,String ruc,String ambiente,String clavesContigencia, String tipoEmision) throws InputMismatchException {
        int verificador = 0;
        String claveGenerada = "";
        StringBuilder clave = new StringBuilder(fechaEmision);
        clave.append(tipoComprobante);
        clave.append(ruc);
        clave.append(ambiente);
        clave.append(clavesContigencia);
        clave.append(tipoEmision);
        verificador = generarDigitoModulo11(clave.toString());
        if (verificador != 10) {
            clave.append(Integer.valueOf(verificador));
            claveGenerada = clave.toString();
        }
        if (clave.toString().length() != 49) {
            claveGenerada = null;
        }
        return claveGenerada;
    }

    public static int generarDigitoModulo11(String cadena) {
        int baseMultiplicador = 7;
        int aux[] = new int[cadena.length()];
        int multiplicador = 2;
        int total = 0;
        int verificador = 0;
        for (int i = aux.length - 1; i >= 0; i--) {
            aux[i] = Integer.parseInt((new StringBuilder()).append("").append(cadena.charAt(i)).toString());
            aux[i] = aux[i] * multiplicador;
            if (++multiplicador > baseMultiplicador) {
                multiplicador = 2;
            }
            total += aux[i];
        }

        if (total == 0 || total == 1) {
            verificador = 0;
        } else {
            verificador = 11 - total % 11 != 11 ? 11 - total % 11 : 0;
        }
        if (verificador == 10) {
            verificador = 1;
        }
        return verificador;
    }
}
