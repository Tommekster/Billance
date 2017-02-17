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
    public int eletricityId;
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
            if(!rs.next()) return null;
            Flat f = new Flat();
            f.id = flatId;
            Field [] fields = Flat.class.getFields();
            System.out.println("Flat:"+fields.length);
            for(Field field : fields){
                System.out.println(field.getName());
                field.set(f,rs.getInt(field.getName()));
            }
            return f;
        } catch (SQLException ex) {
            Logger.getLogger(Tariff.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Flat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Flat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getTM() {
        return "cm"+heatId;
    }

    public String getWM() {
        return "wm"+waterId;
    }

    public String getVT() {
        return "vt"+eletricityId;
    }

    public String getNT() {
        return "nt"+eletricityId;
    }
}
