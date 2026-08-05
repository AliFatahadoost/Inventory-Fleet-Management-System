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
    
    public static class manageProductsMovementTrips implements HttpHandler {
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

                // The JSON keys are expected in this order:
                // action, products_movements_id, drivers_id, vehicle_id,
                // comes_from_location_id, goes_to_location_id, est_arival,
                // did_arive, products_movements_list_id, products_id, products_count
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC MANAGE_PRODUCTS_MOVEMENT_TRIPS ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @PRODUCTS_MOVEMENTS_ID
                    userSentJSON[0][2],  // @DRIVERS_ID
                    userSentJSON[0][3],  // @VEHICLE_ID
                    userSentJSON[0][4],  // @COMES_FROM_LOCATION_ID
                    userSentJSON[0][5],  // @GOES_TO_LOCATION_ID
                    userSentJSON[0][6],  // @EST_ARIVAL
                    userSentJSON[0][7],  // @DID_ARIVE
                    userSentJSON[0][8],  // @PRODUCTS_MOVEMENTS_LIST_ID
                    userSentJSON[0][9],  // @PRODUCTS_ID
                    userSentJSON[0][10]  // @PRODUCTS_COUNT
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in the reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed products movement trip via API");
            }
        }
    }
    
    public static class manageInventoryInfstructure implements HttpHandler {
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

                // Parameters are expected in this order:
                // @ACTION, @INVENTORY_LOCATION_TITLE, @NEW_INVENTORY_LOCATION_TITLE,
                // @PRODUCTS_NAME, @NEW_PRODUCTS_NAME, @PRODUCTS_CATEGORY_ID,
                // @PRODUCTS_CATEGORY_NAME, @NEW_PRODUCTS_CATEGORY_NAME
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC MANAGE_INVENTORY_INFSTRUCTURE ?, ?, ?, ?, ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @INVENTORY_LOCATION_TITLE
                    userSentJSON[0][2],  // @NEW_INVENTORY_LOCATION_TITLE
                    userSentJSON[0][3],  // @PRODUCTS_NAME
                    userSentJSON[0][4],  // @NEW_PRODUCTS_NAME
                    userSentJSON[0][5],  // @PRODUCTS_CATEGORY_ID
                    userSentJSON[0][6],  // @PRODUCTS_CATEGORY_NAME
                    userSentJSON[0][7]   // @NEW_PRODUCTS_CATEGORY_NAME
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed inventory infrastructure via API");
            }
        }
    }
    
    public static class HandleInventoryRequest implements HttpHandler {
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

                // Parameters order:
                // @ACTION, @INVENTORY_STOCK_REQUEST_ID, @INVENTORY_ID,
                // @PRODUCTS_ID, @PRODUCTS_COUNT
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC HANDLE_INVENTORY_REQUEST ?, ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @INVENTORY_STOCK_REQUEST_ID
                    userSentJSON[0][2],  // @INVENTORY_ID
                    userSentJSON[0][3],  // @PRODUCTS_ID
                    userSentJSON[0][4]   // @PRODUCTS_COUNT
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in the reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user handled inventory request via API");
            }
        }
    }
    
    public static class createUpdateDeleteVehicle implements HttpHandler {
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

                // Parameters order: @ACTION, @VEHICLE_ID, @VEHICLE_NAME, @VEHICLE_LICENCE_PLATE
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC CREATE_UPDATE_DELETE_VEHICLE ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @VEHICLE_ID
                    userSentJSON[0][2],  // @VEHICLE_NAME
                    userSentJSON[0][3]   // @VEHICLE_LICENCE_PLATE
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in the reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed vehicle via API");
            }
        }
    }
    
    public static class createUpdateDeleteInventoryLocation implements HttpHandler {
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

                // Parameters order:
                // @ACTION, @INVENTORY_LOCATION_ID, @INVENTORY_LOCATION_NAME,
                // @INVENTORY_LOCATION_ADDRESS, @INVENTORY_LOCATION_LAT,
                // @INVENTORY_LOCATION_LONG, @LT_INVENTORY_LOCATION_TYPE_ID
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC CREATE_UPDATE_DELETE_INVENTORY_LOCATION ?, ?, ?, ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @INVENTORY_LOCATION_ID
                    userSentJSON[0][2],  // @INVENTORY_LOCATION_NAME
                    userSentJSON[0][3],  // @INVENTORY_LOCATION_ADDRESS
                    userSentJSON[0][4],  // @INVENTORY_LOCATION_LAT
                    userSentJSON[0][5],  // @INVENTORY_LOCATION_LONG
                    userSentJSON[0][6]   // @LT_INVENTORY_LOCATION_TYPE_ID
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in the reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed inventory location via API");
            }
        }
    }
    
    public static class createUpdateDeleteDriver implements HttpHandler {
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

                // Parameters order:
                // @ACTION, @DRIVER_ID, @DRIVER_NAME, @DRIVER_LAST_NAME,
                // @DRIVERS_NATIONAL_CODE, @DRIVERS_PHONE_NUMBER
                dataBaseUtils.runSelectQueryGetJSON(
                    "EXEC CREATE_UPDATE_DELETE_DRIVER ?, ?, ?, ?, ?, ?",
                    userSentJSON[0][0],  // @ACTION
                    userSentJSON[0][1],  // @DRIVER_ID
                    userSentJSON[0][2],  // @DRIVER_NAME
                    userSentJSON[0][3],  // @DRIVER_LAST_NAME
                    userSentJSON[0][4],  // @DRIVERS_NATIONAL_CODE
                    userSentJSON[0][5]   // @DRIVERS_PHONE_NUMBER
                );

                exchange.sendResponseHeaders(200, -1);
                exchange.close();

                // Optional activity log (commented out as in the reference)
                // dataBaseUtils.runSelectQueryGetJSON("EXEC ACTIVITY_LOG_MANAGER 0, ?, ?", token, "user managed driver via API");
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
