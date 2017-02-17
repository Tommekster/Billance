/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Acer
 */
public class Database {
    private Connection con;
    private boolean hasData = false;
    private static Database database = null;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private Database(){}
    
    public static Database getInstance(){
        if(database == null) database = new Database();
        return database;
    }
    
    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    private void getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:database1.sqlite");
        initialise();
    }

    private void initialise() throws SQLException{
        if ( !hasData ){
            hasData = true;

            checkMeasuresTable();
            checkContractsTable();
            checkPersonsTable();
            checkPersoncontractsTable();
            checkFlatsTable();
            checkTariffsTable();
            checkMeasuresDateView();
            checkHeatConsumptionView();
        }
    }

    private void checkMeasuresTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'measures'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'measures' ('date' DATETIME PRIMARY KEY UNIQUE DEFAULT CURRENT_TIMESTAMP NOT NULL, 'wm0' INTEGER, 'wm1' INTEGER, 'wm2' INTEGER, 'wm3' INTEGER, 'wm4' INTEGER, 'vt1' INTEGER, 'nt1' INTEGER, 'vt2' INTEGER, 'nt2' INTEGER, 'vt3' INTEGER, 'nt3' INTEGER, 'vt4' INTEGER, 'nt4' INTEGER, 'gas' INTEGER, 'cm1' INTEGER, 'cm2' INTEGER, 'cm3' INTEGER, 'cm4' INTEGER);");
            
            // insterting some sample data
            /*PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
            prep.setString(2, "John");
            prep.setString(3, "McNeail");
            prep.execute();
            
            PreparedStatement prep2 = con.prepareStatement("INSERT INTO user values(?,?,?);");
            prep2.setString(2, "Paul");
            prep2.setString(3, "Smith");
            prep2.execute();*/
        }
    }

    private void checkContractsTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'contracts'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'contracts' ('code' TEXT PRIMARY KEY UNIQUE NOT NULL, 'from' DATETIME NOT NULL, 'to' DATETIME, 'flat' INTEGER, 'eletricity' BOOL DEFAULT 'true', 'archived' BOOL DEFAULT 'false' NOT NULL)");
        }
    }
    
    private void checkPersonsTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'persons'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'persons' ('rowid' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, 'surname' TEXT NOT NULL, 'name' TEXT NOT NULL, 'domificile' TEXT NOT NULL, 'birthDate' DATETIME NOT NULL, 'bankAccount' TEXT, 'email' TEXT, 'phone' TEXT, 'note' TEXT, 'archived' BOOL DEFAULT 'false' NOT NULL)");
        }
    }
    
    private void checkPersoncontractsTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'personcontracts'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'personcontracts' ('person' INTEGER, 'contract' TEXT, 'archived' BOOL DEFAULT 'false')");
        }
    }
    
    private void checkFlatsTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'flats'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'flats' ('waterId' INTEGER NOT NULL, 'heatId' INTEGER NOT NULL, 'eletricityId' INTEGER NOT NULL, 'surface' INTEGER);");
            Statement state3 = con.createStatement();
            state3.execute("INSERT INTO flats (rowid,waterId,heatId,eletricityId,surface) VALUES (1,4,1,2,58),(2,2,4,3,60),(3,1,3,4,74),(4,3,2,1,63);");
            //INSERT INTO flats (rowid,waterId,heatId,eletricityId,surface) VALUES (1,4,1,2,58),(2,2,4,3,60),(3,1,3,4,74),(4,3,2,1,63);
        }
    }
    
    private void checkTariffsTable() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'tariffs'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE 'tariffs' ('validFrom' DATETIME PRIMARY KEY UNIQUE NOT NULL, 'water' FLOAT DEFAULT '0' NOT NULL, 'heat' FLOAT DEFAULT '0' NOT NULL, 'fee' FLOAT DEFAULT '0' NOT NULL, 'elvt' FLOAT DEFAULT '0' NOT NULL, 'elnt' FLOAT DEFAULT '0' NOT NULL, 'elfee' FLOAT DEFAULT '0' NOT NULL, 'volumeCoef' FLOAT DEFAULT '0' NOT NULL, 'combustionHeat' FLOAT DEFAULT '0' NOT NULL, 'surfaceCoef' FLOAT DEFAULT '0' NOT NULL)");
        }
    }
    
    private void checkMeasuresDateView() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'measuresDate'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW IF NOT EXISTS 'measuresDate' AS SELECT 'date', strftime(\"%s\",'date') AS 'unixtime' FROM 'measures'");
        }
    }
    
    private void checkHeatConsumptionView() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='view' AND name = 'heatConsumption'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("CREATE VIEW IF NOT EXISTS 'heatConsumption' AS SELECT p.date AS 'from', c.date AS 'to', (c.gas-p.gas) AS gas, (c.wm0-p.wm0) AS wm0, (c.wm1-p.wm1) AS wm1, (c.wm2-p.wm2) AS wm2, (c.wm3-p.wm3) AS wm3, (c.wm4-p.wm4) AS wm4 FROM measures AS c LEFT JOIN measures AS p ON p.date = (SELECT MAX(date) FROM measures WHERE date < [c].date)");
        }
    }
    
    /*
    private void check_Table() throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = '_'");
        if(!res.next()){
            // build the table
            Statement state2 = con.createStatement();
            state2.execute("");
        }
    }*/

    /*public void addUser(String firstName, String lastName) throws SQLException, ClassNotFoundException{
        if(con == null) getConnection();

        PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
        prep.setString(2, firstName);
        prep.setString(3, lastName);
        prep.execute();
    }

    public void vlozeniZaznamuTranskci() throws SQLException, ClassNotFoundException{
        if(con == null) getConnection();

        try{
            con.setAutoCommit(false);
            Statement state = con.createStatement();
            state.executeUpdate("vubec netusim co");
            state.executeQuery("INSERT INTO user VALUES (10, 'nevim', 'neco')");
            con.commit();
        }
        catch(Exception e){
            con.rollback();
        }
    }*/
    
    public static Date getDate(ResultSet rs, String field) throws SQLException, ParseException{
        return dateFormat.parse(rs.getString(field));
    }
    
    public Date [] getTarrifs() throws ClassNotFoundException, SQLException, ParseException{
        if(con == null) getConnection();
        Statement state = con.createStatement();
        ResultSet rs = state.executeQuery("SELECT validFrom FROM tariffs ORDER BY validFrom");
        List<Date> tarrifs = new LinkedList<>();
        while(rs.next()) {
            tarrifs.add(getDate(rs,"validFrom"));
        }
        return tarrifs.toArray(new Date [0]);
    }
    
    public String [] getContracts() throws ClassNotFoundException, SQLException{
        if(con == null) getConnection();
        Statement state = con.createStatement();
        ResultSet rs = state.executeQuery("SELECT code FROM contracts ORDER BY 'from'");
        List<String> contracts = new LinkedList<>();
        while(rs.next()) {
            contracts.add(rs.getString("code"));
        }
        return contracts.toArray(new String [0]);
    }
    
    public String [] getFlats() throws ClassNotFoundException, SQLException{
        if(con == null) getConnection();
        Statement state = con.createStatement();
        ResultSet rs = state.executeQuery("SELECT rowid FROM flats ORDER BY rowid");
        List<String> flats = new LinkedList<>();
        while(rs.next()) {
            flats.add(rs.getString("rowid"));
        }
        return flats.toArray(new String [0]);
    }

    public ResultSet findHeatConsumption(Date nearestFrom, Date nearestTo) throws SQLException {
        try {
            if(con == null) getConnection();
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'heatConsumption' WHERE \"from\" >= ? AND \"to\" <= ?");
            prep.setString(1, dateFormat.format(nearestFrom));
            prep.setString(2, dateFormat.format(nearestTo));
            ResultSet rs = prep.executeQuery();
            return rs;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet findMeasure(Date date) throws SQLException {
        try {
            if(con == null) getConnection();
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'measures' WHERE 'date' == ?");
            prep.setString(1, dateFormat.format(date));
            ResultSet rs = prep.executeQuery();
            return rs;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Date findNearestMeasureDate(Date date) {
        try {
            if(con == null) getConnection();
            PreparedStatement prep = con.prepareStatement("SELECT min(abs(unixtime-strftime('%s',?)) AS 'var', 'date' FROM 'measuresDate'");
            prep.setString(1, dateFormat.format(date));
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return getDate(rs,"date");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    ResultSet findTariff(Date validFrom) throws SQLException {
        try {
            if(con == null) getConnection();
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'tariffs' WHERE 'validFrom' == ?");
            prep.setString(1, dateFormat.format(validFrom));
            ResultSet rs = prep.executeQuery();
            return rs;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    ResultSet findFlat(int flatId) throws SQLException {
        try {
            if(con == null) getConnection();
            PreparedStatement prep = con.prepareStatement("SELECT * FROM 'flats' WHERE 'rowid' == ?");
            prep.setInt(1, flatId);
            ResultSet rs = prep.executeQuery();
            return rs;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
