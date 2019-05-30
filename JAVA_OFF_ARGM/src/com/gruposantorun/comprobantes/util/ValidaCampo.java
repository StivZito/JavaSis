/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.util;

/**
 *
 * @author Progrmador3
 */
public class ValidaCampo {

    private String respuesta;

    public ValidaCampo() {
    }

    public String ValidaCampoString(String campo) {
        String salir = "";
        if (campo.isEmpty()) {
            campo = "Vacio";
            salir = campo;

        } else {
            salir = campo;

        }
        return salir;
    }

    public String ValidaCampoDouble(String campo) {
        String salir = "0";
        if (campo.isEmpty()) {
            campo = "-100";
            salir = campo;
        } else {

            salir = campo;
        }

        return salir;
    }
}
