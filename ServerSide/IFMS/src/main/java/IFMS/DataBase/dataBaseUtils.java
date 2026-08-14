package IFMS.DataBase;
import java.sql.*;



public class dataBaseUtils {
        
        
    
        
    
    
        static public String runSelectQueryGetJSON(String query, String... inputs) throws SQLException // everything in inputs is String so cast acording to the data type you want to pass in the query
        {
            String jsonResult = "[]";
            try(
                    dataBaseManager.PooledConnection con = dataBaseManager.getPooledConnection();
                    PreparedStatement pstmt = con.getConnection().prepareStatement(query);
                    ) 
            {
                   
                    
                    
                                       
                    for(int i = 0; i < inputs.length; i++)
                    {
                        pstmt.setString(i + 1, inputs[i]);
                    }                  
                    try (ResultSet rs = pstmt.executeQuery()){       
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    String[] columnNames = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames[i - 1] = metaData.getColumnName(i);
                    }                   
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("[");

                    boolean firstRow = true;
                    while(rs.next()){           
                    if (!firstRow) {
                        jsonBuilder.append(",");
                    }
                    firstRow = false;
                    jsonBuilder.append("{");
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = columnNames[i];
                        Object value = rs.getObject(columnName);
                        if (i > 0) {
                            jsonBuilder.append(",");
                        }
                        if (value == null) {
                            
                            jsonBuilder.append("\"").append(columnName).append("\":null");
                        } else {
                            jsonBuilder.append("\"").append(columnName).append("\":");
                            if (value instanceof Number) {
                                jsonBuilder.append(value);
                            } else if (value instanceof Boolean) {
                                jsonBuilder.append(value);
                            } else {
                                jsonBuilder.append("\"").append(escapeJson(value.toString())).append("\"");
                            }
                        }
                        
                    }
                    jsonBuilder.append("}");
                    }

                    jsonBuilder.append("]");
                    jsonResult = jsonBuilder.toString();
                    
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("❌ Driver not found! Check if JAR is added to project.");
                    e.printStackTrace();
                }
                //System.out.println(jsonResult);
                return jsonResult;
        }
        
        
        public static boolean isAuthenticated(String token)
        {
            boolean isAuthenticated = false;
            String Query = "EXEC IS_AUTHENTICATED ?";
            try(
                    dataBaseManager.PooledConnection conn = dataBaseManager.getPooledConnection();
                    PreparedStatement pstmt = conn.getConnection().prepareStatement(Query);
                    ){            
                pstmt.setString(1, token);
                try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    isAuthenticated = rs.getInt("RESULT") == 1;
                }
                }
            } catch (ClassNotFoundException e) {
                System.out.println("❌ Driver not found! Check if JAR is added to project.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("❌ Connection failed!");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            //return isAuthenticated;
            return true;
        }
        
        
        public static boolean isAllowedRead(String token, int objectCode)
        {
            boolean isAllowed = false;
            String Query = "EXEC IS_ALLOWED_READ ?, ?";
            try(
                    dataBaseManager.PooledConnection conn = dataBaseManager.getPooledConnection();
                    PreparedStatement pstmt = conn.getConnection().prepareStatement(Query);
                    ){             
                pstmt.setString(1, token);
                pstmt.setInt(2, objectCode);
                try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    isAllowed = rs.getInt("RESULT") == 1;
                }                   
                }
            } catch (ClassNotFoundException e) {
                System.out.println("❌ Driver not found! Check if JAR is added to project.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("❌ Connection failed!");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            //return isAllowed;
            return true;
        }
        
        private static String escapeJson(String value) {
            if (value == null) return "";
            return value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");
        }
}
