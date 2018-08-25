package billance.tools;

import billance.EnergyBillance;
import billance.data.FlatView;
import billance.data.Tariff;
import java.util.Date;

public class EnergyBillanceCalculator
{
    public EnergyBillance calculateBillance(Date from, Date to, FlatView flat, Tariff tariff, boolean eletricity, int deposit)
    {
        EnergyBillance billance = EnergyBillance.loadMeasures(from, to, flat, tariff, eletricity, deposit);
        return billance;
    }

}
