/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.modelo.modelo;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rolando
 */
@XmlType(propOrder = {
    "ambiente", "tipoEmision", "razonSocial", "nombreComercial", "ruc", 
    "claveAcceso", "codDoc", "estab", "ptoEmi", "secuencial", "dirMatriz"})
public class InfoTributaria {

    protected String ambiente;
    protected String tipoEmision;
    protected String razonSocial;
    protected String nombreComercial;
    protected String ruc;
    protected String claveAcceso;
    protected String codDoc;
    protected String estab;
    protected String ptoEmi;
    protected String secuencial;
    protected String dirMatriz;

    public InfoTributaria() {
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String value) {
        ambiente = value;
    }

    public String getTipoEmision() {
        return tipoEmision;
    }

    public void setTipoEmision(String value) {
        tipoEmision = value;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String value) {
        razonSocial = value;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String value) {
        nombreComercial = value;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String value) {
        ruc = value;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String value) {
        claveAcceso = value;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String value) {
        codDoc = value;
    }

    public String getEstab() {
        return estab;
    }

    public void setEstab(String value) {
        estab = value;
    }

    public String getPtoEmi() {
        return ptoEmi;
    }

    public void setPtoEmi(String value) {
        ptoEmi = value;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String value) {
        secuencial = value;
    }

    public String getDirMatriz() {
        return dirMatriz;
    }

    public void setDirMatriz(String value) {
        dirMatriz = value;
    }
}
