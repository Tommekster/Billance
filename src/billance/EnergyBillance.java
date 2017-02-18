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

import java.lang.reflect.Field;
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
    boolean includeEletricity;
    List<HeatConsumptionRow> heating;
    int days;
    float months;
    double gasFlat;
    int gasCommon;
    
    private EnergyBillance(){
    }
    
    public static EnergyBillance loadMeasures(Date from, Date to, Flat flat, Tariff tariff, boolean eletricity){
        EnergyBillance m = new EnergyBillance();
        m.periodFrom = from;
        m.periodTo = to;
        m.days = (int)((m.periodTo.getTime()-m.periodFrom.getTime())/1000/86400);
        m.months = (float) (((float)m.days)/365.25*12);
        m.flat = flat;
        m.findNearestDates();
        m.tariff = tariff;
        m.includeEletricity = eletricity;
        if(m.nearestFrom == null || m.nearestTo == null) return null;
        try {
            m.loadValues();
            m.loadHeatConsumption();
        } catch (SQLException | ParseException ex) {
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
        gasFlat = 0;
        gasCommon = 0;
        while(result.next()) {
            HeatConsumptionRow hcr = new HeatConsumptionRow();
            hcr.from = df.parse(result.getString("from"));
            hcr.to = df.parse(result.getString("to"));
            hcr.gas = result.getInt("gas");
            hcr.tm_sum = result.getInt("sum");
            hcr.tm = result.getInt(flat.getTM());
            hcr.countFraction();
            heating.add(hcr);
            
            gasFlat += hcr.gasFraction;
            gasCommon += hcr.gas;
        }
    }
    
    private double getGasEnergy(double volumeGas) {
        return volumeGas*tariff.getVolumeCoeficient()*tariff.getCombustionHeat();
    }
    
    private void countConsumption() {
        water = waterEnd-waterBeg;
        vt = vtEnd - vtBeg;
        nt = ntEnd - ntBeg;
    }
    
    public double getGasConsumption(){
        return gasFlat;
    }
    
    public double getMeasuredHeatConsumption(){
        return getGasEnergy(gasFlat);
    }
    
    public double getCommonHeat(){
        return getGasEnergy(gasCommon);
    }
    
    public double getBasicHeatPart(){
        return getCommonHeat()*flat.getFlatCoef()*tariff.surfaceCoef;
    }
    
    public double getConsumptionHeatPart(){
        return getMeasuredHeatConsumption()*(1-tariff.surfaceCoef);
    }
    
    public double getHeatingEnergy(){
        return getBasicHeatPart()+getConsumptionHeatPart();
    }
    
    public ServicesTableModel getServicesTableModel(){
        return new ServicesTableModel();
    }
    
    public HeatTableModel getHeatTableModel(){
        return new HeatTableModel();
    }
    
    public SummaryTableModel getSummaryTableModel(){
        return new SummaryTableModel();
    }

    Tariff getTariff() {
        return tariff;
    }

    Flat getFlat() {
        return flat;
    }
    
    static class HeatConsumptionRow {
        Date from;
        Date to;
        int gas;
        int tm;
        int tm_sum;
        float fraction;
        float gasFraction;
        public void countFraction(){
            fraction = (tm_sum > 0)?(float)tm/((float)tm_sum):0;
            gasFraction = gas*fraction;
        }
    }
    
    public class ServicesTableModel extends AbstractTableModel{
        private final String[] columnNames = {ResourceBundle.getBundle("billance/Services").getString("serviceName"),
            ResourceBundle.getBundle("billance/Services").getString("serviceFrom"),
            ResourceBundle.getBundle("billance/Services").getString("serviceTo"),
            ResourceBundle.getBundle("billance/Services").getString("serviceUnit"),
            ResourceBundle.getBundle("billance/Services").getString("serviceBegin"),
            ResourceBundle.getBundle("billance/Services").getString("serviceEnd"),
            ResourceBundle.getBundle("billance/Services").getString("serviceConsumption")};
        
        List<ServiceRow> rows = new LinkedList<>();
        {
            rows.add(new ServiceRow(ResourceBundle.getBundle("billance/Services").getString("water"),
                nearestFrom,nearestTo,
                ResourceBundle.getBundle("billance/Services").getString("waterUnit"),
                waterBeg,waterEnd,water));
            if(includeEletricity){
                rows.add(new ServiceRow(ResourceBundle.getBundle("billance/Services").getString("electricityVT"),
                    nearestFrom,nearestTo,
                    ResourceBundle.getBundle("billance/Services").getString("electricityVTUnit"),
                    vtBeg,vtEnd,vt));
                rows.add(new ServiceRow(ResourceBundle.getBundle("billance/Services").getString("electricityNT"),
                    nearestFrom,nearestTo,
                    ResourceBundle.getBundle("billance/Services").getString("electricityNTUnit"),
                    ntBeg,ntEnd,nt));
            }
            rows.add(new ServiceRow(ResourceBundle.getBundle("billance/Services").getString("heat"),
                nearestFrom,nearestTo,
                ResourceBundle.getBundle("billance/Services").getString("heatUnit"),
                "","",getHeatingEnergy()));
            rows.add(new ServiceRow(ResourceBundle.getBundle("billance/Services").getString("monthlyFee"),
                periodFrom,periodTo,
                ResourceBundle.getBundle("billance/Services").getString("monthlyFeeUnit"),
                periodFrom,periodTo,months));
        }
        
        private final DateFormat dateFormat = new SimpleDateFormat(ResourceBundle.getBundle("billance/Services").getString("dateFormat"));
        private final DecimalFormat floatFormat = new DecimalFormat("#.000");
        /*private Measures measures;
        
        public MeasuresTableModel(Measures measures){
            this.measures = measures;
        }*/
        public ServicesTableModel(){ }
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rows.get(rowIndex).getItem(columnIndex);
        }
        
        class ServiceRow{
            public String name;
            public Date from;
            public Date to;
            public String unit;
            public Object begin;
            public Object end;
            public Object consumption;
            public ServiceRow(String name,Date from,Date to,String unit,Object begin,Object end,Object consumption){
                this.name = name;
                this.from = from;
                this.to = to;
                this.unit = unit;
                this.begin = begin;
                this.end = end;
                this.consumption = consumption;
            }
            String getItem(int index){
                try {
                    Field field = this.getClass().getFields()[index];
                    Object val = field.get(this);
                    if(val instanceof Date)
                        return dateFormat.format(val);
                    if(val instanceof Integer)
                        return Integer.toString((int) val);
                    if(val instanceof Float)
                        return floatFormat.format(val);
                    if(val instanceof String)
                        return (String) val;
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(EnergyBillance.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "";
            }
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
        private final DecimalFormat fractionFormat = new DecimalFormat("0.0 %");
        private final DecimalFormat floatFormat = new DecimalFormat("0.000");
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
                    return floatFormat.format(hcr.gasFraction);
                case 6:
                    return hcr.gas;
            }
            return null;
        }
    }
    
    public class SummaryTableModel extends AbstractTableModel{
        private final String[] columnNames = {ResourceBundle.getBundle("billance/Services").getString("summaryName"),
            ResourceBundle.getBundle("billance/Services").getString("summaryAmount"),
            ResourceBundle.getBundle("billance/Services").getString("summaryUnit"),
            ResourceBundle.getBundle("billance/Services").getString("summaryCost"),
            ResourceBundle.getBundle("billance/Services").getString("summaryUnitPrice")};
        
        List<SummaryRow> rows = new LinkedList<>();
        {
            rows.add(new SummaryRow(ResourceBundle.getBundle("billance/Services").getString("water"),
                water,
                ResourceBundle.getBundle("billance/Services").getString("waterUnit"),
                tariff.water*water,tariff.water));
            if(includeEletricity){
                rows.add(new SummaryRow(ResourceBundle.getBundle("billance/Services").getString("electricityVT"),
                    vt,
                    ResourceBundle.getBundle("billance/Services").getString("electricityVTUnit"),
                    tariff.elvt*vt,tariff.elvt));
                rows.add(new SummaryRow(ResourceBundle.getBundle("billance/Services").getString("electricityNT"),
                    nt,
                    ResourceBundle.getBundle("billance/Services").getString("electricityNTUnit"),
                    tariff.elnt*nt,tariff.elnt));
            }
            rows.add(new SummaryRow(ResourceBundle.getBundle("billance/Services").getString("heat"),
                getHeatingEnergy(),
                ResourceBundle.getBundle("billance/Services").getString("heatUnit"),
                tariff.heat*getHeatingEnergy(),tariff.heat));
            rows.add(new SummaryRow(ResourceBundle.getBundle("billance/Services").getString("monthlyFee"),
                months,
                ResourceBundle.getBundle("billance/Services").getString("monthlyFeeUnit"),
                tariff.getMonthFee(includeEletricity)*months,tariff.getMonthFee(includeEletricity)));
        }
        
        private final DateFormat dateFormat = new SimpleDateFormat(ResourceBundle.getBundle("billance/Services").getString("dateFormat"));
        private final DecimalFormat floatFormat = new DecimalFormat("0.000");
        private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00 Kč");
        /*private Measures measures;
        
        public MeasuresTableModel(Measures measures){
            this.measures = measures;
        }*/
        public SummaryTableModel(){ }
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rows.get(rowIndex).getItem(columnIndex);
        }
        
        class SummaryRow{
            public String service;
            public Object amount;
            public String unit;
            public double cost;
            public double unitPrice;
            public SummaryRow(String service, Object amount, String unit, double cost, double unitPrice){
                this.service = service;
                this.amount = amount;
                this.unit = unit;
                this.cost = cost;
                this.unitPrice = unitPrice;
            }
            String getItem(int index){
                try {
                    Field field = this.getClass().getFields()[index];
                    Object val = field.get(this);
                    if(field.getType().getName().equals(double.class.getTypeName()))
                        return currencyFormat.format(val);
                    if(val instanceof Date)
                        return dateFormat.format(val);
                    if(val instanceof Integer)
                        return Integer.toString((int) val);
                    if(val instanceof Float || val instanceof Double)
                        return floatFormat.format(val);
                    if(val instanceof String)
                        return (String) val;
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(EnergyBillance.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "";
            }
        }
    }
}
