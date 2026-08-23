package IFMS;

import IFMS.PageRelatedEnums.WebPagesEnum;
import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.PageRelatedEnums.CrudQueriesEnum;
import IFMS.WebServerHandlers.apiManagement;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class mainServerLaunch {
    public static void main(String[] args) throws IOException {
        HttpServer server = readConfig.initiate();
        
        
        server.createContext("/LoginApi", new apiManagement.dataApiGen.builder().shouldSetCookie(true).shouldAuthenticate(false).sendTokenToDB(false)
                                                                             .setQuery(CrudQueriesEnum.Login).setRedirectLocation("/Dashboard").build());
        
        server.createContext("/getUsername", new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(true).setQuery(CrudQueriesEnum.getUsernameWithToken).build());
        
        WebPagesEnum.Login.registerRoute(server);
        WebPagesEnum.Dashboard.registerRoute(server);
        
        
    }
   
    
    
}