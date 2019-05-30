/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.notacredito;

import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlRootElement(name = "notaCredito")
@XmlType(propOrder = {
    "id", "version", "infoTributaria", "infoNotaCredito", "detalles", "infoAdicional"})
public class NotaCredito {

    protected InfoTributaria infoTributaria;
    protected InfoNotaCredito infoNotaCredito;
    protected Detalles detalles;
//    protected Motivos motivos;
    protected InfoAdicional infoAdicional;
    protected String id;
    protected String version;

    public Detalles getDetalles() {
        return detalles;
    }

    public void setDetalles(Detalles detalles) {
        this.detalles = detalles;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public InfoNotaCredito getInfoNotaCredito() {
        return infoNotaCredito;
    }

    public void setInfoNotaCredito(InfoNotaCredito infoNotaCredito) {
        this.infoNotaCredito = infoNotaCredito;
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    @XmlAttribute(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

//    public Motivos getMotivos() {
//        return motivos;
//    }
//
//    public void setMotivos(Motivos motivos) {
//        this.motivos = motivos;
//    }
}
