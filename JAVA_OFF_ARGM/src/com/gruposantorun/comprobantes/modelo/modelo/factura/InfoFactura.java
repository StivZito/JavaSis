/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.factura;

import java.math.BigDecimal;
import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial",
    "obligadoContabilidad", "tipoIdentificacionComprador", "guiaRemision",
    "razonSocialComprador", "identificacionComprador", "totalSinImpuestos",
    "totalDescuento", "totalImpuesto","propina", "importeTotal", "moneda", "pago",
    "valorRetIva","valorRetRenta"})
public class InfoFactura {

    protected String              fechaEmision;
    protected String              dirEstablecimiento;
    protected String              contribuyenteEspecial;
    protected String              obligadoContabilidad;
    protected String              tipoIdentificacionComprador;
    protected String              guiaRemision;
    protected String              razonSocialComprador;
    protected String              identificacionComprador;
    protected BigDecimal          totalSinImpuestos;
    protected BigDecimal          totalDescuento;
    protected BigDecimal          valorRetIva;
    protected BigDecimal          valorRetRenta;
    private   List<TotalImpuesto> totalImpuesto;
    private   List<Pago>          pago;    
    protected BigDecimal          propina;
    protected BigDecimal          importeTotal;
    protected String              moneda;
    
    @XmlElementWrapper(name = "pagos")
    public List<Pago> getPago() {
        return pago;
    }
    
        public void setPago(List<Pago> pago) {
        this.pago = pago;
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

    public String getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(String guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = propina;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    @XmlElementWrapper(name = "totalConImpuestos")
    public List<TotalImpuesto> getTotalImpuesto() {
        return totalImpuesto;
    }

    public void setTotalImpuesto(List<TotalImpuesto> totalImpuesto) {
        this.totalImpuesto = totalImpuesto;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }
    
     public BigDecimal getValorRetIva() {
        return valorRetIva;
    }

    public void setValorRetIva(BigDecimal valorRetIva) {
        this.valorRetIva = valorRetIva;
    }
    
    public BigDecimal getValorRetRenta() {
        return valorRetRenta;
    }

    public void setValorRetRenta(BigDecimal valorRetRenta) {
        this.valorRetRenta = valorRetRenta;
    }
  
}
