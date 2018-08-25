
package billance.remote;

import billance.data.ContractView;
import com.github.tommekster.jsonRpcClient.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ContractImporter
{
    public ContractView[] Import(String serviceURL) throws MalformedURLException{
        try
        {
            // url "http://localhost:8000/moneyManager/billanceService/call/jsonrpc"
            URL url = new URL(serviceURL);
            IBillanceRemoteService service = JsonRpcProxy.getProxy(url, IBillanceRemoteService.class);
            ContractView[] contracts = service.loadContracts();
            Stream.of(contracts).forEach(c->{
                try
                {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    c.activeFrom = df.parse("2017-01-01");
                    c.activeTo = df.parse("2018-01-01");
                }
                catch (ParseException ex)
                {
                    c.activeFrom = new Date(0);
                    c.activeTo = new Date(0);
                }
            });
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
