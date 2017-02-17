/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.sql.SQLException;

/**
 *
 * @author Acer
 */
public class Contract {
    public static String [] getContracts() throws ClassNotFoundException, SQLException{
        return Database.getInstance().getContracts();
    }
}
