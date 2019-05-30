/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Washington
 */
public class LeerSai {
    
    
   private static String serverp;
   private static String database;
   private static String logid;
   private static String logopassword;
   
   
   public static String getDatabase() {
        return database;
    }

    public static String getLogid() {
        return logid;
    }

    public static String getLogopassword() {
        return logopassword;
    }

    public static String getServerp() {
        return serverp;
    }

    public static void setDatabase(String database) {
        LeerSai.database = database;
    }

    public static void setLogid(String logid) {
        LeerSai.logid = logid;
    }

    public static void setLogopassword(String logopassword) {
        LeerSai.logopassword = logopassword;
    }

    public static void setServerp(String serverp) {
        LeerSai.serverp = serverp;
    }
    public static  String escogerLinea(String aux){
        Pattern pat = Pattern.compile("(^;|:|\\[).*");
        Matcher mat = pat.matcher(aux.trim());
        //System.out.println(mat);
        if(!mat.matches()){
          String lin = aux.trim();
          return lin;
        }
//        String lin = aux.trim();
//        String pyc="";
//        if(!lin.equals("")){  pyc=lin.substring(0,1);      }
//        if(!pyc.matches(";|[|:")) {return lin;}
        return "";
    }   
    
    public static  String derCadena(String cadr){
       String cadena=cadr.trim();
       //System.out.println(cadena);
       String cad = cadena.substring(cadena.indexOf("=")+1,cadena.length());
       //System.out.println(" Cadena izq:"+cad.trim());
      
       return cad;
    }
    public static  String izqCadena(String cadr1){
       String cadena1=cadr1.trim();
       //System.out.println(cadena1);
       String cad1 = cadena1.substring(0,cadena1.indexOf("="));
       //System.out.println(" Cadena der:"+cad1.trim());
      
       return cad1;
    }
    
    public LeerSai() {
        DataConect dc = new DataConect();
        String linea="";
        List<String> lst = new ArrayList();     
        int i=0;
        try {
             
            FileInputStream archivo = new FileInputStream("C:\\sai_win\\sai.ini");
            DataInputStream entrada = new DataInputStream(archivo);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
            while((linea=buffer.readLine())!=null){  
                if(!(linea.isEmpty())){
                     String filtro = escogerLinea(linea);
                     if(!filtro.isEmpty()){       
                          
//                             System.out.println("["+  i  +"]" + filtro);
//                             i++;
                             lst.add(filtro);
                          
                  }
                 
                }//if
            }// while           
            entrada.close();
            //----------------------------------------------------------------------
            int count =lst.size();
            String server = "(servername|Servername)";
            Pattern patserv = Pattern.compile(server);
            String Logid = "(Logid|logid)";
            Pattern patlogid = Pattern.compile(Logid);
            String LogPassword ="(LogPassword|logpassword)";
            Pattern patlogp = Pattern.compile(LogPassword);
            String Database = "(Database|database)";
            Pattern patdatab = Pattern.compile(Database);

            //System.out.println("Filas: "+count);
            for(int x=0; x < count; x++){     
                 String evalua =izqCadena(lst.get(x));
                 Matcher matser = patserv.matcher(evalua.trim());
                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                 if(matser.matches()){
                     String datser= derCadena(lst.get(x).toString());
                     //System.out.println(datser);
                     dc.setServer(datser);
                     serverp=datser;
                 
                 }
                 
              
              }
             for(int x=0; x < count; x++){     
                 String log =izqCadena(lst.get(x));
                 Matcher matser = patlogid.matcher(log.trim());
                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                 if(matser.matches()){
                     String datser= derCadena(lst.get(x).toString());
//                     System.out.println(datser);
                      dc.setLogid(datser);
                      //logid = datser;
                      setLogid(datser);
                 
                 }
                 
              
              }
             for(int x=0; x < count; x++){     
                 String logp =izqCadena(lst.get(x));
                 Matcher matser = patlogp.matcher(logp.trim());
                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                 if(matser.matches()){
                     String datser= derCadena(lst.get(x).toString());
//                     System.out.println(datser);
                      //logopassword =datser;
                      setLogopassword(datser);
                 }
              }
             for(int x=0; x < count; x++){     
                 String datbas =izqCadena(lst.get(x));
                 Matcher matser = patdatab.matcher(datbas.trim());
                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                 if(matser.matches()){
                     String datser= derCadena(lst.get(x).toString());
//                     System.out.println(datser);
                    //dc.setBase(datser);
                     //database =datser;
                     setDatabase(datser);
                  }
              }
            
        //----------------------------------------------------------------------
         } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
         }

   }

   
    
    
    
}