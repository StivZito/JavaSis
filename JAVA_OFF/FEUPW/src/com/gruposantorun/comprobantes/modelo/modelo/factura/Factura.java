/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.factura;

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
@XmlRootElement(name = "factura")
@XmlType(propOrder = {
    "id", "version", "infoTributaria", "infoFactura", "detalle", "infoAdicional", "retencion"})
public class Factura {

    private String id;
    protected String version;
    protected InfoTributaria infoTributaria;
    protected InfoFactura infoFactura;
    private List<FacturaDetalle> detalle;
    protected InfoAdicional infoAdicional;
    private List<RetencionFactura> retencion;

    @XmlElementWrapper(name = "retenciones")
    public List<RetencionFactura> getRetencion() {
        return retencion;
    }

    public void setRetencion(List<RetencionFactura> retencion) {
        this.retencion = retencion;
    }

    @XmlElementWrapper(name = "detalles")
    public List<FacturaDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<FacturaDetalle> detalle) {
        this.detalle = detalle;
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

    @XmlAttribute(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }//

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    public InfoFactura getInfoFactura() {
        return infoFactura;
    }

    public void setInfoFactura(InfoFactura infoFactura) {
        this.infoFactura = infoFactura;
    }
//    public RetencionesFactura getRetenciones() {
//        return retenciones;
//    }
//
//    public void setRetenciones(RetencionesFactura retenciones) {
//        this.retenciones = retenciones;
//    }
}
