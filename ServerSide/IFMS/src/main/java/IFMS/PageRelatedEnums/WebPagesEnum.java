
package IFMS.PageRelatedEnums;

import com.sun.net.httpserver.HttpServer;
import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.WebServerHandlers.pageHandlerOpener;
import IFMS.InterFaces.JaliWebPage;
import IFMS.DataBase.DataBaseInit;

public enum WebPagesEnum implements JaliWebPage{
    
    ;
    
    private final int objectId;
    private final FilesEnum pageFile;
    
    WebPagesEnum(int objectId, FilesEnum pageFile){
        this.objectId = objectId;
        this.pageFile = pageFile;
    }
    
    @Override
    public int getObjectId(){return this.objectId;}
    @Override
    public FilesEnum getFile(){return this.pageFile;}
    @Override
    public void registerRoute(HttpServer server){
        DataBaseInit.loadObjectIntoObjectList(objectId, this.name());
        server.createContext("/"+this.name(), new pageHandlerOpener(readConfig.BASE_FILE_ADDRESS, this.pageFile));}
}
