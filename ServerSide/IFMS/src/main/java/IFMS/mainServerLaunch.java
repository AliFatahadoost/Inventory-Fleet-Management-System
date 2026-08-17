package IFMS;

import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.PageRelatedEnums.CrudQueriesEnum;
import IFMS.InterFaces.*;
import IFMS.PageRelatedEnums.FileTypesEnum;
import IFMS.PageRelatedEnums.FilesEnum;
import IFMS.WebServerHandlers.apiManagement;
import IFMS.WebServerHandlers.pageHandlerOpener;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class mainServerLaunch {
    public static void main(String[] args) throws IOException {
        HttpServer server = readConfig.initiate();
        
        server.createContext("/Login", new apiManagement.dataApiGen.builder().shouldSetCookie(true).shouldAuthenticate(false).sendTokenToDB(false)
                                                                             .setQuery(CrudQueriesEnum.Login).setRedirectLocation("/dashboard").build());
        
        
    }
   
    
    
    
   //these are 3 Important Enums that run your Front and CRUD mostly about Files
   public enum Queries implements CrudQueries
   {

        ;
        
        private final String createQuery;
        private final String readQuery;
        private final String updateQuery;
        private final String deleteQuery;
        
        Queries( String create, String read, String update, String delete)
        {
        
            this.createQuery = create;
            this.readQuery = read;
            this.updateQuery = update;
            this.deleteQuery = delete;
        
        }
        
        @Override
        public String getReadQuery() {
            return this.readQuery;
        }

        @Override
        public String getUpdateQuery() {
            return this.updateQuery;
        }

        @Override
        public String getDeleteQuery() {
            return this.deleteQuery;
        }

        @Override
        public String getCreateQuery() {
            return this.createQuery;
        }
   
   }
   public enum webPages implements JaliWebPage
   {

        ;

     private final int objectId;
     private final FilesEnum pageFile;

     webPages(int objectId, FilesEnum pageFile){
         this.objectId = objectId;
         this.pageFile = pageFile;
     }

     @Override
     public int getObjectId(){return this.objectId;}
     @Override
     public FilesEnum getFile(){return this.pageFile;}
     @Override
     public void registerRoute(HttpServer server){server.createContext("/"+this.name(), new pageHandlerOpener(readConfig.BASE_FILE_ADDRESS, this.pageFile));}
  
   }
   
   
   public enum myFiles implements JaliFiles
   {
   
       
    ;
    
    private final String relativeAddress;
    private final boolean loadedByIframe;
    private final FileTypesEnum fileType;//text/html or text/JavaScript for example
    
    myFiles(String relativeAddress, boolean loadedByIframe, FileTypesEnum fileType)
    {
        this.relativeAddress = relativeAddress;
        this.loadedByIframe = loadedByIframe;
        this.fileType = fileType;
        
    }
    
    @Override
    public String relativeAddress(){return this.relativeAddress;}
    @Override
    public boolean loadedByIframe(){return this.loadedByIframe;}
    @Override
    public String getFileType(){return this.fileType.getTechnicalType();} 
       
   }
   
   //this is the End of those Enums
   
}