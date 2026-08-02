
package IFMS;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class webServerUtils {
    
    
    public static String[][] jsonParser(String json) {
            json = json.replace("\n", "")
                       .replace("\r", "")
                       .replace("[", "")
                       .replace("]", "")
                       .trim();

            String[] objects = json.split("\\},\\s*\\{");

            String[][] result = new String[objects.length][];

            for (int i = 0; i < objects.length; i++) {
                String obj = objects[i]
                        .replace("{", "")
                        .replace("}", "")
                        .trim();

                String[] pairs = obj.split(",");

                result[i] = new String[pairs.length];

                for (int j = 0; j < pairs.length; j++) {
                    String[] kv = pairs[j].split(":", 2);

                    String value = kv[1]
                            .trim()
                            .replace("\"", "");

                    result[i][j] = value;
                }
            }

            return result;
        }
    
    
    public static String[] extractCredentials(String input) {
    String cleaned = input.trim();
    if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
        cleaned = cleaned.substring(1, cleaned.length() - 1);
    }
    
    String[] pairs = cleaned.split(",");
    String action = "";
    String username = "";
    String password = "";
    
    for (String pair : pairs) {
        String[] keyValue = pair.split(":", 2);
        if (keyValue.length == 2) {
            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim().replaceAll("\"", "");
            
            switch (key) {
                case "action":
                    action = value;
                    break;
                case "username":
                    username = value;
                    break;
                case "password":
                    password = value;
                    break;
            }
        }
    }
    
    return new String[]{action, username, password};
    }
    
    
    static public String extractTokenFromCookie(HttpExchange exchange) {
    String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
    if (cookieHeader == null) return null;
    String[] cookies = cookieHeader.split("; ");
    for (String cookie : cookies) {
        if (cookie.startsWith("token=")) {
            return cookie.substring(6);
        }
    }
    return null;
    }
    
    
    static public void refreshPage(HttpExchange exchange) throws IOException
        {           
            String htmlResponse = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "    <script>\n" +
                            "        // Refresh the entire parent page (not just iframe)\n" +
                            "        if (window.top !== window.self) {\n" +
                            "            window.top.location.reload();\n" +
                            "        } else {\n" +
                            "            window.location.reload();\n" +
                            "        }\n" +
                            "    </script>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    <p>Refreshing page...</p>\n" +
                            "</body>\n" +
                            "</html>";

            byte[] response = htmlResponse.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
            return;
        }
    
    
    static public void kickUnAuthenticated(HttpExchange exchange) throws IOException
    {
        String token = webServerUtils.extractTokenFromCookie(exchange);
            if (!dataBaseUtils.isAuthenticated(token)) {   
                exchange.getResponseHeaders().set("Location", "/LoginPage");
                exchange.sendResponseHeaders(302, -1); // 302 Found redirect
                exchange.close();
                dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "User kicked. users token was invalid");
                return;
            }
    }
    
}
