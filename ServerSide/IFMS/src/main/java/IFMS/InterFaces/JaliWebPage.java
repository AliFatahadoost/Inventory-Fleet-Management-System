package IFMS.InterFaces;

import com.sun.net.httpserver.HttpServer;
import IFMS.PageRelatedEnums.FilesEnum;

public interface JaliWebPage {

    int getObjectId();

    FilesEnum getFile();

    void registerRoute(HttpServer server);
}