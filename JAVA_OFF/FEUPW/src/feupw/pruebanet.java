/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import feupw.LeerSai;

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
         String serv1 = ls.getServerp().trim().toString();
         //char [] aux1 = serv.toCharArray(); 
         
        //JOptionPane.showMessageDialog(null,"serv1: "+serv1,"Aviso",JOptionPane.INFORMATION_MESSAGE);
         String serv2 = serv1.replace("\\", ",");
       // JOptionPane.showMessageDialog(null,"serv2: "+serv2,"Aviso",JOptionPane.INFORMATION_MESSAGE); 
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
         
        
         
//         System.out.println("Servidor3:"+pas_serv);
//         System.out.println("--------------------------------------------------");
//         System.out.println("Servidor:"+pas_serv);
//         System.out.println("Base:"+ls.getDatabase().trim());
//         System.out.println("Usuario:"+ls.getLogid().trim());
//         System.out.println("password:"+ls.getLogopassword().trim());
//         System.out.println("---------------------------------------------------");
//          JOptionPane.showMessageDialog(null,"Servidor: "+pas_serv+
//                                             "\nBase: " +ls.getDatabase().trim()+
//                                             "\nUsuario: "+ls.getLogid().trim()+
//                                             "\npassword: "+ls.getLogopassword().trim(),"Aviso",JOptionPane.INFORMATION_MESSAGE);
         Connection con=null;
         //String connectionURL="jdbc:sqlserver://SERVDESARROLLO\\SERVER_PUNTOSOFT:1433;"+"databaseName=Estandar;";
          String connectionURL="jdbc:sqlserver://"+pas_serv.trim().toString()+":1433"+";databaseName="+ls.getDatabase().trim().toString()+";selectMethod=Direct;";
        try {
           
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection(connectionURL,ls.getLogid().trim(),ls.getLogopassword().trim());
             System.out.println("Conexion con exito");
//             JOptionPane.showMessageDialog(null,"Conexion con exito","Aviso",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
             //System.out.println("SQL Exception: " +e.toString());
              JOptionPane.showMessageDialog(null,"ERROR:  e  "+e.getMessage(),"Aviso",JOptionPane.INFORMATION_MESSAGE);
             
        }catch(ClassNotFoundException ce){
            //System.out.println("Class not found Execption: "+ ce.toString());
            JOptionPane.showMessageDialog(null,"ERROR:   e  "+ce.getMessage(),"Aviso",JOptionPane.INFORMATION_MESSAGE);
        }
       
        return con;
        
    }
    
}
