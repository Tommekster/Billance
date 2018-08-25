/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billance.remote;

import com.github.tommekster.jsonRpcClient.JsonRpcDataMember;

/**
 *
 * @author Tomáš
 */
public class Contract
{
    @JsonRpcDataMember
    public String code;
    
    @JsonRpcDataMember
    public String From;
    
    @JsonRpcDataMember
    public String To;
    
    @JsonRpcDataMember
    public Long flat;
    
    @JsonRpcDataMember
    public Boolean eletricity;
    
    @JsonRpcDataMember
    public Boolean archived;
    
    @JsonRpcDataMember
    public Person[] persons;
    
}
