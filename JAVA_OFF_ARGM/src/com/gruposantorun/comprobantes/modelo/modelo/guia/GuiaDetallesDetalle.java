/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.guia;

import com.gruposantorun.comprobantes.modelo.modelo.DetAdicional;
import java.math.BigDecimal;
import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlRootElement(name = "detalle")
@XmlType(propOrder = {
    "codigoInterno", "codigoAdicional", "descripcion", "cantidad",
    "detAdicional"})
public class GuiaDetallesDetalle {

    protected String codigoInterno;
    protected String codigoAdicional;
    protected String descripcion;
    protected BigDecimal cantidad;
    private List<DetAdicional> detAdicional;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoAdicional() {
        return codigoAdicional;
    }

    public void setCodigoAdicional(String codigoAdicional) {
        this.codigoAdicional = codigoAdicional;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlElementWrapper(name = "detallesAdicionales")
    public List<DetAdicional> getDetAdicional() {
        return detAdicional;
    }

    public void setDetAdicional(List<DetAdicional> detAdicional) {
        this.detAdicional = detAdicional;
    }
}
