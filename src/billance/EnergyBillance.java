/*
 * Copyright 2017 zikmuto2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package billance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author zikmuto2
 */
public class EnergyBillance {
    Tariff tariff;
    Date periodFrom;
    Date periodTo;
    Flat flat;
    Date nearestFrom;
    Date nearestTo;
    int waterBeg;
    int waterEnd;
    int water;
    int vtBeg;
    int vtEnd;
    int vt;
    int ntBeg;
    int ntEnd;
    int nt;
    List<HeatConsumptionRow> heating;
    int days;
    float months;
    
    private EnergyBillance(){
    }
    
    public static EnergyBillance loadMeasures(Date from, Date to, Flat flat, Tariff tariff){
        EnergyBillance m = new EnergyBillance();
        m.periodFrom = from;
        m.periodTo = to;
        m.days = (int)((m.periodTo.getTime()-m.periodFrom.getTime())/1000/86400);
        m.months = (float) (((float)m.days)/365.25*12);
        m.flat = flat;
        m.findNearestDates();
        m.tariff = tariff;
        if(m.nearestFrom == null || m.nearestTo == null) return null;
        try {
            m.loadValues();
            m.loadHeatConsumption();
        } catch (SQLException ex) {
            Logger.getLogger(EnergyBillance.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ParseException ex) {
            Logger.getLogger(EnergyBillance.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        m.countConsumption();
        
        return m;
    }
    
    private void findNearestDates(){
        nearestFrom = Database.getInstance().findNearestMeasureDate(periodFrom);
        nearestTo = Database.getInstance().findNearestMeasureDate(periodTo);
    }
    
    private void loadValues() throws SQLException{
        ResultSet result = Database.getInstance().findMeasure(nearestFrom);
        result.next();
        waterBeg = result.getInt(flat.getWM());
        vtBeg = result.getInt(flat.getVT());
        ntBeg = result.getInt(flat.getNT());
        
        result = Database.getInstance().findMeasure(nearestTo);
        result.next();
        waterEnd = result.getInt(flat.getWM());
        vtEnd = result.getInt(flat.getVT());
        ntEnd = result.getInt(flat.getNT());
    }
    
    private void loadHeatConsumption() throws SQLException, ParseException{
        ResultSet result = Database.getInstance().findHeatConsumption(nearestFrom,nearestTo);
        heating = new LinkedList<>();
        DateFormat df = Database.getDateFormat();
        while(result.next()) {
            HeatConsumptionRow hcr = new HeatConsumptionRow();
            hcr.from = df.parse(result.getString("from"));
            hcr.to = df.parse(result.getString("to"));
            hcr.gas = result.getInt("gas");
            hcr.tm_sum = result.getInt("sum");
            hcr.tm = result.getInt(flat.getTM());
            hcr.countFraction();
            heating.add(hcr);
        }
    }
    
    private double getGasEnergy(int volumeGas) {
        return ((double)volumeGas)*tariff.getVolumeCoeficient()*tariff.getCombustionHeat();
    }
    
    private void countConsumption() {
        water = waterEnd-waterBeg;
        vt = vtEnd - vtBeg;
        nt = ntEnd - ntBeg;
    }
    
    public MeasuresTableModel getMeasuresTableModel(){
        return new MeasuresTableModel();
    }
    
    public HeatTableModel getHeatTableModel(){
        return new HeatTableModel();
    }
    
    static class HeatConsumptionRow {
        Date from;
        Date to;
        int gas;
        int tm;
        int tm_sum;
        float fraction;
        public void countFraction(){
            fraction = (tm_sum > 0)?(float)tm/((float)tm_sum):0;
        }
    }
    
    public class MeasuresTableModel extends AbstractTableModel{
        private final String[] columnNames = {ResourceBundle.getBundle("billance/Services").getString("serviceName"),
            ResourceBundle.getBundle("billance/Services").getString("serviceFrom"),
            ResourceBundle.getBundle("billance/Services").getString("serviceTo"),
            ResourceBundle.getBundle("billance/Services").getString("serviceUnit"),
            ResourceBundle.getBundle("billance/Services").getString("serviceBegin"),
            ResourceBundle.getBundle("billance/Services").getString("serviceEnd"),
            ResourceBundle.getBundle("billance/Services").getString("serviceConsumption")};
        private final String[] services = {ResourceBundle.getBundle("billance/Services").getString("water"),
            ResourceBundle.getBundle("billance/Services").getString("electricityVT"),
            ResourceBundle.getBundle("billance/Services").getString("electricityNT"),
            ResourceBundle.getBundle("billance/Services").getString("electricityFee"),
            ResourceBundle.getBundle("billance/Services").getString("heat"),
            ResourceBundle.getBundle("billance/Services").getString("monthlyFee")};
        private final String[] units = {ResourceBundle.getBundle("billance/Services").getString("waterUnit"),
            ResourceBundle.getBundle("billance/Services").getString("electricityVTUnit"),
            ResourceBundle.getBundle("billance/Services").getString("electricityNTUnit"),
            ResourceBundle.getBundle("billance/Services").getString("electricityFeeUnit"),
            ResourceBundle.getBundle("billance/Services").getString("heatUnit"),
            ResourceBundle.getBundle("billance/Services").getString("monthlyFeeUnit")};
        
        private final DateFormat dateFormat = new SimpleDateFormat(ResourceBundle.getBundle("billance/Services").getString("dateFormat"));
        private final DecimalFormat floatFormat = new DecimalFormat("#.###");
        /*private Measures measures;
        
        public MeasuresTableModel(Measures measures){
            this.measures = measures;
        }*/
        public MeasuresTableModel(){}
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public int getRowCount() {
            return services.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        
            switch(columnIndex){
                case 0:
                    return services[rowIndex];
                case 1: // from
                    if(rowIndex == 3 || rowIndex == 5) 
                        return dateFormat.format(periodFrom);
                    else 
                        return dateFormat.format(nearestFrom);
                case 2: // to
                    if(rowIndex == 3 || rowIndex == 5) 
                        return dateFormat.format(periodTo);
                    else 
                        return dateFormat.format(nearestTo);
                case 3: 
                    return units[rowIndex];
                case 4: // begin
                    switch(rowIndex){
                        case 0: 
                            return waterBeg;
                        case 1:
                            return vtBeg;
                        case 2:
                            return ntBeg;
                        case 4:
                            return "";
                        case 3:
                        case 5:
                            return dateFormat.format(periodFrom);
                    }
                case 5: // end
                    switch(rowIndex){
                        case 0: 
                            return waterEnd;
                        case 1:
                            return vtEnd;
                        case 2:
                            return ntEnd;
                        case 4:
                            return "";
                        case 3:
                        case 5:
                            return dateFormat.format(periodTo);
                    }
                case 6: // consumption
                    switch(rowIndex){
                        case 0: 
                            return water;
                        case 1:
                            return vt;
                        case 2:
                            return nt;
                        case 4:
                            return "";
                        case 3:
                        case 5:
                            return floatFormat.format(months); //String.format(%.3f, months);
                    }
                    return "";

            }
            return null;
        }
    }
    
    public class HeatTableModel extends AbstractTableModel{
        private final String[] columnNames = {ResourceBundle.getBundle("billance/Services").getString("heatFrom"),
            ResourceBundle.getBundle("billance/Services").getString("heatTo"),
            ResourceBundle.getBundle("billance/Services").getString("heatConsumption"),
            ResourceBundle.getBundle("billance/Services").getString("heatConsumptionSum"),
            ResourceBundle.getBundle("billance/Services").getString("heatFraction"),
            ResourceBundle.getBundle("billance/Services").getString("heatGasConsumption"),
            ResourceBundle.getBundle("billance/Services").getString("heatGasConsumptionSum")};
        
        private final DateFormat dateFormat = new SimpleDateFormat(ResourceBundle.getBundle("billance/Services").getString("dateFormat"));
        private final DecimalFormat fractionFormat = new DecimalFormat("#.# %");
        private final DecimalFormat floatFormat = new DecimalFormat("#.###");
        /*private Measures measures;
        
        public MeasuresTableModel(Measures measures){
            this.measures = measures;
        }*/
        public HeatTableModel(){}
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public int getRowCount() {
            return heating.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HeatConsumptionRow hcr = heating.get(rowIndex);
            switch(columnIndex){
                case 0:
                    return dateFormat.format(hcr.from);
                case 1:
                    return dateFormat.format(hcr.to);
                case 2:
                    return hcr.tm;
                case 3:
                    return hcr.tm_sum;
                case 4:
                    return fractionFormat.format(hcr.fraction);
                case 5:
                    return floatFormat.format(hcr.gas*hcr.fraction);
                case 6:
                    return hcr.gas;
                    

            }
            return null;
        }
    }
}
