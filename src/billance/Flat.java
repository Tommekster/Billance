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
public class Flat {
    public static String [] getFlats() throws ClassNotFoundException, SQLException {
        /*Integer [] flats = Database.getInstance().getFlats();
        String [] flatNames = new String [flats.length];
        for(int i = 0; i < flats.length; i++) flatNames[i] = Integer.toString(flats[i].intValue());
        return flatNames;*/
        return Database.getInstance().getFlats();
    }
}
