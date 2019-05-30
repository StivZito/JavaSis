/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.seguridad;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import org.w3c.dom.Document;

/**
 *
 * @author Programador3
 */
public class Signer extends GenericXMLSignature {

    private String resourceToSing;
    private String resourceSigned;

    public Signer(String resourceToSing, String resourceSigned) {
        this.resourceToSing = resourceToSing;
        this.resourceSigned = resourceSigned;
    }
    
    public void parSignGen ( String cert, String clave ){
      GenericXMLSignature.PKCS12_PASSWORD = clave;
      GenericXMLSignature.PKCS12_RESOURCE = cert;
   } 

    @Override
    protected DataToSign createDataToSign() {
        DataToSign dataToSign = new DataToSign();
        try {
            dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
            dataToSign.setEsquema(XAdESSchemas.XAdES_132);
            dataToSign.setXMLEncoding("UTF-8");
            dataToSign.setEnveloped(true);
            dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "Documento de ejemplo", null, "text/xml", null));
            Document docToSign = getDocument(resourceToSing);
            dataToSign.setDocument(docToSign);
        } catch (Exception ex) {
            dataToSign = null;
            ex.printStackTrace();
        } finally {
            return dataToSign;
        }
    }

    @Override
    protected String getSignatureFileName() {
        return resourceSigned;
    }

    public void firmarFactura() {
        execute();
    }

    public String getResourceSigned() {
        return resourceSigned;
    }

    public void setResourceSigned(String resourceSigned) {
        this.resourceSigned = resourceSigned;
    }

    public String getResourceToSing() {
        return resourceToSing;
    }

    public void setResourceToSing(String resourceToSing) {
        this.resourceToSing = resourceToSing;
    }

//    public static void main(String[] args) {
//        Signer s = new Signer("D:\\Factura.xml ","D:\\FacturaF\\FacturaFirmado.xml");
//        s.firmarFactura();
//    }
}
