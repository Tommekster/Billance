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

    public static Date getDate(ResultSet rs, String field) throws SQLException, ParseException
    {
        return DateFormatProvider.getDateFormat().parse(rs.getString(field));
    }
}
