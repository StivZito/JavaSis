/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

import javax.swing.JOptionPane;

/**
 *
 * @author Progrmador3
 */
public class FEUPW {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            MenuMain mn = new MenuMain();
            //mn.setVisible(true);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "No hay datos en el archvio FEUPW.ini", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("Error: "+ e.getMessage().toString());
            System.exit(0);

        }

    }
    // TODO code application logic here
}
