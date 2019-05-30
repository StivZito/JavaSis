/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;  
/**
 * @Modifcado para facturacion electronica
 */
public class FEUPW {    
    
    public static void main(String[] args) {
        try {            
            
            LeerSai     ls  =  new LeerSai();
            LeerSaiFact lsf =  new LeerSaiFact();
            
            //---llenamos las variables con las que trabajara el sistema
            if (args.length > 0) {  
                ls.setServerp(args[0]);                
                ls.setLogid(args[1]); 
                ls.setLogopassword(args[2]);
                ls.setDatabase(args[3]);                
                lsf.setEmpresa(args[4]);
                lsf.setSucursal(args[5]);
                lsf.setTipo(args[6]);
                lsf.setNumero(args[7]);
                lsf.setFactura(args[8]);
                lsf.setRuc(args[9]);
            }            
             MenuNew mn = new MenuNew();
             
        } catch (Exception e) {
            System.out.println("Error OpenFeupw: " + e.getMessage());
            System.exit(0);
            
        }
       
    }
}
