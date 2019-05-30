/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
/**
 *
 * @author Risoflin
 */
public class LeerSai {    
    
   private static String serverp;
   private static String database;
   private static String logid;
   private static String logopassword;

   public  String getDatabase() {
        return database;
    }
   
    public  void setDatabase(String base) {
        this.database = base;
    }

    public  String getLogid() {
        return logid;
    }
    
    public  void setLogid(String id) {
        this.logid = id;
    }

    public  String getLogopassword() {
        return logopassword;        
    }
    
    public  void setLogopassword(String pass) {
        logopassword = pass;
    }

    public  String getServerp() {
        return serverp;
    }

    public  void setServerp(String serv) {
        serverp = serv;
    }
    
}
