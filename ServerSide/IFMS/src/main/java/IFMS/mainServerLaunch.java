package IFMS;

import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.PageRelatedEnums.CrudQueriesEnum;
import IFMS.WebServerHandlers.apiManagement;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class mainServerLaunch {
    public static void main(String[] args) throws IOException {
        HttpServer server = readConfig.initiate();
        
        server.createContext("/Login", new apiManagement.dataApiGen.builder().shouldSetCookie(true).shouldAuthenticate(false).sendTokenToDB(false)
                                                                             .setQuery(CrudQueriesEnum.Login).setRedirectLocation("/dashboard").build());
        
        
    }
   
    
    
}