package billance.data;

import billance.dataProvider.ResultSetField;
import com.github.tommekster.jsonRpcClient.JsonRpcDataMember;
import java.util.Date;

/**
 *
 * @author Tomáš Zikmund
 */
public class ContractView
{
    @JsonRpcDataMember
    @ResultSetField
    public String code;
    
    @JsonRpcDataMember
    @ResultSetField
    public Date activeFrom;
    
    @JsonRpcDataMember
    @ResultSetField
    public Date activeTo;
    
    @JsonRpcDataMember
    @ResultSetField
    public Long flat;
    
    @JsonRpcDataMember
    @ResultSetField
    public Boolean eletricity;
    
    @JsonRpcDataMember
    @ResultSetField
    public String names;
    
    @Override
    public String toString()
    {
        return code; 
    }
}
