/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Progrmador3
 */
public class ValidaContingengia {

    Connection con = null;
    String sql = "";
    int cont = 0;

    public int valida(String CODEMP, String CODSUC, String TIPO, double numero, double factura) {
        sql = " select COUNT(*) AS CANTIDAD "
                + " from  fcclavcont "
                + " where CODEMP=" + CODEMP
                + " AND   CODSUC=" + CODSUC
                + " AND   TIPO=" + "'" + TIPO + "'"
                + " AND   NUMERO_TRANS=" + numero
                + " AND   NUMERO_FACT=" + factura;
        pruebanet bp = new pruebanet();
        try {
            con = bp.getConexion();
            PreparedStatement pstm = con.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                cont = rs.getInt("CANTIDAD");
            }

        } catch (Exception e) {
            
           cont=-1; 
        }

        return cont;

    }
}
