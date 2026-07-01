package es.uhu.amc.amcp2.ModoGrafico;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SeleccionVentana extends JFrame {


    private File archivo;
    private boolean elegido;

    public SeleccionVentana() {

        JFileChooser chooser = new JFileChooser(new File("./src/main/ArchivosLectura/"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "*.txt", "txt","TXT");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this.getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            archivo=chooser.getSelectedFile();
            elegido = true;
        }
        else{
            elegido=false;
        }
    }

    public boolean isElegido() {
        return elegido;
    }

    public String getArchivo() {
        return archivo.toString();
    }


}
