/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Acer
 */
public class Flat {
    private int id;
    public int waterId;
    public int heatId;
    public int electricityId;
    public int surface;
    
    public static String [] getFlats() throws ClassNotFoundException, SQLException {
        /*Integer [] flats = Database.getInstance().getFlats();
        String [] flatNames = new String [flats.length];
        for(int i = 0; i < flats.length; i++) flatNames[i] = Integer.toString(flats[i].intValue());
        return flatNames;*/
        return Database.getInstance().getFlats();
    }
    
    private Flat() {}

    static Flat findFlat(int flatId) {
        try {
            ResultSet rs = Database.getInstance().findFlat(flatId);
            Flat f = new Flat();
            f.id = flatId;
            Field [] fields = Flat.class.getFields();
            for(Field field : fields){
                rs.getInt(field.getName());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tariff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    int getTM() {
        return heatId;
    }

    int getWM() {
        return waterId;
    }

    int getVT() {
        return electricityId;
    }

    int getNT() {
        return electricityId;
    }
}
