/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.guia;

import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlRootElement(name = "destinatario")
@XmlType(propOrder = {
    "identificacionDestinatario", "razonSocialDestinatario", "dirDestinatario",
    "motivoTraslado", "docAduaneroUnico", "codEstabDestino",
    "ruta", "codDocSustento", "numDocSustento",
    "numAutDocSustento", "fechaEmisionDocSustento", "detalle"})
public class Destinatario {

    protected String identificacionDestinatario;
    protected String razonSocialDestinatario;
    protected String dirDestinatario;
    protected String motivoTraslado;
    protected String docAduaneroUnico;
    protected String codEstabDestino;
    protected String ruta;
    protected String codDocSustento;
    protected String numDocSustento;
    protected String numAutDocSustento;
    protected String fechaEmisionDocSustento;
    private List<GuiaDetallesDetalle> detalle;

    @XmlElementWrapper(name = "detalles")
    public List<GuiaDetallesDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<GuiaDetallesDetalle> detalle) {
        this.detalle = detalle;
    }

    public String getCodDocSustento() {
        return codDocSustento;
    }

    public void setCodDocSustento(String codDocSustento) {
        this.codDocSustento = codDocSustento;
    }

    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    public void setCodEstabDestino(String codEstabDestino) {
        this.codEstabDestino = codEstabDestino;
    }

    public String getDirDestinatario() {
        return dirDestinatario;
    }

    public void setDirDestinatario(String dirDestinatario) {
        this.dirDestinatario = dirDestinatario;
    }

    public String getDocAduaneroUnico() {
        return docAduaneroUnico;
    }

    public void setDocAduaneroUnico(String docAduaneroUnico) {
        this.docAduaneroUnico = docAduaneroUnico;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getIdentificacionDestinatario() {
        return identificacionDestinatario;
    }

    public void setIdentificacionDestinatario(String identificacionDestinatario) {
        this.identificacionDestinatario = identificacionDestinatario;
    }

    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(String motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }

    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    public void setNumAutDocSustento(String numAutDocSustento) {
        this.numAutDocSustento = numAutDocSustento;
    }

    public String getNumDocSustento() {
        return numDocSustento;
    }

    public void setNumDocSustento(String numDocSustento) {
        this.numDocSustento = numDocSustento;
    }

    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    public void setRazonSocialDestinatario(String razonSocialDestinatario) {
        this.razonSocialDestinatario = razonSocialDestinatario;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
