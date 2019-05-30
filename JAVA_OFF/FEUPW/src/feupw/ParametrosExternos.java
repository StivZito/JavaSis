/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Progrmador3
 */
public class ParametrosExternos {

    pruebanet pb = new pruebanet();
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
    //String rutaJar = "";
    String rutaCertificado = "";
    String claveAcceso = "";
    String rutaXmlAutorizado = "";
    String tipoAmbiente = "";
    String empresa ="";
    String sucursal ="";
    int cant  = 0;
    int canti = 0;

   

    public String getRutaCertificado() {
        return rutaCertificado;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public String getRutaXmlAutorizado() {
        return rutaXmlAutorizado;
    }

    public String getTipoAmbiente() {
        return tipoAmbiente;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public void setRutaCertificado(String rutaCertificado) {
        this.rutaCertificado = rutaCertificado;
    }

    public void setRutaXmlAutorizado(String rutaXmlAutorizado) {
        this.rutaXmlAutorizado = rutaXmlAutorizado;
    }

    public void setTipoAmbiente(String tipoAmbiente) {
        this.tipoAmbiente = tipoAmbiente;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
    public ParametrosExternos (){}
    
    public void Parametros(String codemp, String codsuc) {
        String certificado="",rutaAutxml="";
        String[] auxserv = {};
        String[] auxserv1 = {};
        String serv2="";
        

        try {

            con = pb.getConexion();
            cst = con.prepareCall("{call buscar_parametros_externos_xml(?,?)}");
            cst.setString(1, codemp);
            cst.setString(2, codsuc);
            rs = cst.executeQuery();
            if (rs.next()) {
                setRutaCertificado(rs.getString("rutaCertificado").trim().toString());
                setClaveAcceso(rs.getString("claveCertificado").trim().toString());
                setRutaXmlAutorizado(rs.getString("rutaXmlAutWeb").trim().toString());
                setTipoAmbiente(rs.getString("tipoAmbiente").trim().toString());
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error No se puede conectar : " + e.getMessage() , "Aviso", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        }

        certificado=getRutaCertificado();
        serv2 = certificado.replace("\\", ",");
        //System.out.println("no:"+serv2);    
        auxserv1 = serv2.split(",");
        canti=auxserv1.length;
        //System.out.println("CANTI: "+canti);
        certificado="";
         for (int j=0;  j < canti ; j++){
          if( j < canti-1) {   
              certificado+=auxserv1[j]+"\\"+"\\";
              //System.out.println("indice:"+j +' ' + certificado);
          }
         
          if( j == canti-1) {
            
             certificado+=auxserv1[j]; 
            ///System.out.println("indice2:"+j+' ' + certificado); 
          }
         
         }
       //certificado=auxserv[0]+"\\"+"\\"+auxserv[1]+"\\"+"\\"+auxserv[2];
        
        setRutaCertificado(certificado);
       // System.out.println("cert: "+certificado);
        
        rutaAutxml = getRutaXmlAutorizado();
        rutaAutxml=rutaAutxml.substring(2,rutaAutxml.length() );
        String serv3 = rutaAutxml.replace("\\", ",");
        auxserv = serv3.split(",");
        cant=auxserv.length;
        //System.out.println("CANT: "+cant);
        rutaAutxml="\\\\\\\\";
        for (int i=0; i<cant ; i++){
         if( i < cant-1) {   
           rutaAutxml+=auxserv[i]+"\\"+"\\";
           
         }
         
         if( i == cant-1) {
           rutaAutxml+=auxserv[i]; 
         }
         
        }
        //rutaAutxml=auxserv[0]+"\\"+"\\"+auxserv[1]+"\\"+"\\"+auxserv[2]+"\\"+"\\"+auxserv[3];
        setRutaXmlAutorizado(rutaAutxml.trim().toString());
        //System.out.println("aut: "+rutaAutxml);
        
        
        
        


    }
    
//        public static void main(String[] args) {
//           ParametrosExternos p = new  ParametrosExternos();
//           p.Parametros("10", "10");
//           JOptionPane.showMessageDialog(null, "Clave Acceso: " + p.getClaveAcceso()+
//                                               "\ncertificado: " + p.getRutaCertificado()+
//                                               "\ntipoAmbiente: "+ p.getTipoAmbiente()+
//                                               "\nruta xml aut: "+p.getRutaXmlAutorizado()
//                                               , "Aviso", JOptionPane.INFORMATION_MESSAGE);
//          
//            
//            
//        }
        
        
}
