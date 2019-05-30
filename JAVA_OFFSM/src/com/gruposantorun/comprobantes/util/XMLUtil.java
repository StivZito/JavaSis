/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.util;

import com.gruposantorun.comprobantes.modelo.modelo.factura.Factura;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 *
 * @author Programador3
 */
public class XMLUtil {
    public XMLUtil(){}
    public static String marshall(Object comprobante, String pathArchivoSalida) {
        String respuesta = null;
        try {
            JAXBContext context = JAXBContext.newInstance(new Class[]{comprobante.getClass()});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pathArchivoSalida), "UTF-8");
            marshaller.marshal(comprobante, out);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
        return respuesta;
    }

    public static Factura unmarshall(String pathArchivoSalida) {
        Factura factura = null;
        try {
            File file = new File(pathArchivoSalida);
            JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            factura = (Factura) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            factura = null;
            e.printStackTrace();
        } finally {
            return factura;
        }
    }

    public static String validarXML(File archivoXML, File archivoXSD) {
        String mensaje = null;
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        try {
            schema = schemaFactory.newSchema(archivoXSD);
        } catch (SAXException e) {
            throw new IllegalStateException("Existe un error en la sintaxis del esquema", e);
        }
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(archivoXML));
        } catch (Exception e) {
            return e.getMessage();
        }
        return mensaje;
    }
    
//    public static void main(String[] args) {
//        String mensajes = validarXML(new File("D:\\factura.xml"), new File("C:\\xsd\\factura.xsd"));
//        System.out.println(mensajes);
//    }
}
