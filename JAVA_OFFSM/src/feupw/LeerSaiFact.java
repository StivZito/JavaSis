/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Progrmador3
 */
public class LeerSaiFact  extends LeerSai {
   private static String numero;
   private static String tipo;
   private static String empresa;
   private static String sucursal;
   private static String factura;

  
    public static String getNumero() {
        return numero;
    }
    
    public static String getTipo() {
        return tipo;
    }
    
    public static String getEmpresa() {
        return empresa;
    }
    public static String getSucursal() {
        return sucursal;
    }
    
    public static void setNumero(String numero) {
        LeerSaiFact.numero = numero;
    }
    public static void setTipo(String tipo) {
        LeerSaiFact.tipo = tipo;
    }
    public static void setEmpresa(String empresa) {
        LeerSaiFact.empresa = empresa;
    }
    public static void setSucursal(String sucursal) {
        LeerSaiFact.sucursal = sucursal;
    }

    public static String getFactura() {
        return factura;
    }

    public static void setFactura(String factura) {
        LeerSaiFact.factura = factura;
    }
    
    
    public  LeerSaiFact (){
        LeerSai lsai = new  LeerSai();
        String linea="";
        List<String> lst = new ArrayList();     
        int i=0;    
        try {
         
            File f = new File("C:\\sai_win\\FEUPW.ini");   
        
       
        if(f.exists()){
          if(f.length() > 0){
             try {
                    FileInputStream archivo = new FileInputStream("C:\\sai_win\\FEUPW.ini");
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
                    String numero = "(Numero|numero|NUMERO)";
                    Pattern patnumero = Pattern.compile(numero);
                    String tipo = "(Tipo|tipo|TIPO)";
                    Pattern pattipo = Pattern.compile(tipo);
                    String empresa ="(CODEMP|codemp|Codemp)";
                    Pattern patempresa = Pattern.compile(empresa);
                    String sucursal = "(CODSUC|codsuc|Codsuc)";
                    Pattern patsucursal = Pattern.compile(sucursal);
                    String factura = "(FACTURA|factura|Factura)";
                    Pattern patfactura = Pattern.compile(factura);

                    for(int x=0; x < count; x++){     
                        String evalua =izqCadena(lst.get(x));
                        Matcher matser = patnumero.matcher(evalua.trim());
        //                System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                        if(matser.matches()){
                            String datser= derCadena(lst.get(x).toString());
        //                     System.out.println(datser);
        //                     numero = datser;
                            setNumero(datser);
                        }
                    }
                    for(int x=0; x < count; x++){     
                        String evalua =izqCadena(lst.get(x));
                        Matcher matser = pattipo.matcher(evalua.trim());
        //                System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                        if(matser.matches()){
                            String datser= derCadena(lst.get(x).toString());
        //                     System.out.println(datser);
        //                     tipo = datser;
                            setTipo(datser);
                        }
                    }
                    for(int x=0; x < count; x++){     
                        String evalua =izqCadena(lst.get(x));
                        Matcher matser = patempresa.matcher(evalua.trim());
        //                System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                        if(matser.matches()){
                            String datser= derCadena(lst.get(x).toString());
        //                     System.out.println(datser);
        //                     empresa = datser;
                            setEmpresa(datser);
                        }
                    }
                    for(int x=0; x < count; x++){     
                        String evalua =izqCadena(lst.get(x));
                        Matcher matser = patsucursal.matcher(evalua.trim());
        //                System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                        if(matser.matches()){
                            String datser= derCadena(lst.get(x).toString());
        //                     System.out.println(datser);
        //                     sucursal = datser;
                            setSucursal(datser);
                        }
                    }
                    for(int x=0; x < count; x++){     
                        String evalua =izqCadena(lst.get(x));
                        Matcher matser = patfactura.matcher(evalua.trim());
        //                System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
                        if(matser.matches()){
                            String datser= derCadena(lst.get(x).toString());
        ////                     System.out.println(datser);
        //                     factura = datser;
                            setFactura(datser);
                        }
                    }
                    }catch(Exception ex){
                                System.out.println("Error: "+ex.getMessage().toString());      
                    }// trycatch
          } else{
//             JOptionPane.showMessageDialog(null,"El Archivo FEUPW.ini EXISTE  \n"+
//                                                "Pero esta vacio, Solicite ayuda a Sistema!  ","Aviso", JOptionPane.INFORMATION_MESSAGE);
             //System.exit(0);
          
          }
       }else{
        
          JOptionPane.showMessageDialog(null,"No Existe el archivo: FEUPW.ini  \n"+
                                             "Debe crearlo en la ruta: C:\\sai_win ","Aviso", JOptionPane.INFORMATION_MESSAGE);
          //System.exit(0);
         
       }
        
     } catch (Exception e) {
          //System.exit(0);
         
     }   
    
    }// constructor
    
    
