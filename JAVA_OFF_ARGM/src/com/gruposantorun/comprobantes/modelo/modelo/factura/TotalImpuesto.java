/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.factura;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "codigo", "codigoPorcentaje", "baseImponible", "tarifa", "valor"})
public class TotalImpuesto {

    protected String codigo;
    protected String codigoPorcentaje;
    protected BigDecimal baseImponible;
    protected BigDecimal tarifa;
    protected BigDecimal valor;

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public void setCodigoPorcentaje(String codigoPorcentaje) {
        this.codigoPorcentaje = codigoPorcentaje;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
