
package billance.remote;

import com.github.tommekster.jsonRpcClient.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ContractImporter
{
    public void Import() {
        try
        {
            URL url = new URL("http://localhost:8000/moneyManager/billanceService/call/jsonrpc");
            IBillanceRemoteService service = JsonRpcProxy.getProxy(url, IBillanceRemoteService.class);
            Contract[] message = service.getContracts();
            Stream.of(message).forEach(c->{
                System.out.println("");
                System.out.println(c.code);
                System.out.println(c.From);
                System.out.println(c.To);
                System.out.println(c.flat);
                System.out.println(c.eletricity);
                System.out.println(c.archived);
                // c.persons is null; jsonRpcMapper dont map complexObject
                //Stream.of(c.persons).forEach(p->
                  //  System.out.println(p.name+" "+p.surname));
            });
            
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
