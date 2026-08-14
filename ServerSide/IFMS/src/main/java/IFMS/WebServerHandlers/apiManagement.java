package IFMS.WebServerHandlers;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import IFMS.PageRelatedEnums.CrudQueriesEnum;
import IFMS.DataBase.dataBaseUtils;
import IFMS.DataBase.dataBaseManager;
import IFMS.ConfigAndLauncherManager.readConfig;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.util.Arrays;

public class apiManagement{ 
    
    public static class dataApiGen implements HttpHandler {
        
        public static class builder{
            
            private String  apiName;
            private String  methodAccepted;
            private CrudQueriesEnum  query;
            private int     accessCode;
            private boolean sendTokenToDB;

            public builder setApiName(String apiName){ this.apiName = apiName; return this;}
            public builder setMethodAccepted(String methodAccepted){ this.methodAccepted = methodAccepted.toUpperCase(); return this;}
            public builder setQuery(CrudQueriesEnum query){ this.query = query; return this;}
            public builder setAccessCode(int accessCode){ this.accessCode = accessCode; return this;}
            public builder sendTokenToDB(boolean sendTokenToDB){ this.sendTokenToDB = sendTokenToDB; return this;}

            public dataApiGen build()
            {
                return new dataApiGen(this.apiName, this.methodAccepted, this.query, this.accessCode, this.sendTokenToDB);
            }
        }
        
        private final String  apiName;
        private final String  methodAccepted;
        private final CrudQueriesEnum  query;
        private final int     accessCode;
        
        private final boolean sendTokenToDB;
        
        private int countParameters(String query) {
                int count = 0;

                for (char c : query.toCharArray()) {
                    if (c == '?') {
                        count++;
                    }
                }

                return count;
            }
        
        private dataApiGen(String apiName, String methodAccepted, CrudQueriesEnum query, int accessCode, boolean sendTokenToDB)
        {
            this.apiName = apiName;
            this.methodAccepted = methodAccepted;
            this.query = query;
            this.accessCode = accessCode;
            this.sendTokenToDB = sendTokenToDB;
        }
        
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String token = webServerUtils.extractTokenFromCookie(exchange);
            //System.out.println("something is calling me");
            boolean isAuthenticated = /*token != null &&*/ dataBaseUtils.isAuthenticated(token);
            if (!isAuthenticated) {
                //System.out.println("401");
                exchange.sendResponseHeaders(401, -1);
                exchange.close();
                return;
            }
            
            /*if(!(dataBaseUtils.isAllowedRead(token, this.accessCode))){
                        //System.out.println("403");
                        exchange.sendResponseHeaders(403, -1);
                        exchange.close();
                        return;
            }*/
            
            if ("PATCH".equals(exchange.getRequestMethod())) {
                //System.out.println("405");
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            } 
                
