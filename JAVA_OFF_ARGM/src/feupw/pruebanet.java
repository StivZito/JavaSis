/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Programador3
 */
public class pruebanet {
    
    public pruebanet(){}   
    
    public static Connection getConexion(){
        LeerSai ls =  new LeerSai();
        String[] auxserv = {};
        String pas_serv="";
        String auxserv1 ="";

        boolean band=false;
        String serv1 = ls.getServerp().trim();         
        String serv2 = serv1.replace("\\", ",");
         
         if(serv1.equals(serv2)){
            auxserv1 = serv2;          
         }else{
            auxserv = serv2.split(",");
            band=true;
         }   
      
         if(band){
            pas_serv = auxserv[0]+"\\"+"\\"+auxserv[1];
         }else{
            pas_serv = auxserv1;
             
         }
        
        System.out.println("Parametros para conexion"); 
        System.out.println("Servidor: " +pas_serv.trim());
        System.out.println("Database: " +ls.getDatabase().trim());
        System.out.println("Id:       " +ls.getLogid().trim());        
        System.out.println("Pass:     " +ls.getLogopassword().trim());
        
        Connection con=null;
        String connectionURL="jdbc:sqlserver://"+pas_serv.trim()+":1433"+";databaseName="+ls.getDatabase().trim()+";selectMethod=Direct;";
        try {           
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection(connectionURL,ls.getLogid().trim(),ls.getLogopassword().trim());
             System.out.println("BASE: Conexion con exito");
        } catch (SQLException e) {
             System.out.println("SQL Exception: " +e.toString());
             System.out.println("ERROR CONEXION BASE:  e  "+e.getMessage());             
        }catch(ClassNotFoundException ce){
             System.out.println("ERROR DE CLASE:   e  "+ce.getMessage());
        }
       
        return con;
        
    }
    
}
