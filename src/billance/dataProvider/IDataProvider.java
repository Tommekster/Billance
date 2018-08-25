/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance.dataProvider;

import billance.data.ContractView;
import billance.data.FlatView;
import billance.data.TariffSelectionView;
import billance.data.Tariff;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Tomáš
 */
public interface IDataProvider
{

    ResultSet findHeatConsumption(Date nearestFrom, Date nearestTo) throws SQLException;

    ResultSet findMeasure(Date date) throws SQLException;

    Date findNearestMeasureDate(Date date);

    ContractView[] loadContracts();

    FlatView[] loadFlats();

    TariffSelectionView[] loadTarrifs(DateFormat format);

    Tariff getTariff(Date validFrom);

}
