/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.test;

import com.gruposantorun.comprobantes.modelo.modelo.CampoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.Impuesto;
import com.gruposantorun.comprobantes.modelo.modelo.InfoAdicional;
import com.gruposantorun.comprobantes.modelo.modelo.InfoTributaria;
import com.gruposantorun.comprobantes.modelo.modelo.notadebito.InfoNotaDebito;
import com.gruposantorun.comprobantes.modelo.modelo.notadebito.Motivo;
import com.gruposantorun.comprobantes.util.ClaveAcceso;
import com.gruposantorun.comprobantes.util.XMLUtil;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Washington
 */
public class GeneracionXMLNotaDebito {
    
    
    
    public static void main(String[] args) {
             //txAut.setText("");
                     ClaveAcceso clv = new ClaveAcceso();
                     DefaultTableModel md1 = new DefaultTableModel();
                     String nomclave = "";
                    //cod =0.0;
                    //-------------------------------
                      File dir = new File("D:\\ComprobanteElectronicos\\NOTADEBITO\\notaDebitoGenerada\\");
                    //----------------------- generar
                    int fila = 0;
                    int paso = 0;
                    int tm = 0;
                    String clave_aux ="";
                    String auxfecha = "";
                    String nuevo = "";
                    String[] fec;
                    boolean tranferencia = false;
                    
        com.gruposantorun.comprobantes.modelo.modelo.notadebito.NotaDebito ntd = new com.gruposantorun.comprobantes.modelo.modelo.notadebito.NotaDebito();
                ntd.setVersion("1.0.0");
                ntd.setId("comprobante");
                //JOptionPane.showMessageDialog(this, "Cliente Econtrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                //--------------------------------------------------------------------------------------
                    clave_aux = clv.generarClaveAcceso("clave");
                   
                    InfoTributaria it = new InfoTributaria();
                        it.setAmbiente("");
                        it.setTipoEmision("");
                        it.setRazonSocial("");
                        it.setNombreComercial("");
                        it.setRuc("");
                        it.setClaveAcceso(clave_aux);
                        it.setCodDoc("05");
                        it.setEstab("");
                        it.setPtoEmi("");
                        it.setSecuencial("");
                        it.setDirMatriz("");
                  ntd.setInfoTributaria(it);

                  InfoNotaDebito ifr = new InfoNotaDebito();
                        ifr.setFechaEmision("");
                        ifr.setDirEstablecimiento("");
                        ifr.setTipoIdentificacionComprador("");
                        ifr.setRazonSocialComprador("");
                        ifr.setIdentificacionComprador("");
                        ifr.setContribuyenteEspecial("");
                        ifr.setObligadoContabilidad("");
                        ifr.setCodDocModificado("");
                        ifr.setNumDocModificado("");
                        ifr.setFechaEmisionDocSustento("");
                        ifr.setTotalSinImpuestos(BigDecimal.ZERO);
                                     
            //-----------------------------------------------------DETALLE DE LA NOTA DEBITO
                       double total = 0.0;
                       List<Impuesto> lstimp =  new ArrayList();
                       Impuesto imp = new Impuesto();
                       imp.setCodigo("");
                       imp.setCodigoPorcentaje("");
                       imp.setTarifa(BigDecimal.ZERO);
                       imp.setBaseImponible(BigDecimal.ZERO);
                       imp.setValor(BigDecimal.ZERO);
                       lstimp.add(imp);
                       ifr.setImpuesto(lstimp);
                       ifr.setValorTotal(BigDecimal.ZERO);
                   ntd.setInfoNotaDebito(ifr);   
                  //-----------------------------------------------------------------------
                       List<Motivo> lstmt =  new ArrayList();
                       Motivo  mt = new Motivo();
                       mt.setRazon("");
                       mt.setValor(BigDecimal.ZERO);
                       lstmt.add(mt);
                    ntd.setMotivo(lstmt);
                  //-----------------------------------------------------------------------   
                        InfoAdicional infad = new InfoAdicional();
                        List<CampoAdicional> lstifa = new ArrayList();
                        CampoAdicional cmpad1 = new CampoAdicional();
                        cmpad1.setNombre("Telefono");
                        cmpad1.setValue("");
                        CampoAdicional cmpad2 = new CampoAdicional();
                        cmpad2.setNombre("Direccion");
                        cmpad2.setValue("");
                        CampoAdicional cmpad3 = new CampoAdicional();
                        cmpad3.setNombre("Correo");
                        cmpad3.setValue("");
                        CampoAdicional cmpadp = new CampoAdicional();
                        cmpadp.setNombre("Programador");
                        cmpadp.setValue("Washington Carrillo S");
                        
                        lstifa.add(cmpad1);
                        infad.setCampoAdicional(lstifa);
                        ntd.setInfoAdicional(infad);

                 //--------------------------------------------------------------------------------------
                 if(dir.exists()){
                  System.out.println("existe D:\\ComprobanteElectronicos\\NOTADEBITO\\notaDebitoGenerada\\");
                  XMLUtil.marshall(ntd, "D:\\ComprobanteElectronicos\\NOTADEBITO\\notaDebitoGenerada\\" + clave_aux + ".xml");
                  tranferencia=true;  
                 } else { 
                  dir.mkdirs();
                  XMLUtil.marshall(ntd, "D:\\ComprobanteElectronicos\\NOTADEBITO\\notaDebitoGenerada\\" + clave_aux + ".xml");
                  JOptionPane.showMessageDialog(null, "Nota Debito en formato XML generado con Exito...!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                  tranferencia=true;         
                  }
        
    }
    
}