            if("POST".equals(exchange.getRequestMethod()))
                {         
                    InputStream is = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    String userData = sb.toString();
                    String[][] userSentJSON = webServerUtils.jsonParser(userData);


                    int parametersCount = countParameters(this.query.getCreateQuery() != null? this.query.getCreateQuery() : "A");

                    if(userSentJSON.length == 0 || userSentJSON[0].length != parametersCount){
                        exchange.sendResponseHeaders(400, -1);
                        exchange.close();
                        return;
                    }
                    try{
                    String statusResult = dataBaseUtils.runSelectQueryGetJSON(
                                            this.query.getCreateQuery(),
                                            userSentJSON[0] 
                                          );
                    
                    String status = webServerUtils.jsonParser(statusResult)[0][0];
                    
                    if(status.contains("1"))
                        exchange.sendResponseHeaders(200, -1);
                    else if(status.contains("0"))
                        exchange.sendResponseHeaders(500, -1);
                    }catch(Exception e){}
                    exchange.close();
            }
            else if("GET".equals(exchange.getRequestMethod()))
            {
            
                String[][] valuesSent = webServerUtils.parseGetRequestURL(exchange.getRequestURI().toString());
                
                int parametersCount = countParameters(this.query.getReadQuery() != null? this.query.getReadQuery() : "A");
                
                
                String Result = "";
                if(this.sendTokenToDB){
                    String[] combinedInputs = new String[valuesSent.length + 1];
                    combinedInputs[0] = token;
                    System.arraycopy(valuesSent, 0, combinedInputs, 1, valuesSent.length);
                    try{
                    Result = dataBaseUtils.runSelectQueryGetJSON(this.query.getReadQuery(), combinedInputs);
                    }
                    catch(Exception e)
                    {System.out.println("someThing went wrong with the SQL Code for GET in function genDataAPi");}
                }
                else
                {           
                    try{
                    if(!(valuesSent[1][0] == "" && valuesSent[1][1] == ""))
                        Result = dataBaseUtils.runSelectQueryGetJSON(this.query.getReadQuery(), valuesSent[1]);
                    else
                        Result = dataBaseUtils.runSelectQueryGetJSON(this.query.getReadQuery());
                    }
                    catch(Exception e)
                    {
                       System.out.println("Line 160 apiManamgement e : " + e.toString());
                    }
                }
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        

                byte[] responseBytes = Result.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                try(OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                catch(Exception e) {
                   System.out.println(e);
                   exchange.sendResponseHeaders(500, -1);
                   exchange.close();
                }
            }
            else if("DELETE".equals(exchange.getRequestMethod()))
            {
            
                String[][] valuesSent = webServerUtils.parseGetRequestURL(exchange.getRequestURI().toString());
                
                if(valuesSent.length != 2) 
                {
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }
                String Result = "";
                
                try{
                Result = dataBaseUtils.runSelectQueryGetJSON(this.query.getDeleteQuery(), valuesSent[1][0]);
                }catch(Exception e){}
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        

                byte[] responseBytes = Result.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                try(OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                catch(Exception e) {
                   System.out.println(e);
                   exchange.sendResponseHeaders(500, -1);
                }
            }
            if("PUT".equals(exchange.getRequestMethod()))
            {         
                InputStream is = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                String userData = sb.toString();
                String[][] userSentJSON = webServerUtils.jsonParser(userData);


                String[][] valuesSent = webServerUtils.parseGetRequestURL(exchange.getRequestURI().toString());

                if(userSentJSON.length == 0 || valuesSent.length != 2){
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }

                String[] inputDataBase = new String[userSentJSON[0].length + 1];
                inputDataBase[userSentJSON[0].length] = valuesSent[1][0];  
                System.arraycopy(userSentJSON[0], 0, inputDataBase, 0, userSentJSON[0].length);
                
                try{
                String statusResult = dataBaseUtils.runSelectQueryGetJSON(
                                        this.query.getUpdateQuery(),
                                        inputDataBase
                                      );
                
                
                String status = webServerUtils.jsonParser(statusResult)[0][0];
                if(status.contains("1"))
                    exchange.sendResponseHeaders(200, -1);
                else if(status.contains("0"))
                    exchange.sendResponseHeaders(500, -1);
                }
                catch(Exception e)
                {
                   //Exception Handling BABY they need to learn from me
                }
                
                exchange.close();
            }
        }
        
    }
    
    
    
    
        
    static class loginSignUpHandlingAPI implements HttpHandler{ //this method handles SignUps and Logins    
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String Token = "";
            if("POST".equals(exchange.getRequestMethod())){
                InputStream is = exchange.getRequestBody();
                String userDataSent;
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                reader.close();
                String userData = sb.toString();

                String[] credentialsUser = webServerUtils.extractCredentials(userData);
                
                if(credentialsUser[0].equals("1")){ //this part handles Logins
                    Token = dataBaseManager.userLogin(credentialsUser[1], credentialsUser[2]);
                }
                else if(credentialsUser[0].equals("0")){ //this part handles Signups
                    Token = (dataBaseManager.userSignUp(credentialsUser[1], credentialsUser[2])) + "";
                }
                if (Token != null && !Token.equals("0") && !Token.isEmpty()) {
                    String cookieValue = "token=" + Token + "; Path=/;Max-Age=" + readConfig.MAX_SESSION_TIME + "; HttpOnly";
                    exchange.getResponseHeaders().set("Set-Cookie", cookieValue);
                }
                String response = "{\"status\":\"" + Token + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                exchange.close();
            }
        }   
    }
}
