package billance.remote;

import billance.data.ContractView;


public interface IBillanceRemoteService
{
    ContractView[] loadContracts();
}
