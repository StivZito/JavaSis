/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gruposantorun.comprobantes.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Programador3
 */
public class ArchivoUtil {

    public static byte[] convertirArchivoAByteArray(File file) throws IOException {
        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException("EOF reached while trying to read the whole file");
            }
        } catch (Exception ex) {
            buffer = null;
        } finally {
            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return buffer;
    }
}