//    public static void main(String[] args) {
//          LeerSai lsai = new  LeerSai();
//          String linea="";
//        List<String> lst = new ArrayList();     
//        int i=0;
//       
//        try {
//            FileInputStream archivo = new FileInputStream("C:\\sai_win\\FEUPW.ini");
//            DataInputStream entrada = new DataInputStream(archivo);
//            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
//            while((linea=buffer.readLine())!=null){  
//                if(!(linea.isEmpty())){
//                     String filtro = escogerLinea(linea);
//                     if(!filtro.isEmpty()){       
//                          
////                             System.out.println("["+  i  +"]" + filtro);
//                             i++;
//                             lst.add(filtro);
//                          
//                  }
//                 
//                }//if
//            }// while           
//            entrada.close();
//           // ----------------------------------------------------------------------
//            int count =lst.size();
//            String numero = "(Numero|numero|NUMERO)";
//            Pattern patnumero = Pattern.compile(numero);
//            String tipo = "(Tipo|tipo|TIPO)";
//            Pattern pattipo = Pattern.compile(tipo);
//            String empresa ="(CODEMP|codemp)";
//            Pattern patempresa = Pattern.compile(empresa);
//            String sucursal = "(CODSUC|codsuc)";
//            Pattern patsucursal = Pattern.compile(sucursal);
//            String factura = "(FACTURA|factura|Factura)";
//            Pattern patfactura = Pattern.compile(factura);
//            
//            for(int x=0; x < count; x++){     
//                 String evalua =izqCadena(lst.get(x));
//                 Matcher matser = patnumero.matcher(evalua.trim());
//                //System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
//                 if(matser.matches()){
//                     String datser= derCadena(lst.get(x).toString());
////                     System.out.println(datser);
////                     numero = datser;
//                     setNumero(datser);
//                  }
//             }
//            for(int x=0; x < count; x++){     
//                 String evalua =izqCadena(lst.get(x));
//                 Matcher matser = pattipo.matcher(evalua.trim());
//                //System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
//                 if(matser.matches()){
//                     String datser= derCadena(lst.get(x).toString());
////                     System.out.println(datser);
////                     tipo = datser;
//                     setTipo(datser);
//                  }
//             }
//            for(int x=0; x < count; x++){     
//                 String evalua =izqCadena(lst.get(x));
//                 Matcher matser = patempresa.matcher(evalua.trim());
//                //System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
//                 if(matser.matches()){
//                     String datser= derCadena(lst.get(x).toString());
////                     System.out.println(datser);
////                     empresa = datser;
//                     setEmpresa(datser);
//                  }
//             }
//            for(int x=0; x < count; x++){     
//                 String evalua =izqCadena(lst.get(x));
//                 Matcher matser = patsucursal.matcher(evalua.trim());
//                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
//                 if(matser.matches()){
//                     String datser= derCadena(lst.get(x).toString());
////                     System.out.println(datser);
////                     sucursal = datser;
//                     setSucursal(datser);
//                  }
//             }
//            for(int x=0; x < count; x++){     
//                 String evalua =izqCadena(lst.get(x));
//                 Matcher matser = patfactura.matcher(evalua.trim());
//                // System.out.println("["+ x +"]"+izqCadena(lst.get(x)));
//                 if(matser.matches()){
//                     String datser= derCadena(lst.get(x).toString());
////                     System.out.println(datser);
////                     factura = datser;
//                     setFactura(datser);
//                  }
//             }
//             
//            
//            
//
//        }catch(Exception ex){
//        
//        
//        
//        }// trycatch
//    
//      
//    }
 
}// clase
