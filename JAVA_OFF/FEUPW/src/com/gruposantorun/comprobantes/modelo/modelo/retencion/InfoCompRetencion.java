/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.retencion;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial",
    "obligadoContabilidad", "tipoIdentificacionSujetoRetenido",
    "razonSocialSujetoRetenido", "identificacionSujetoRetenido", "periodoFiscal"})
public class InfoCompRetencion {

    protected String fechaEmision;
    protected String dirEstablecimiento;
    protected String contribuyenteEspecial;
    protected String obligadoContabilidad;
    protected String tipoIdentificacionSujetoRetenido;
    protected String razonSocialSujetoRetenido;
    protected String identificacionSujetoRetenido;
    protected String periodoFiscal;

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

    public String getIdentificacionSujetoRetenido() {
        return identificacionSujetoRetenido;
    }

    public void setIdentificacionSujetoRetenido(String identificacionSujetoRetenido) {
        this.identificacionSujetoRetenido = identificacionSujetoRetenido;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getPeriodoFiscal() {
        return periodoFiscal;
    }

    public void setPeriodoFiscal(String periodoFiscal) {
        this.periodoFiscal = periodoFiscal;
    }

    public String getRazonSocialSujetoRetenido() {
        return razonSocialSujetoRetenido;
    }

    public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
        this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
    }

    public String getTipoIdentificacionSujetoRetenido() {
        return tipoIdentificacionSujetoRetenido;
    }

    public void setTipoIdentificacionSujetoRetenido(String tipoIdentificacionSujetoRetenido) {
        this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
    }
}
