/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Acer
 */
public class TexExport implements BillanceExporter{
    private EnergyBillance billance;
    private DocumentPreambule preambule;
    private File file;
    
    public TexExport(EnergyBillance billance, DocumentPreambule preambule){
        this(billance, preambule, new File("export.tex"));
    }
    public TexExport(EnergyBillance billance, DocumentPreambule preambule, File file){
        this.billance = billance;
        this.preambule = preambule;
        this.file = file;
    }

    @Override
    public void export() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            writer.write(defCommand("doklad", preambule.docNumber));
            writer.write(defCommand("smlouva", preambule.docNumber));
            writer.write(defCommand("byt", preambule.docNumber));
            writer.write(defCommand("sod", preambule.docNumber));
            writer.write(defCommand("sdo", preambule.docNumber));
            writer.write(defCommand("komu", preambule.docNumber));
            writer.write(defCommand("dne", preambule.docNumber));
            writer.write(defCommand("splatnost", preambule.docNumber));
            /*
            \newcommand{\doklad}{17001}

            \newcommand{\smlouva}{03/2015}
            \newcommand{\byt}{4}
            \newcommand{\sod}{01.11.2015}
            \newcommand{\sdo}{31.10.2016}
            \newcommand{\komu}{Dana Peko?ová, Filip Jiráska}
            \newcommand{\dne}{19. listopadu 2016}
            \newcommand{\splatnost}{19. prosince 2016}
            */
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Soubor se nezdařilo uložit", "Chyba při ukládání", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TexExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String defCommand(String name, String val){
        StringBuilder sb = new StringBuilder();
        sb.append("\\newcommand{\\").append(name).append("}{").append(val).append("}\n");

        return sb.toString();
    }
        
    public static class DocumentPreambule{
        String docNumber;
        String contract; 
        String persons; 
        Date issue; 
        Date due;
        public DocumentPreambule(String docNumber, String contract, String persons, Date issue, Date due){
            this.docNumber = docNumber;
            this.contract = contract; 
            this.persons = persons; 
            this.issue = issue; 
            this.due = due; 
        }
    }
}
