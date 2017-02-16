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

/**
 *
 * @author Acer
 */
public class Database {
    private Connection con;
    private boolean hasData = false;

    private void getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:database1.sqlite");
        initialise();
    }

    private void initialise() throws SQLException{
        if ( !hasData ){
            hasData = true;

            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'user'");
            if(!res.next()){
                System.out.println("Building the User table with prepopulated values.");
                // build the table
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE user(id integer,"
                    +"fName varchar(60)," + "lName varchar(60)," + "primary key(id));");

                // insterting some sample data
                PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep.setString(2, "John");
                prep.setString(3, "McNeail");
                prep.execute();

                PreparedStatement prep2 = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep2.setString(2, "Paul");
                prep2.setString(3, "Smith");
                prep2.execute();
            }
        }
    }

    public void addUser(String firstName, String lastName) throws SQLException, ClassNotFoundException{
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
    }
}
