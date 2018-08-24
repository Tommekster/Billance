/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance;

import billance.dataProvider.Database;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Acer
 */
public class Contract {
    private String code;
    public Date from;
    public Date to;
    public int flat;
    public boolean eletricity;
    public boolean archived;
    
    public static String [] getContracts() throws ClassNotFoundException, SQLException{
        return Database.getInstance().getContracts();
    }
    
    private Contract() {}

    static Contract findContract(String code) {
        try {
            ResultSet rs = Database.getInstance().findContract(code);
            if(!rs.next()) return null;
            Contract c = new Contract();
            c.code = code;
            Field [] fields = c.getClass().getFields();
            for(Field field : fields){
                if(Date.class.equals(field.getType())){
                    field.set(c, Database.getDate(rs, field.getName()));
                }else if(int.class.equals(field.getType()))
                    field.set(c, rs.getInt(field.getName()));
                else if(boolean.class.equals(field.getType()))
                    field.set(c, rs.getBoolean(field.getName()));
            }
            return c;
        } catch (SQLException | IllegalArgumentException | IllegalAccessException | ParseException ex) {
            Logger.getLogger(Contract.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getPersons(){
        return Database.getInstance().getContractPersons(code);
    }
}
