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
            return;
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
    
    public static class manageInventoryStockRequest implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String token = webServerUtils.extractTokenFromCookie(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
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

                boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
                if (!isAuthenticated) {
                    return;
                }

                // Expect JSON array with these elements in order:
                // [ACTION, INVENTORY_STOCK_REQUEST_ID, DRIVER_ID]
                String action = userSentJSON[0][0];
                String requestId = userSentJSON[0][1];  // INVENTORY_STOCK_REQUEST_ID
                String driverId  = userSentJSON[0][2];  // DRIVER_ID (only used for ACCEPT_REQUEST)

                String jsonResponse = dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC MANAGE_INVENTORY_STOCK_REQUEST ?, ?, ?",
                    action,
                    requestId,
                    driverId
                );

                // If the action is a GET (select), send the resulting JSON back to the client.
                // For ACCEPT mode, the procedure returns "SELECT 1" – we just confirm success.
                if (LOWER(action).equals("get_request_details")) {
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(200, -1);
                }
                exchange.close();

                // Optional activity log
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?",
                //     token, "user managed inventory stock request via API");
            }
        }

        // Helper method to avoid importing java.util.Locale
        private static String LOWER(String s) {
            return s == null ? null : s.toLowerCase();
        }
    }
    
    
    
    public static class dateModifyApiGen implements HttpHandler {
        
        public static class builder{
            
            private String  apiName;
            private String  methodAccepted;
            private String  query;
            private int     parametersCount; 

            public builder setApiName(String apiName){ this.apiName = apiName; return this;}
            public builder setMethodAccepted(String methodAccepted){ this.methodAccepted = methodAccepted.toUpperCase(); return this;}
            public builder setQuery(String query){ this.query = query; return this;}
            private void setPerametersCount(){ this.parametersCount = countParameters(this.query != null? this.query : "A"); }
            
            public dateModifyApiGen build()
            {
                this.setPerametersCount();
                if(!(this.apiName != null && this.methodAccepted != null && this.query != null && this.parametersCount > 0))
                {
                    throw new IllegalArgumentException("one of the Listed variables are not provided \n"
                                                      +"apiName : " + this.apiName +"\n"
                                                      +"methodAccepted : " + this.methodAccepted + "\n"
                                                      +"query : " + this.query + "\n" 
                                                      +"parametersCount : " + this.parametersCount + "\n"
                                                      +"remember nothing should be null and the count shouldnt be less then Zero");
                }
                
                
                return new dateModifyApiGen(this.apiName, this.methodAccepted, this.query, this.parametersCount);
            }
            
            
            private int countParameters(String query) {
                int count = 0;

                for (char c : query.toCharArray()) {
                    if (c == '?') {
                        count++;
                    }
                }

                return count;
            }
            
        }
        
        private final String apiName;
        private final String methodAccepted;
        private final String query;
        private final int parametersCount;
        
        private dateModifyApiGen(String apiName, String methodAccepted, String query, int parametersCount)
        {
        
            this.apiName = apiName;
            this.methodAccepted = methodAccepted;
            this.query = query;
            this.parametersCount = parametersCount;
        
        }
        
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String token = webServerUtils.extractTokenFromCookie(exchange);
            boolean isAuthenticated = token != null && dataBaseUtils.isAuthenticated(token);
            if (!isAuthenticated) {
                return;
            }
            
            if (!methodAccepted.equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            } 
                
            
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

            


            if(userSentJSON.length == 0 || userSentJSON[0].length != this.parametersCount){
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                throw new IllegalArgumentException(
                    "Expected " + this.parametersCount +
                    " parameters, received " + (userSentJSON.length == 0 ? 0 : userSentJSON[0].length)
                
                );
            }

            String statusResult = dataBaseUtils.runSelectQueryGetJSON(
                                    this.query,
                                    userSentJSON[0] 
                                  );

            String status = webServerUtils.jsonParser(statusResult)[0][0];
            if("1".equals(status))
                exchange.sendResponseHeaders(200, -1);
            else if("0".equals(status))
                exchange.sendResponseHeaders(500, -1);

            exchange.close();

            // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed driver via API");
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
