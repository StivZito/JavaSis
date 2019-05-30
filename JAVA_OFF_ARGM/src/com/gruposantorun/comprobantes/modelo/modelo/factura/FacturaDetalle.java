/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.factura;

import com.gruposantorun.comprobantes.modelo.modelo.DetAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.Impuesto;
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
    "codigoPrincipal", "codigoAuxiliar", "descripcion", "cantidad", "precioUnitario",
    "descuento", "precioTotalSinImpuesto", "detAdicional", "impuesto"})
public class FacturaDetalle {

    protected String codigoPrincipal;
    protected String codigoAuxiliar;
    protected String descripcion;
    protected BigDecimal cantidad;
    protected BigDecimal precioUnitario;
    protected BigDecimal descuento;
    protected BigDecimal precioTotalSinImpuesto;
    private List<DetAdicional> detAdicional;
    private List<Impuesto> impuesto;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
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
