/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion.Mensajes;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "autorizacion")
@XmlType(name = "autorizacion", propOrder = {"estado", "numeroAutorizacion", "fechaAutorizacion","ambiente" ,"comprobante", "mensajes"})
public class A {

    protected String estado;
    protected String numeroAutorizacion;
    //@XmlSchemaType(name = "dateTime")
    protected String fechaAutorizacion;
    protected String ambiente;

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }
    protected String comprobante;
    
    protected Mensajes mensajes;

    public A() {
        //compiled code
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(String fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public Mensajes getMensajes() {
        return mensajes;
    }

    public void setMensajes(Mensajes mensajes) {
        this.mensajes = mensajes;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }
}
