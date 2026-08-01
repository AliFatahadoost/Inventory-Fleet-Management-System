package javaapplication5;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class dataBaseManager {

        /*static private String server = "localhost"; 
        static private int port = 1433;           
        static private String databaseName = "PROJECT_ZERO"; 
        static private String username = "sa";       
        static private String password = "12";  
        

        static private String url = "jdbc:sqlserver://" + server + ":" + port + 
                     ";databaseName=" + databaseName + 
                     ";encrypt=true;trustServerCertificate=true";
        
        static final private int MAX_CONNECTION_POOL = 5;*/
        
        static private PooledConnection[] dbConnections = new PooledConnection[readConfig.MAX_CONNECTION_POOL];
        static private AtomicBoolean[] isConnectionAvaliable = new AtomicBoolean[readConfig.MAX_CONNECTION_POOL];
        static private boolean isDBInitilized = false;
        
    
    public static class PooledConnection implements AutoCloseable {
    private final Connection connection;
    private final int poolIndex;
    
        public PooledConnection(Connection connection, int poolIndex) {
            this.connection = connection;
            this.poolIndex = poolIndex;
        }

        public Connection getConnection() {
            return connection;
        }

        @Override
        public void close() {
            // This returns the connection to the pool instead of closing it
            isConnectionAvaliable[poolIndex].set(true);
        }
    }
        
        
    public static void InitilizeDataBase() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");   
        for(int i = 0; i < readConfig.MAX_CONNECTION_POOL; i ++)
        {
            dbConnections[i] = new PooledConnection(DriverManager.getConnection(readConfig.url, readConfig.username, readConfig.password), i);
            isConnectionAvaliable[i] = new AtomicBoolean(true);
        }
        
        isDBInitilized = true;
    }
    
    private static boolean isValidConnection(Connection conn) {
        if (conn == null) return false;
        
        try {
            if (conn.isClosed()) {
                return false;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static PooledConnection getPooledConnection() throws SQLException, ClassNotFoundException
    {

                if(!isDBInitilized)
                    InitilizeDataBase();
                
                for(int i = 0; i < readConfig.MAX_CONNECTION_POOL; i++)
                {
                    if(isConnectionAvaliable[i].compareAndSet(true, false))
                    {
                        
                        if(isValidConnection(dbConnections[i].getConnection()))
                        {
                            return dbConnections[i];
                        }
                        else
                        {
                            
                            dbConnections[i] = new PooledConnection(DriverManager.getConnection(readConfig.url, readConfig.username, readConfig.password), i);
                            return dbConnections[i];
                        }
                    }
                }   
                
            throw new SQLException("No available connections in the pool. All " + 
                          readConfig.MAX_CONNECTION_POOL + " connections are in use.");
           
    }
    
    
    
    
    public static String userLogin(String usernameinp, String passwordinp)
    {
        int shouldBeLoggedIn = 0;
        String Token ="";
        String Query = "EXEC LOGIN_VALID ?, ?";
        try 
        (
                dataBaseManager.PooledConnection conn = dataBaseManager.getPooledConnection();
                PreparedStatement pstmt = conn.getConnection().prepareStatement(Query);
                )
        {
                   
            
                    
            
            pstmt.setString(1, usernameinp);
            pstmt.setString(2, passwordinp);                      
            try(ResultSet rs = pstmt.executeQuery()){                    
            if(rs.next()){
                shouldBeLoggedIn = rs.getInt("RESULT");
                Token = rs.getString("TOKEN");
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
        if(shouldBeLoggedIn == 1){
        return Token;
        }
        else{
        return "0";
        }
    }
    
    
    
    
    
    public static int userSignUp(String usernameinp, String passwordinp)
    {
        int shouldBeLoggedIn = 0;
        String Query = "EXEC SIGNUP_NEWUSER ?, ?";        
        try 
        (
            dataBaseManager.PooledConnection conn = dataBaseManager.getPooledConnection();
            PreparedStatement pstmt = conn.getConnection().prepareStatement(Query);
                )
        {
             
            
            pstmt.setString(1, usernameinp);
            pstmt.setString(2, passwordinp);           
            try(ResultSet rs = pstmt.executeQuery()){        
            if(rs.next()){
                shouldBeLoggedIn = rs.getInt("RESULT");
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
        return shouldBeLoggedIn;
    }
}

