package IFMS;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.io.TempDir;

class testReadConfig {

    // Snapshot original static values so we can restore after each test
    private String originalBaseFileAddress;
    private int originalMaxSessionTime;
    private String originalServer;
    private int originalPort;
    private String originalDatabaseName;
    private String originalUsername;
    private String originalPassword;
    private int originalMaxConnectionPool;
    private int originalPortNumber;
    private int originalQueueWaitLine;
    private String originalServerIP;
    private String originalUrl;
    private String originalServersBaseUrl;
    private String originalConfigFilePath;
    
    @BeforeEach
    void saveOriginalState() throws Exception {
        originalBaseFileAddress = readConfig.BASE_FILE_ADDRESS;
        originalMaxSessionTime = readConfig.MAX_SESSION_TIME;
        originalServer = readConfig.server;
        originalPort = readConfig.port;
        originalDatabaseName = readConfig.databaseName;
        originalUsername = readConfig.username;
        originalPassword = readConfig.password;
        originalMaxConnectionPool = readConfig.MAX_CONNECTION_POOL;
        originalPortNumber = readConfig.portNumber;
        originalQueueWaitLine = readConfig.queueWaitLine;
        originalServerIP = readConfig.serverIP;
        originalUrl = readConfig.url;
        originalServersBaseUrl = readConfig.serversBaseUrl;
        originalConfigFilePath = getPrivateStaticField("configFilePath");
    }

    @AfterEach
    void restoreOriginalState() throws Exception {
        readConfig.BASE_FILE_ADDRESS = originalBaseFileAddress;
        readConfig.MAX_SESSION_TIME = originalMaxSessionTime;
        readConfig.server = originalServer;
        readConfig.port = originalPort;
        readConfig.databaseName = originalDatabaseName;
        readConfig.username = originalUsername;
        readConfig.password = originalPassword;
        readConfig.MAX_CONNECTION_POOL = originalMaxConnectionPool;
        readConfig.portNumber = originalPortNumber;
        readConfig.queueWaitLine = originalQueueWaitLine;
        readConfig.serverIP = originalServerIP;
        readConfig.url = originalUrl;
        readConfig.serversBaseUrl = originalServersBaseUrl;
        setPrivateStaticField("configFilePath", originalConfigFilePath);
    }

