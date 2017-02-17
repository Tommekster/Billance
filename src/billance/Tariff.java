/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Acer
 */
public class Tariff {
    public static Date [] getTarrifs() throws ClassNotFoundException, SQLException, ParseException{
        return Database.getInstance().getTarrifs();
    }
}
