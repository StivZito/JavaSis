/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.notadebito;

import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlRootElement(name = "notaDebito")
@XmlType(propOrder = {
    "id", "version", "infoTributaria", "infoNotaDebito", "motivo", "infoAdicional"})
public class NotaDebito {

    protected InfoTributaria infoTributaria;
    protected InfoNotaDebito infoNotaDebito;
    protected List<Motivo> motivo;
    protected InfoAdicional infoAdicional;
    protected String id;
    protected String version;

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

    public InfoNotaDebito getInfoNotaDebito() {
        return infoNotaDebito;
    }

    public void setInfoNotaDebito(InfoNotaDebito infoNotaDebito) {
        this.infoNotaDebito = infoNotaDebito;
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    @XmlElementWrapper(name = "motivos")
    public List<Motivo> getMotivo() {
        return motivo;
    }

    public void setMotivo(List<Motivo> motivo) {
        this.motivo = motivo;
    }

    @XmlAttribute(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
