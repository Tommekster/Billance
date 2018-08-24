/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import billance.dataProvider.DataProviderManager;
import billance.dataProvider.DateFormatProvider;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Acer
 */
public class Contract
{

    public static String[] getContracts() throws ClassNotFoundException, SQLException
    {
        return DataProviderManager.getDataProviderInstance().getContracts();
    }
    static Contract findContract(String code) throws ParseException
    {
        try
        {
            ResultSet rs = DataProviderManager.getDataProviderInstance().findContract(code);
            if (!rs.next())
            {
                return null;
            }
            Contract c = new Contract();
            c.code = code;
            Field[] fields = c.getClass().getFields();
            for (Field field : fields)
            {
                if (Date.class.equals(field.getType()))
                {
                    field.set(c, DateFormatProvider.getDate(rs, field.getName()));
                }
                else if (int.class.equals(field.getType()))
                {
                    field.set(c, rs.getInt(field.getName()));
                }
                else if (boolean.class.equals(field.getType()))
                {
                    field.set(c, rs.getBoolean(field.getName()));
                }
            }
            return c;
        }
        catch (SQLException | IllegalArgumentException | IllegalAccessException ex)
        {
            Logger.getLogger(Contract.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Date from;
    public Date to;
    public int flat;
    public boolean eletricity;
    public boolean archived;

    private String code;

    private Contract()
    {
    }


    public String getPersons()
    {
        return DataProviderManager.getDataProviderInstance().getContractPersons(code);
    }
}
