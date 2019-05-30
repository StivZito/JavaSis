/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.notacredito;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador", "razonSocialComprador",
    "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise", "codDocModificado",
    "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "valorModificacion", "moneda", "totalConImpuestos", "motivo"})
public class InfoNotaCredito {

    protected String fechaEmision;
    protected String dirEstablecimiento;
    protected String tipoIdentificacionComprador;
    protected String razonSocialComprador;
    protected String identificacionComprador;
    protected String contribuyenteEspecial;
    protected String obligadoContabilidad;
    protected String rise;
    protected String codDocModificado;
    protected String numDocModificado;
    protected String fechaEmisionDocSustento;
    protected BigDecimal totalSinImpuestos;
    protected BigDecimal valorModificacion;
    protected String moneda;
    protected TotalConImpuestos totalConImpuestos;
    protected String motivo;

    public String getCodDocModificado() {
        return codDocModificado;
    }

    public void setCodDocModificado(String codDocModificado) {
        this.codDocModificado = codDocModificado;
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = contribuyenteEspecial;
    }

    public String getDirEstablecimiento() {
        return dirEstablecimiento;
    }

    public void setDirEstablecimiento(String dirEstablecimiento) {
        this.dirEstablecimiento = dirEstablecimiento;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumDocModificado() {
        return numDocModificado;
    }

    public void setNumDocModificado(String numDocModificado) {
        this.numDocModificado = numDocModificado;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    public TotalConImpuestos getTotalConImpuestos() {
        return totalConImpuestos;
    }

    public void setTotalConImpuestos(TotalConImpuestos totalConImpuestos) {
        this.totalConImpuestos = totalConImpuestos;
    }

    public BigDecimal getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }

    public BigDecimal getValorModificacion() {
        return valorModificacion;
    }

    public void setValorModificacion(BigDecimal valorModificacion) {
        this.valorModificacion = valorModificacion;
    }
}
