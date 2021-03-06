package billance.dataProvider;

import billance.data.ContractView;
import billance.data.FlatView;
import billance.data.TariffSelectionView;
import billance.data.Tariff;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Acer
 */
public class SqliteDataProvider implements IDataProvider
{

    private Connection con;
    private boolean hasData = false;
    private final ResultSetObjectMapper mapper = new ResultSetObjectMapper();

    @Override
    public ResultSet findHeatConsumption(Date nearestFrom, Date nearestTo) throws SQLException
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'heatConsumption' WHERE \"from\" >= ? AND \"to\" <= ?");
            prep.setString(1, DateFormatProvider.getDateFormat().format(nearestFrom));
            prep.setString(2, DateFormatProvider.getDateFormat().format(nearestTo));
            ResultSet rs = prep.executeQuery();
            return rs;
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DataProviderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ResultSet findMeasure(Date date) throws SQLException
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'measures' WHERE date == ?");
            prep.setString(1, DateFormatProvider.getDateFormat().format(date));
            ResultSet rs = prep.executeQuery();
            return rs;
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DataProviderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Date findNearestMeasureDate(Date date)
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            PreparedStatement prep = con.prepareStatement("SELECT min(abs(unixtime-strftime('%s',?))) AS 'var', date FROM 'measuresDate'");
            prep.setString(1, DateFormatProvider.getDateFormat().format(date));
            ResultSet rs = prep.executeQuery();
            if (rs.next())
            {
                return DateFormatProvider.getDate(rs, "date");
            }
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            Logger.getLogger(DataProviderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ContractView[] loadContracts()
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            Statement state = con.createStatement();
            String query = "SELECT * FROM 'contractViews'";
            ResultSet resultSet = state.executeQuery(query);
            ContractView[] contracts = new ResultSetIterator(resultSet)
                    .toStream()
                    .map(rs -> this.mapper.map(rs, ContractView.class))
                    .filter(x -> x != null)
                    .toArray(ContractView[]::new);
            return contracts;
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            Logger.getLogger(SqliteDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ContractView[0];
    }

    @Override
    public FlatView[] loadFlats()
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            Statement state = con.createStatement();
            ResultSet rs = state.executeQuery("SELECT * FROM flatViews ORDER BY number");
            return new ResultSetIterator(rs).toStream()
                    .map(x -> this.mapper.map(x, FlatView.class))
                    .filter(x -> x != null)
                    .toArray(FlatView[]::new);
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            Logger.getLogger(SqliteDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new FlatView[0];
    }

    @Override
    public TariffSelectionView[] loadTarrifs(DateFormat format)
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            Statement state = con.createStatement();
            ResultSet rs = state.executeQuery("SELECT validFrom FROM tariffs ORDER BY validFrom");
            return new ResultSetIterator(rs).toStream()
                    .map(x ->
                    {
                        try
                        {
                            Date validFrom = DateFormatProvider.getDate(x, "validFrom");
                            TariffSelectionView tsv = new TariffSelectionView();
                            tsv.validFrom = format.format(validFrom);
                            return tsv;
                        }
                        catch (SQLException ex)
                        {
                            return null;
                        }
                    })
                    .filter(x -> x != null)
                    .toArray(TariffSelectionView[]::new);
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            Logger.getLogger(SqliteDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new TariffSelectionView[0];
    }

    @Override
    public Tariff getTariff(Date validFrom)
    {
        try
        {
            if (con == null)
            {
                getConnection();
            }
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'tariffs' WHERE validFrom == ?");
            prep.setString(1, DateFormatProvider.getDateFormat().format(validFrom));
            ResultSet rs = prep.executeQuery();
            if (!rs.next())
            {
                return null;
            }
            Tariff t = this.mapper.map(rs, Tariff.class);
            return t;
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            Logger.getLogger(SqliteDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void getConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:database1.sqlite");
        initialize();
    }

    private void initialize() throws SQLException
    {
        if (!hasData)
        {
            hasData = true;

            checkMeasuresTable();
            checkContractsTable();
            checkPersonsTable();
            checkPersoncontractsTable();
            checkFlatsTable();
            checkTariffsTable();
            checkMeasuresDateView();
            checkHeatConsumptionView();
            checkContractPersonsView();
            checkContractViewsView();
            checkFlatViewsView();
        }
    }

    private void checkMeasuresTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'measures'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'measures' ('date' DATETIME PRIMARY KEY UNIQUE DEFAULT CURRENT_TIMESTAMP NOT NULL, 'wm0' INTEGER, 'wm1' INTEGER, 'wm2' INTEGER, 'wm3' INTEGER, 'wm4' INTEGER, 'vt1' INTEGER, 'nt1' INTEGER, 'vt2' INTEGER, 'nt2' INTEGER, 'vt3' INTEGER, 'nt3' INTEGER, 'vt4' INTEGER, 'nt4' INTEGER, 'gas' INTEGER, 'cm1' INTEGER, 'cm2' INTEGER, 'cm3' INTEGER, 'cm4' INTEGER);");
        }
    }

    private void checkContractsTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'contracts'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'contracts' ('code' TEXT PRIMARY KEY UNIQUE NOT NULL, 'from' DATETIME NOT NULL, 'to' DATETIME, 'flat' INTEGER, 'eletricity' CHAR DEFAULT 1, 'archived' CHAR DEFAULT 0 NOT NULL)");
        }
    }

    private void checkPersonsTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'persons'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'persons' ('rowid' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, 'surname' TEXT NOT NULL, 'name' TEXT NOT NULL, 'domificile' TEXT NOT NULL, 'birthDate' DATETIME NOT NULL, 'bankAccount' TEXT, 'email' TEXT, 'phone' TEXT, 'note' TEXT, 'archived' BOOL DEFAULT 'false' NOT NULL)");
        }
    }

    private void checkPersoncontractsTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'personcontracts'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'personcontracts' ('person' INTEGER, 'contract' TEXT, 'archived' BOOL DEFAULT 'false')");
        }
    }

    private void checkFlatsTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'flats'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'flats' ('waterId' INTEGER NOT NULL, 'heatId' INTEGER NOT NULL, 'eletricityId' INTEGER NOT NULL, 'surface' INTEGER);");
            Statement state3 = con.createStatement();
            state3.execute("INSERT INTO flats (rowid,waterId,heatId,eletricityId,surface) VALUES (1,4,1,2,58),(2,2,4,3,60),(3,1,3,4,74),(4,3,2,1,63);");
        }
    }

    private void checkTariffsTable() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'tariffs'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'tariffs' ('validFrom' DATETIME PRIMARY KEY UNIQUE NOT NULL, 'water' FLOAT DEFAULT '0' NOT NULL, 'heat' FLOAT DEFAULT '0' NOT NULL, 'fee' FLOAT DEFAULT '0' NOT NULL, 'elvt' FLOAT DEFAULT '0' NOT NULL, 'elnt' FLOAT DEFAULT '0' NOT NULL, 'elfee' FLOAT DEFAULT '0' NOT NULL, 'volumeCoef' FLOAT DEFAULT '0' NOT NULL, 'combustionHeat' FLOAT DEFAULT '0' NOT NULL, 'surfaceCoef' FLOAT DEFAULT '0' NOT NULL)");
        }
    }

    private void checkMeasuresDateView() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'measuresDate'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW IF NOT EXISTS 'measuresDate' AS SELECT 'date', strftime(\"%s\",'date') AS 'unixtime' FROM 'measures'");
        }
    }

    private void checkHeatConsumptionView() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'heatConsumption'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW 'heatConsumption' AS SELECT p.date AS 'from', c.date AS 'to', (c.gas-p.gas) AS gas, (c.cm1-p.cm1) AS cm1, (c.cm2-p.cm2) AS cm2, (c.cm3-p.cm3) AS cm3, (c.cm4-p.cm4) AS cm4, (c.cm1+c.cm2+c.cm3+c.cm4-p.cm1-p.cm2-p.cm3-p.cm4) AS 'sum' FROM measures AS c LEFT JOIN measures AS p ON p.date = (SELECT MAX(date) FROM measures WHERE date < [c].date)");
        }
    }

    private void checkContractPersonsView() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'contractPersons'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW contractPersons AS SELECT c.contract, group_concat(p.name || ' ' || p.surname,', ') AS names FROM persons AS p JOIN personcontracts AS c ON p.rowid == c.person GROUP BY c.contract");
        }
    }

    private void checkContractViewsView() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'contractViews'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW contractViews AS\n"
                    + "    SELECT code,\n"
                    + "           [from] AS activeFrom,\n"
                    + "           [to] AS activeTo,\n"
                    + "           flat,\n"
                    + "           eletricity,\n"
                    + "           names\n"
                    + "      FROM contracts\n"
                    + "           JOIN\n"
                    + "           contractPersons ON contracts.code = contractPersons.contract\n"
                    + "     WHERE contracts.archived = 0;");
        }
    }

    private void checkFlatViewsView() throws SQLException
    {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'flatViews'");
        if (!res.next())
        {
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW flatViews AS\n"
                    + "    SELECT t1.rowid AS id,\n"
                    + "           t1.rowid AS number,\n"
                    + "           t1.*,\n"
                    + "           sum(t2.surface) AS commonSurface\n"
                    + "      FROM flats AS t1\n"
                    + "           INNER JOIN\n"
                    + "           flats AS t2 ON 1 = 1\n"
                    + "     GROUP BY t1.rowid;");
        }
    }

}
