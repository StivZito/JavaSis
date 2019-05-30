/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.notacredito;

import com.gruposantorun.comprobantes.modelo.modelo.DetAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.Impuesto;
import java.math.BigDecimal;
import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "codigoInterno", "codigoAdicional", "descripcion", "cantidad", "precioUnitario",
    "descuento", "precioTotalSinImpuesto", "detAdicional", "impuesto"})
public class Detalle {

    protected String codigoInterno;
    protected String codigoAdicional;
    protected String descripcion;
    protected BigDecimal cantidad;
    protected BigDecimal precioUnitario;
    protected BigDecimal descuento;
    protected BigDecimal precioTotalSinImpuesto;
    private List<DetAdicional> detAdicional;
    private List<Impuesto> impuesto;

    @XmlElementWrapper(name = "detallesAdicionales")
    public List<DetAdicional> getDetAdicional() {
        return detAdicional;
    }

    public void setDetAdicional(List<DetAdicional> detAdicional) {
        this.detAdicional = detAdicional;
    }

    @XmlElementWrapper(name = "impuestos")
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(List<Impuesto> impuesto) {
        this.impuesto = impuesto;
    }

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

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getPrecioTotalSinImpuesto() {
        return precioTotalSinImpuesto;
    }

    public void setPrecioTotalSinImpuesto(BigDecimal precioTotalSinImpuesto) {
        this.precioTotalSinImpuesto = precioTotalSinImpuesto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
