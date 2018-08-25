
package billance.remote;

import billance.data.ContractView;
import com.github.tommekster.jsonRpcClient.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContractImporter
{
    public ContractView[] Import(String serviceURL) throws MalformedURLException{
        try
        {
            // url "http://localhost:8000/moneyManager/billanceService/call/jsonrpc"
            URL url = new URL(serviceURL);
            IBillanceRemoteService service = JsonRpcProxy.getProxy(url, IBillanceRemoteService.class);
            ContractView[] contracts = service.loadContracts();
            return contracts;
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        catch (Exception ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ContractView[0];
    }
}
