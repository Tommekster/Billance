package billance.data;

import billance.dataProvider.ResultSetField;
import com.github.tommekster.jsonRpcClient.JsonRpcDataMember;
import com.github.tommekster.jsonRpcClient.convertors.DefaultDateConvertor;
import com.github.tommekster.jsonRpcClient.convertors.JsonRpcConvertor;
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
    @JsonRpcConvertor(convertor = DefaultDateConvertor.class)
    @ResultSetField
    public Date activeFrom;
    
    @JsonRpcDataMember
    @JsonRpcConvertor(convertor = DefaultDateConvertor.class)
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
