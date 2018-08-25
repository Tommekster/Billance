/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance.dataProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomáš
 */
public class DateFormatProvider
{

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static DateFormat getDateFormat()
    {
        return DATE_FORMAT;
    }

    public static Date getDate(ResultSet rs, String field) throws SQLException
    {
        try
        {
            return DateFormatProvider.getDateFormat().parse(rs.getString(field));
        }
        catch (ParseException ex)
        {
            Logger.getLogger(DateFormatProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
