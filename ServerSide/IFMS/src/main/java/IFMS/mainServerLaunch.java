package IFMS;

import IFMS.ConfigAndLauncherManager.readConfig;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class mainServerLaunch {
    public static void main(String[] args) throws IOException {
        readConfig.initiate();
    }
}