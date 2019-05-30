/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
/**
 *
 * @author Risoflin
 */
public class LeerSaiFact {
   private static String numero;
   private static String tipo;
   private static String empresa;
   private static String sucursal;
   private static String factura;
   private static String ruc;

    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String num) {
        numero = num;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tip) {
        tipo = tip;
    }
    
    public String getEmpresa() {
        return empresa;
    }
    
    public void setEmpresa(String emp) {
        empresa = emp;
    }
    
    public String getSucursal() {
        return sucursal;
    }
    
    public void setSucursal(String suc) {
        sucursal = suc;
    }
    
    public String getFactura() {
        return factura;
    }
    public void setFactura(String fac) {
        factura = fac;
    }
    
    public String getRuc() {
        return ruc;
    }
    
    public void setRuc(String as_ruc) {
        ruc = as_ruc;
    }
    
}