    // Helper: set the private static configFilePath field
    private void setPrivateStaticField(String fieldName, String value) throws Exception {
        Field field = readConfig.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private String getPrivateStaticField(String fieldName) throws Exception {
        Field field = readConfig.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(null);
    }

    @Test
    void testDefaultValues() {
        assertNotNull(readConfig.BASE_FILE_ADDRESS);
        assertEquals(86400, readConfig.MAX_SESSION_TIME);
        assertEquals("localhost", readConfig.server);
        assertEquals(1433, readConfig.port);
        assertEquals("PROJECT_ZERO", readConfig.databaseName);
        assertEquals("sa", readConfig.username);
        assertEquals("12", readConfig.password);
        assertEquals(5, readConfig.MAX_CONNECTION_POOL);
        assertEquals(8080, readConfig.portNumber);
        assertEquals(10, readConfig.queueWaitLine);
        assertEquals("127.0.0.1", readConfig.serverIP);
        assertTrue(readConfig.url.contains("jdbc:sqlserver://"));
        assertTrue(readConfig.serversBaseUrl.startsWith("http://"));
    }

    @Test
    void testBuildURL() {
        String expectedUrl = "jdbc:sqlserver://" + readConfig.server + ":" + readConfig.port +
                ";databaseName=" + readConfig.databaseName +
                ";encrypt=true;trustServerCertificate=true";
        assertEquals(expectedUrl, readConfig.url);
    }

    @Test
    void testLoadConfigFileNotFound(@TempDir Path tempDir) throws Exception {
        File nonExistent = tempDir.resolve("nonexistent.txt").toFile();
        setPrivateStaticField("configFilePath", nonExistent.getAbsolutePath());
        assertFalse(readConfig.loadConfig());
    }

    @Test
    void testLoadConfigValidFile(@TempDir Path tempDir) throws Exception {
        File configFile = tempDir.resolve("testConfig.txt").toFile();
        try (FileWriter fw = new FileWriter(configFile)) {
            Properties props = new Properties();
            props.setProperty("BASE_FILE_ADDRESS", "/test/path");
            props.setProperty("MAX_SESSION_TIME", "3600");
            props.setProperty("server", "192.168.1.100");
            props.setProperty("port", "1434");
            props.setProperty("databaseName", "TEST_DB");
            props.setProperty("username", "testuser");
            props.setProperty("password", "secret");
            props.setProperty("MAX_CONNECTION_POOL", "3");
            props.setProperty("portNumber", "9090");
            props.setProperty("queueWaitLine", "5");
            props.setProperty("serverIP", "10.0.0.1");
            props.store(fw, "Test config");
        }

        setPrivateStaticField("configFilePath", configFile.getAbsolutePath());

        assertTrue(readConfig.loadConfig());

        assertEquals("/test/path", readConfig.BASE_FILE_ADDRESS);
        assertEquals(3600, readConfig.MAX_SESSION_TIME);
        assertEquals("192.168.1.100", readConfig.server);
        assertEquals(1434, readConfig.port);
        assertEquals("TEST_DB", readConfig.databaseName);
        assertEquals("testuser", readConfig.username);
        assertEquals("secret", readConfig.password);
        assertEquals(3, readConfig.MAX_CONNECTION_POOL);
        assertEquals(9090, readConfig.portNumber);
        assertEquals(5, readConfig.queueWaitLine);
        assertEquals("10.0.0.1", readConfig.serverIP);

        // Check derived fields
        assertTrue(readConfig.url.contains("192.168.1.100:1434"));
        assertTrue(readConfig.url.contains("databaseName=TEST_DB"));
        assertEquals("http://10.0.0.1:9090", readConfig.serversBaseUrl);
    }

    @Test
    void testSaveConfig(@TempDir Path tempDir) throws Exception {
        File configFile = tempDir.resolve("savedConfig.txt").toFile();
        setPrivateStaticField("configFilePath", configFile.getAbsolutePath());

        // Modify some properties
        readConfig.server = "db.example.com";
        readConfig.port = 9999;
        readConfig.databaseName = "SAVED_DB";
        readConfig.serverIP = "192.168.0.1";
        readConfig.portNumber = 7070;

        readConfig.saveConfig();

        // Now load the file to verify
        Properties loaded = new Properties();
        try (FileInputStream fr = new FileInputStream(configFile)) {
            loaded.load(fr);
        }

        assertEquals("db.example.com", loaded.getProperty("server"));
        assertEquals("9999", loaded.getProperty("port"));
        assertEquals("SAVED_DB", loaded.getProperty("databaseName"));
        assertEquals("192.168.0.1", loaded.getProperty("serverIP"));
        assertEquals("7070", loaded.getProperty("portNumber"));
    }

    @Test
    void testRebuildURL() {
        readConfig.server = "newhost";
        readConfig.port = 1234;
        readConfig.databaseName = "newDB";

        // Trigger rebuildURL via reflection (it's private)
        try {
            java.lang.reflect.Method method = readConfig.class.getDeclaredMethod("rebuildURL");
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception e) {
            fail("Failed to invoke rebuildURL: " + e.getMessage());
        }

        assertEquals("jdbc:sqlserver://newhost:1234;databaseName=newDB;encrypt=true;trustServerCertificate=true",
                readConfig.url);
    }

    @Test
    void testUpdateServersBaseUrl() throws Exception {
        readConfig.serverIP = "10.10.10.10";
        readConfig.portNumber = 8888;

        java.lang.reflect.Method method = readConfig.class.getDeclaredMethod("updateServersBaseUrl");
        method.setAccessible(true);
        method.invoke(null);

        assertEquals("http://10.10.10.10:8888", readConfig.serversBaseUrl);
    }
}