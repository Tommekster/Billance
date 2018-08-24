package billance.remote;

import com.github.tommekster.jsonRpcClient.JsonRpcDataMember;

public class Person
{
    @JsonRpcDataMember
    public String surname;
    
    @JsonRpcDataMember
    public String name;
}
