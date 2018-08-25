/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Acer
 */
public class TexExport implements BillanceExporter
{

    private EnergyBillance billance;
    private DocumentPreambule preambule;
    private File file;

    public TexExport(EnergyBillance billance, DocumentPreambule preambule)
    {
        this(billance, preambule, new File("export.tex"));
    }

    public TexExport(EnergyBillance billance, DocumentPreambule preambule, File file)
    {
        this.billance = billance;
        this.preambule = preambule;
        this.file = file;
    }

    @Override
    public void export()
    {
        //try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))
        {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            DecimalFormat ff = new DecimalFormat("0.000");
            DecimalFormat sf = new DecimalFormat("#,##0.0");
            DecimalFormat cf = new DecimalFormat("#,##0.00");
            writer.write(defCommand("doklad", preambule.docNumber));
            writer.write(defCommand("smlouva", preambule.contract));
            writer.write(defCommand("byt", Integer.toString(billance.getFlat().number)));
            writer.write(defCommand("sod", df.format(billance.getBegin())));
            writer.write(defCommand("sdo", df.format(billance.getEnd())));
            writer.write(defCommand("komu", preambule.persons));
            writer.write(defCommand("dne", df.format(preambule.issue)));
            writer.write(defCommand("splatnost", df.format(preambule.due)));
            writer.write(defCommand("mesBegin", df.format(billance.nearestFrom)));
            writer.write(defCommand("mesEnd", df.format(billance.nearestTo)));
            writer.write(defCommand("combustionHeat", ff.format(billance.getTariff().getCombustionHeat())));
            writer.write(defCommand("volumeCoef", ff.format(billance.getTariff().getVolumeCoeficient())));
            writer.write(defCommand("flatCoef", ff.format(billance.getFlat().getFlatCoef())));
            writer.write(defCommand("gasCons", sf.format(billance.getGasConsumption())));
            writer.write(defCommand("heatCons", sf.format(billance.getMeasuredHeatConsumption())));
            writer.write(defCommand("commonHeatCons", sf.format(billance.getCommonHeat())));
            writer.write(defCommand("basicPart", sf.format(billance.getBasicHeatPart())));
            writer.write(defCommand("consPart", sf.format(billance.getConsumptionHeatPart())));
            writer.write(defCommand("heating", sf.format(billance.getHeatingEnergy())));
            writer.write(defCommand("totalCosts", cf.format(billance.getTotalCosts())));
            writer.write(defCommand("depositField", cf.format(billance.getDeposit())));
            writer.write(defCommand("balanceField", cf.format(billance.getBillance())));
            writer.write(defCommand(billance.isOverpaid() ? "overpaid" : "notoverpaid", "1"));
            writer.write(defCommand(billance.isOverpaid() ? "preplatek" : "nedoplatek", cf.format(Math.abs(billance.getBillance()))));

            /*
            
        combustionHeat.setText(floatFormat.format(billance.getTariff().getCombustionHeat()));
        volumeCoef.setText(floatFormat.format(billance.getTariff().getVolumeCoeficient()));
        flatCoef.setText(floatFormat.format(billance.getFlat().getFlatCoef()));
        gasCons.setText(floatShortFormat.format(billance.getGasConsumption())+" m3");
        heatCons.setText(floatShortFormat.format(billance.getMeasuredHeatConsumption())+" kWh");
        commonHeatCons.setText(floatShortFormat.format(billance.getCommonHeat())+" kWh");
        basicPart.setText(floatShortFormat.format(billance.getBasicHeatPart())+" kWh");
        consPart.setText(floatShortFormat.format(billance.getConsumptionHeatPart())+" kWh");
        heating.setText(floatShortFormat.format(billance.getHeatingEnergy())+" kWh");
        totalCosts.setText(currencyFormat.format(billance.getTotalCosts()));
        depositField.setText(currencyFormat.format(billance.getDeposit()));
        balanceField.setText(((billance.isOverpaid()?"+":""))+currencyFormat.format(billance.getBillance()));
             */
            writer.write(defTableContent("servicesTable", billance.getServicesTableModel()));
            writer.write(defTableContent("heatingTable", billance.getHeatTableModel()));
            writer.write(defTableContent("summaryTable", billance.getSummaryTableModel()));
            writer.write(defTableContent("exServiceTable", billance.getExtendedServicesTableModel()));

            JOptionPane.showMessageDialog(null, "Vyúčtování bylo uloženo do souboru: \n" + file.getCanonicalPath(), "Soubor uložen", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "Soubor se nezdařilo uložit", "Chyba při ukládání", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TexExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String defCommand(String name, String val)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\\newcommand{\\").append(name).append("}{").append(val).append("}\n");

        return sb.toString();
    }

    public String defTableContent(String name, AbstractTableModel model)
    {
        int cols = model.getColumnCount();
        int rows = model.getRowCount();
        StringBuilder sb = new StringBuilder();
        sb.append("\\newcommand{\\").append(name).append("}{\n");
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                sb.append(model.getValueAt(i, j).toString());
                if (j == cols - 1)
                {
                    sb.append(" \\\\ \\hline\n");
                }
                else
                {
                    sb.append(" & ");
                }
            }
        }
        sb.append("}\n");

        return sb.toString().replaceAll("\\%", "\\\\\\%");
    }

    public static class DocumentPreambule
    {

        String docNumber;
        String contract;
        String persons;
        Date issue;
        Date due;

        public DocumentPreambule(String docNumber, String contract, String persons, Date issue, Date due)
        {
            this.docNumber = docNumber;
            this.contract = contract;
            this.persons = persons;
            this.issue = issue;
            this.due = due;
        }
    }
}
