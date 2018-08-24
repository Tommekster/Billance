/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance.dataProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    String[] getContracts() throws ClassNotFoundException, SQLException;
    
    ResultSet findContract(String code) throws SQLException;
    
    String getContractPersons(String code);

    String[] getFlats() throws ClassNotFoundException, SQLException;
    
    ResultSet findFlat(int flatId) throws SQLException;
    
    int getFlatsSurface();

    Date[] getTarrifs() throws ClassNotFoundException, SQLException, ParseException;
    
    ResultSet findTariff(Date validFrom) throws SQLException;
    
}
