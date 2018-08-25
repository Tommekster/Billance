package billance.dataProvider;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Tomáš Zikmund <tommekster@gmail.com>
 */
public class ResultSetObjectMapper
{
    public <T> T map(ResultSet resultSet, Class<T> type)
    {
        try
        {
            T dest = type.newInstance();
            Stream.of(type.getFields())
                    .filter(f -> f.getAnnotation(ResultSetField.class) != null)
                    .forEach(f -> this.setField(resultSet, f, dest));
        }
        catch (InstantiationException | IllegalAccessException ex)
        {
            Logger.getLogger(ResultSetObjectMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void setField(ResultSet resultSet, Field field, Object destination)
    {
        try
        {
            Class<?> fieldType = field.getType();
            String fieldName = this.getResultSetFieldName(field);
            if (Date.class.equals(fieldType))
            {
                field.set(destination, DateFormatProvider.getDate(resultSet, fieldName));
            }
            else
            {
                field.set(destination, resultSet.getObject(fieldName, fieldType));
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | SQLException | ParseException ex)
        {
            Logger.getLogger(ResultSetObjectMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getResultSetFieldName(Field field)
    {
        ResultSetField rsField = field.getAnnotation(ResultSetField.class);
        String name = rsField.name();
        return !name.isEmpty() ? name : field.getName();
    }
}
