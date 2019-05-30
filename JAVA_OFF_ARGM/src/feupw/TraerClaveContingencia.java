/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import feupw.pruebanet.*;
import java.lang.String;
import javax.swing.JOptionPane;

/**
 *
 * @author Progrmador3
 */
public class TraerClaveContingencia {
    
    String claveContingencia = "";
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;

    public TraerClaveContingencia() {
    }
    

    public String getClaveContingencia() {
        return claveContingencia;
    }

    public void setClaveContingencia(String claveContingencia) {
        this.claveContingencia = claveContingencia;
    }

    public String traeClave( String codempaux , String codsucaux ) {
       String auto ="";
       pruebanet db = new pruebanet();
       try {
            con = db.getConexion();
            cst = con.prepareCall("{call buscar_clave_contigencia(?,?,?,?)}");
            cst.setString(1,codempaux);
            cst.setString(2,codsucaux );
            cst.setDouble(3,1);
            cst.setString(4,"");
            rs = cst.executeQuery();
            while (rs.next()) {
                setClaveContingencia(rs.getString("AUT_CONT"));
                //System.out.println("aut: "+getClaveContingencia());
            }
            
           con.close();
           cst.close();         
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "ERROR: " + e.getMessage(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
       try {
            con = db.getConexion();
            cst = con.prepareCall("{call buscar_clave_contigencia(?,?,?,?)}"); 
            cst.setString(1,codempaux);
            cst.setString(2,codsucaux );
            cst.setDouble(3,2);
            cst.setString(4,getClaveContingencia());
            rs = cst.executeQuery();
            con.close();
            cst.close();         
        } catch (Exception e) {
          //JOptionPane.showMessageDialog(null, "ERROR: " + e.getMessage(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
        auto = getClaveContingencia(); 
       // JOptionPane.showMessageDialog(null, "AUTORIZACION: "+auto.toString().trim(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
        return   auto.toString().trim();
    }
    
    
    
//    public static void main(String[] args) {
//        String hola ="";
//        TraerClaveContingencia tr = new TraerClaveContingencia();
//        hola = tr.traeClave();
//        JOptionPane.showMessageDialog(null, "AUTORIZACION: "+hola, "Aviso", JOptionPane.INFORMATION_MESSAGE);
//        
//        
//    }
    
    
}
