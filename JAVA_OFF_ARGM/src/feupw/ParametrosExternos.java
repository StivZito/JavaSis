/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
/**
 *
 * @author Progrmador3
 */
public class ParametrosExternos {

    pruebanet pb = new pruebanet();
    Connection con = null;
    ResultSet rs = null;
    CallableStatement cst = null;
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
            System.out.println("Error No se puede conectar : " + e.getMessage());
            System.exit(0);
        }

        certificado=getRutaCertificado();
        serv2 = certificado.replace("\\", ",");  
        auxserv1 = serv2.split(",");
        canti=auxserv1.length;
        certificado="";
         for (int j=0;  j < canti ; j++){
          if( j < canti-1) {   
              certificado+=auxserv1[j]+"\\"+"\\";
          }
         
          if( j == canti-1) {            
             certificado+=auxserv1[j]; 
          }
         
         }
        
        setRutaCertificado(certificado);   
        rutaAutxml = getRutaXmlAutorizado();
        rutaAutxml=rutaAutxml.substring(2,rutaAutxml.length() );
        String serv3 = rutaAutxml.replace("\\", ",");
        auxserv = serv3.split(",");
        cant=auxserv.length;
        rutaAutxml="\\\\\\\\";
        for (int i=0; i<cant ; i++){
         if( i < cant-1) {   
           rutaAutxml+=auxserv[i]+"\\"+"\\";           
         }
         
         if( i == cant-1) {
           rutaAutxml+=auxserv[i]; 
         }
         
        }

        setRutaXmlAutorizado(rutaAutxml.trim().toString());    
    }      
}
