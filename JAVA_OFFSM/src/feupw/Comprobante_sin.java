/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

/**
 *
 * @author Progrmador3
 */
public class Comprobante_sin {
    String comprobante;
    int cero=0;

    
    Comprobante_sin(){};
    
    public String Sacar_etiqueta(String aux){
        String comp="";
        comp=aux;
        comprobante=comp.substring(38, comp.length());
//        System.out.println("NUMERO: ["+ cero++  +" ] " +comprobante);
//        System.out.println("\n \n ======================================================================================"); 
//  
        return comprobante;
    
    }
    
   
    
}
