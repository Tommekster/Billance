/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import billance.dataProvider.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.*;

/**
 *
 * @author Acer
 */
public class Tariff {
    private Date validFrom;
    public float water;
    public float heat;
    public float fee;
    public float elvt;
    public float elnt;
    public float elfee;
    public float volumeCoef;
    public float combustionHeat;
    public float surfaceCoef;
    
    public static Date [] getTarrifs() throws ClassNotFoundException, SQLException, ParseException{
        return Database.getInstance().getTarrifs();
    }
    
    private Tariff(){}

    static Tariff findTariff(Date validFrom) {
        try {
            ResultSet rs = Database.getInstance().findTariff(validFrom);
            if(!rs.next()) return null;
            Tariff t = new Tariff();
            t.validFrom = validFrom;
            Field [] fields = Tariff.class.getFields();
            for(Field field : fields){
                field.set(t,rs.getFloat(field.getName()));
            }
            return t;
        } catch (SQLException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Tariff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    double getVolumeCoeficient() {
        return volumeCoef;
    }

    double getCombustionHeat() {
        return combustionHeat;
    }
    
    double getMonthFee(boolean eletricity){
        return fee+((eletricity)?elfee:0);
    }
}
