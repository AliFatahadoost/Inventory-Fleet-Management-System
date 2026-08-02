package javaapplication5;

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
import java.util.Arrays;

public class apiManagement{
    private String queryToGetResponse = "";
    private String[] inputs;
    
    public apiManagement(String queryToGetResponse, String... inputs)
    {
        this.queryToGetResponse = queryToGetResponse;
        this.inputs = Arrays.copyOf(inputs, inputs.length);
    }
    
    public static class reportBasedOnUserTokenMaker implements HttpHandler
    {
        private String query = "";
        private String[] inputs;
        
        public reportBasedOnUserTokenMaker(String query, String... inputs)
        {
            this.query = query;
            this.inputs = Arrays.copyOf(inputs, inputs.length);
        }
    
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange);
            
            String[] combinedInputs = new String[this.inputs.length + 1];
            combinedInputs[0] = token;
            System.arraycopy(this.inputs, 0, combinedInputs, 1, this.inputs.length);
            
            apiManagement api = new apiManagement(query, combinedInputs);
            api.queryBasedApiWithAuth(exchange);
            //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user ran this query via API : " + query);
        }
    
    }
    
    public static class changeUsername implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
            
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
                
                apiManagement api = new apiManagement("EXEC CHANGE_USERNAME ?, ?", credentialsUser[1], token);
                api.queryBasedApiWithAuth(exchange);
            }

        }
    }
    
    
    public static class changePassword implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
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
                
                apiManagement api = new apiManagement("EXEC CHANGE_PASSWORD ?, ?, ?", credentialsUser[1], credentialsUser[2], token);
                api.queryBasedApiWithAuth(exchange);
            }

        }
    }
    
    public void queryBasedApiWithAuth(HttpExchange exchange) throws IOException
    {
        String token = webServerUtils.extractTokenFromCookie(exchange);
        boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
        if (!isAuthenticated) {
            webServerUtils.refreshPage(exchange);
        }
        //System.out.println("hello");
        if("POST".equals(exchange.getRequestMethod())){
            //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user ran this query via API : " + queryToGetResponse);
            String response = dataBaseUtils.runSelectQueryGetJSON(queryToGetResponse, Arrays.copyOf(this.inputs, this.inputs.length));              
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            exchange.close();
        }
    }
    
    
    public static class changeTaskStatus implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
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
                
                apiManagement api = new apiManagement("EXEC TASKS_REPORT ?, ?, ?",token ,credentialsUser[1], credentialsUser[2]);
                api.queryBasedApiWithAuth(exchange);
                //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user changed Task Status via API");
            }

        }
    }
    
    public static class createNewUser implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
            if("POST".equals(exchange.getRequestMethod())){
                InputStream is = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                reader.close();
                String userData = sb.toString();
                String[][] sentUserJSON = webServerUtils.jsonParser(userData);
                boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
                if (!isAuthenticated) {
                    return;
                }
                dataBaseUtils.runSelectQueryGetJSON("EXEC SIGNUP_NEWUSER ?, ?", sentUserJSON[0][0], sentUserJSON[0][1]);
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user changed Task Status via API");
            }

        }
    }
    
    
    public static class addRevokeRoles implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
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
                System.out.println(userData);
                String[][] userSentJSON = webServerUtils.jsonParser(userData);
                boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
                if (!isAuthenticated) {
                    return;
                }
                dataBaseUtils.runSelectQueryGetJSON("EXEC ROLES_MANAGEMENT_ID_BASED ?, ?, ?", userSentJSON[0][0], userSentJSON[0][1], userSentJSON[0][2]);
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user changed Task Status via API");
            }

        }
    }
    
    
    public static class createUpdateDeleteRoles implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
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
                System.out.println(userData);
                String[][] userSentJSON = webServerUtils.jsonParser(userData);
                boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
                if (!isAuthenticated) {
                    return;
                }
                dataBaseUtils.runSelectQueryGetJSON("EXEC CREATE_UPDATE_DELETE_ROLES ?, ?, ?, ?, ?, ?", userSentJSON[0][0], userSentJSON[0][1], userSentJSON[0][2],
                                                                                                        userSentJSON[0][3], userSentJSON[0][4], userSentJSON[0][5]);
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user changed Task Status via API");
            }

        }
    }
    
    
    public static class updateUserCredByAdmin implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String token = webServerUtils.extractTokenFromCookie(exchange); 
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
                System.out.println(userData);
                String[][] userSentJSON = webServerUtils.jsonParser(userData);
                boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
                if (!isAuthenticated) {
                    return;
                }
                
                dataBaseUtils.runSelectQueryGetJSON("EXEC CHANGE_USER_CRED_BY_ADMIN ?, ?, ?", userSentJSON[0][0], userSentJSON[0][1], userSentJSON[0][2]);
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                //dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user changed Task Status via API");
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
