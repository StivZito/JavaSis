/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.retencion;

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
@XmlRootElement(name = "comprobanteRetencion")
@XmlType(propOrder = {
    "id", "version", "infoTributaria", "infoCompRetencion", "impuesto", "infoAdicional"})
public class ComprobanteRetencion {

    protected InfoTributaria infoTributaria;
    protected InfoCompRetencion infoCompRetencion;
    private List<Impuesto> impuesto;
    protected InfoAdicional infoAdicional;
    protected String id;
    protected String version;

    @XmlElementWrapper(name = "impuestos")
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(List<Impuesto> impuesto) {
        this.impuesto = impuesto;
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

    public InfoCompRetencion getInfoCompRetencion() {
        return infoCompRetencion;
    }

    public void setInfoCompRetencion(InfoCompRetencion infoCompRetencion) {
        this.infoCompRetencion = infoCompRetencion;
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
}
