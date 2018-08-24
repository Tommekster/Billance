
package billance.remote;

import com.github.tommekster.jsonRpcClient.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContractImporter
{
    public void Import() {
        try
        {
            URL url = new URL("http://localhost:8000/moneyManager/billanceService/call/jsonrpc");
            JsonRpcInvoker invoker = new JsonRpcInvoker();
            Object message = invoker.invoke(url, "getContracts");
            System.out.println(message);
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException | JsonRpcError ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ContractImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
