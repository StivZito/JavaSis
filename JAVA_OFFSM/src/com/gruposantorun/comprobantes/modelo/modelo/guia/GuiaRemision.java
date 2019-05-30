/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.guia;

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
@XmlRootElement(name = "guiaRemision")
@XmlType(propOrder = {
    "id", "version", "infoTributaria", "infoGuiaRemision", "destinatario", "infoAdicional"})
public class GuiaRemision {

    protected InfoTributaria infoTributaria;
    protected InfoGuiaRemision infoGuiaRemision;
    private List<Destinatario> destinatario;
    protected String id;
    protected String version;
    protected InfoAdicional infoAdicional;

    @XmlElementWrapper(name = "destinatarios")
    public List<Destinatario> getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(List<Destinatario> destinatario) {
        this.destinatario = destinatario;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InfoGuiaRemision getInfoGuiaRemision() {
        return infoGuiaRemision;
    }

    public void setInfoGuiaRemision(InfoGuiaRemision infoGuiaRemision) {
        this.infoGuiaRemision = infoGuiaRemision;
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

    public InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }
}
