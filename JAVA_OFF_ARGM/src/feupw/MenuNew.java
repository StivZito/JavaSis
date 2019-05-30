/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author progrmador
 */

package feupw;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class MenuNew {
    
    Boolean pasar = false;
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    String tipo = "";  
    LeerSaiFact fac =  new LeerSaiFact();  
    
    
    public MenuNew()  
    {         
        try{
            tipo = fac.getTipo();
            tipo = tipo.toUpperCase();                                              

            if (tipo.equals("FA") || tipo.equals("NV")) { //factura
                eFactura fe = new eFactura();
            }
            if (tipo.equals("RE")) { // retencion
                eRetencion rt = new eRetencion();
            }
            if (tipo.equals("DE")) { // nota de credito
                eNotaCredito cr = new eNotaCredito();
            }
            if (tipo.equals("DB")) { // nota de debito
                eNotaDebito db = new eNotaDebito();
            }
            if (tipo.equals("GR")) { // guia de remision
                eGuiaRemision gui = new eGuiaRemision();
            }   

        }catch (Exception e) {
            System.out.println("No se encontro tipo documento  " + e.getMessage());
            System.exit(0);
        }               
    }    
}
