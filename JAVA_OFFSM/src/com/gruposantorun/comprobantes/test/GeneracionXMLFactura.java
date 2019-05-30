/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.test;

import com.gruposantorun.comprobantes.modelo.modelo.*;
import com.gruposantorun.comprobantes.modelo.modelo.factura.Factura;
import com.gruposantorun.comprobantes.modelo.modelo.factura.FacturaDetalle;
import com.gruposantorun.comprobantes.modelo.modelo.factura.InfoFactura;
import com.gruposantorun.comprobantes.modelo.modelo.factura.TotalImpuesto;
import com.gruposantorun.comprobantes.util.XMLUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Programador3
 */
public class GeneracionXMLFactura {
/*
 */
    public static void main(String[] args) {
        Factura f = new Factura();
        f.setVersion("1.1.0");
        f.setId("comprobante");
        
        InfoTributaria it = new InfoTributaria();
        it.setAmbiente("1");
        it.setTipoEmision("1");
        it.setRazonSocial("FASINARM");
        it.setNombreComercial("FASINARM");
        it.setRuc("0992635509001");
        it.setClaveAcceso("3005201401099263550900110010010001138611234567819");
        it.setCodDoc("01");
        it.setEstab("001");
        it.setPtoEmi("001");
        it.setSecuencial("000113861");
        it.setDirMatriz("CDLA.KENNEDY NORTE AV. MIGUEL H. ALCIVAR");
        f.setInfoTributaria(it);
        
        InfoFactura iff = new InfoFactura();
        iff.setFechaEmision("02/06/2014");
        iff.setDirEstablecimiento("CDLA.KENNEDY NORTE AV. MIGUEL H. ALCIVA");
        iff.setObligadoContabilidad("SI");
        iff.setTipoIdentificacionComprador("04");
        iff.setRazonSocialComprador("3M ECUADOR C.A.");
        iff.setIdentificacionComprador("1790017478001");
        iff.setTotalSinImpuestos(new BigDecimal("3"));
        iff.setTotalDescuento(new BigDecimal("6"));
        iff.setPropina(new BigDecimal("0"));
        iff.setImporteTotal(new BigDecimal("0"));
        iff.setMoneda("0");
        
        List<TotalImpuesto> lst = new ArrayList<>();
        
        TotalImpuesto t1 = new TotalImpuesto();
        t1.setCodigo("1");
        t1.setCodigoPorcentaje("1");
        t1.setBaseImponible(BigDecimal.ZERO);
        t1.setTarifa(BigDecimal.ZERO);
        t1.setValor(BigDecimal.ZERO);
        
        TotalImpuesto t2 = new TotalImpuesto();
        t2.setCodigo("2");
        t2.setCodigoPorcentaje("1");
        t2.setBaseImponible(BigDecimal.ZERO);
        t2.setTarifa(BigDecimal.ZERO);
        t2.setValor(BigDecimal.ZERO);
        lst.add(t1);
        lst.add(t2);
        
        iff.setTotalImpuesto(lst);
        f.setInfoFactura(iff);
       
        List<FacturaDetalle> detalle = new ArrayList();
        FacturaDetalle fd1 = new FacturaDetalle();
        fd1.setCodigoPrincipal("CM094");
        fd1.setCodigoAuxiliar("CM094");
        fd1.setDescripcion("DIARIO DEL BEBE");
        fd1.setCantidad(new BigDecimal("1"));
        fd1.setPrecioUnitario(new BigDecimal("10.00"));
        fd1.setDescuento(new BigDecimal("0"));
        fd1.setPrecioTotalSinImpuesto(new BigDecimal("10.00"));
        
        List<DetAdicional> lsta = new ArrayList();
        DetAdicional dea = new  DetAdicional();
        dea.setNombre("aux");
        dea.setValor("0");
        lsta.add(dea);
        fd1.setDetAdicional(lsta);
        
        List<Impuesto> lstimp = new ArrayList();
        Impuesto  imp = new Impuesto();
        imp.setCodigo("2");
        imp.setCodigoPorcentaje("2");
        imp.setTarifa(new BigDecimal("12"));
        imp.setBaseImponible( new BigDecimal("13.50"));
        imp.setValor(new BigDecimal("1.62"));
        lstimp.add(imp);
        fd1.setImpuesto(lstimp);
        InfoAdicional  infad = new InfoAdicional();
        List<CampoAdicional> lstifa = new ArrayList();
        CampoAdicional cmpad = new CampoAdicional();
        cmpad.setNombre("washington");
        cmpad.setValue("12");
        lstifa.add(cmpad);
        infad.setCampoAdicional(lstifa);
        f.setInfoAdicional(infad);
         
        detalle.add(fd1);
        f.setDetalle(detalle);
       
        
        
        
          
        

        XMLUtil.marshall(f, "D:\\factura.xml");
 
    }
}
