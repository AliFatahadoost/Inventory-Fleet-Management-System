package IFMS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testWebServerUtils {

    // ────────────── jsonParser tests ──────────────

    @Test
    void jsonParserSingleObject() {
        String json = "[{\"name\":\"Alice\",\"age\":30}]";
        String[][] result = webServerUtils.jsonParser(json);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"Alice", "30"}, result[0]);
    }

    @Test
    void jsonParserMultipleObjects() {
        String json = "[{\"a\":\"1\"},{\"b\":\"2\"},{\"c\":\"3\"}]";
        String[][] result = webServerUtils.jsonParser(json);
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"1"}, result[0]);
        assertArrayEquals(new String[]{"2"}, result[1]);
        assertArrayEquals(new String[]{"3"}, result[2]);
    }

    @Test
    void jsonParserEmptyArray() {
        String json = "[]";
        String[][] result = webServerUtils.jsonParser(json);
        assertEquals(0, result.length);
    }

    @Test
    void jsonParserMultipleKeys() {
        String json = "[{\"action\":\"update\",\"id\":\"42\",\"name\":\"test\"}]";
        String[][] result = webServerUtils.jsonParser(json);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"update", "42", "test"}, result[0]);
    }

    @Test
    void jsonParserObjectWithWhitespace() {
        String json = "[ { \"key\" : \"value\" } ]";
        String[][] result = webServerUtils.jsonParser(json);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"value"}, result[0]);
    }

    // ────────────── extractCredentials tests ──────────────

    @Test
    void extractCredentialsAllPresent() {
        String input = "{\"action\":\"1\",\"username\":\"john\",\"password\":\"secret\"}";
        String[] creds = webServerUtils.extractCredentials(input);
        assertArrayEquals(new String[]{"1", "john", "secret"}, creds);
    }

    @Test
    void extractCredentialsNoBraces() {
        String input = "\"action\":\"0\",\"username\":\"jane\",\"password\":\"pass\"";
        String[] creds = webServerUtils.extractCredentials(input);
        assertArrayEquals(new String[]{"0", "jane", "pass"}, creds);
    }

    @Test
    void extractCredentialsMissingFields() {
        String input = "{\"action\":\"1\"}";
        String[] creds = webServerUtils.extractCredentials(input);
        assertArrayEquals(new String[]{"1", "", ""}, creds);
    }

    @Test
    void extractCredentialsEmptyInput() {
        String input = "";
        String[] creds = webServerUtils.extractCredentials(input);
        assertArrayEquals(new String[]{"", "", ""}, creds);
    }

    @Test
    void extractCredentialsMixedOrder() {
        String input = "{\"password\":\"pw\",\"action\":\"0\",\"username\":\"user\"}";
        String[] creds = webServerUtils.extractCredentials(input);
        assertArrayEquals(new String[]{"0", "user", "pw"}, creds);
    }
}