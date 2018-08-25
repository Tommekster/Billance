package billance.dataProvider;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Tomáš Zikmund <tommekster@gmail.com>
 */
public class ResultSetObjectMapper
{
    Map<Class<?>, ResultSetGetter<?>> rsGetters;

    public ResultSetObjectMapper()
    {
        rsGetters = new HashMap<>();
        this.RegisterResultSetGetter(float.class, (rs, n) -> rs.getFloat(n));
        this.RegisterResultSetGetter(Float.class, (rs, n) -> Float.valueOf(rs.getFloat(n)));
        this.RegisterResultSetGetter(double.class, (rs, n) -> rs.getDouble(n));
        this.RegisterResultSetGetter(Double.class, (rs, n) -> Double.valueOf(rs.getDouble(n)));
        this.RegisterResultSetGetter(int.class, (rs, n) -> rs.getInt(n));
        this.RegisterResultSetGetter(Integer.class, (rs, n) -> Integer.valueOf(rs.getInt(n)));
        this.RegisterResultSetGetter(Long.class, (rs, n) -> Long.valueOf(rs.getInt(n)));
        this.RegisterResultSetGetter(boolean.class, (rs, n) -> rs.getBoolean(n));
        this.RegisterResultSetGetter(Boolean.class, (rs, n) -> Boolean.valueOf(rs.getBoolean(n)));
        this.RegisterResultSetGetter(String.class, (rs, n) -> rs.getString(n));
        this.RegisterResultSetGetter(Date.class, (rs, n) -> DateFormatProvider.getDate(rs, n));
    }

    public <T> T map(ResultSet resultSet, Class<T> type)
    {
        try
        {
            T dest = type.newInstance();
            Stream.of(type.getFields())
                    .filter(f -> f.getAnnotation(ResultSetField.class) != null)
                    .forEach(f -> this.setField(resultSet, f, dest));
            return dest;
        }
        catch (InstantiationException | IllegalAccessException ex)
        {
            Logger.getLogger(ResultSetObjectMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private <T> void RegisterResultSetGetter(Class<T> forType, ResultSetGetter<T> getter)
    {
        rsGetters.put(forType, getter);
    }

    private void setField(ResultSet resultSet, Field field, Object destination)
    {
        try
        {
            Class<?> fieldType = field.getType();
            String fieldName = this.getResultSetFieldName(field);
            field.set(destination, this.getValue(resultSet, fieldName, fieldType));
        }
        catch (IllegalArgumentException | IllegalAccessException | SQLException ex)
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

    private <T> T getValue(ResultSet resultSet, String fieldName, Class<T> fieldType) throws SQLException
    {
        T value = (T) this.rsGetters.get(fieldType).apply(resultSet, fieldName);
        return value;
    }

    @FunctionalInterface
    private interface ResultSetGetter<R>
    {
        public R apply(ResultSet resultSet, String name) throws SQLException;
    }
}
