package billance.tools;

import java.util.HashMap;
import java.util.Map;

public class ToolsProvider
{
    private static final Map<Class<?>, Object> tools;

    static
    {
        tools = new HashMap<>();
        tools.put(EnergyBillanceCalculator.class, new EnergyBillanceCalculator());
    }

    public static <T> T getTool(Class<T> clazz)
    {
        return (T) tools.get(clazz);
    }

}
