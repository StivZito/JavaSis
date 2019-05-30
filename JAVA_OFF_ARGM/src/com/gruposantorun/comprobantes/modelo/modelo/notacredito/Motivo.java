/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.notacredito;

import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author Rolando
 */
public class Motivo {

    private String motivo;

    @XmlValue
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
