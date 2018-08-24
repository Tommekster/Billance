package billance.dataProvider;

public class DataProviderManager
{

    private static IDataProvider dataProviderInstance = null;

    private DataProviderManager()
    {
    }

    public static IDataProvider getDataProviderInstance()
    {
        if (dataProviderInstance == null)
        {
            dataProviderInstance = new SqliteDataProvider();
        }
        return dataProviderInstance;
    }
}
