/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo.factura;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Stiven Prado
 */
@XmlType(propOrder = {
    "formaPago", "total", "plazo", "unidadTiempo"})
public class Pago {

    protected String     formaPago;
    protected String     UnidadTiempo;
    protected BigDecimal total;
    protected BigDecimal plazo;
    
    ////////////////////////////////////////////////////////////
    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    
    ////////////////////////////////////////////////////////////
    public String getUnidadTiempo() {
        return UnidadTiempo;
    }

    public void setUnidadTiempo(String UnidadTiempo) {
        this.UnidadTiempo = UnidadTiempo;
    }
    
    ////////////////////////////////////////////////////////////
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    ////////////////////////////////////////////////////////////
     public BigDecimal getPlazo() {
        return plazo;
    }
    
    public void setPlazo(BigDecimal plazo) {
        this.plazo = plazo;
    }

}
