package IFMS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

class testDataBaseUtils {

    @Test
    void escapeJsonHandlesNull() throws Exception {
        Method method = dataBaseUtils.class.getDeclaredMethod("escapeJson", String.class);
        method.setAccessible(true);
        assertEquals("", method.invoke(null, (Object) null));
    }

    @Test
    void escapeJsonEscapesBackslashAndQuote() throws Exception {
        Method method = dataBaseUtils.class.getDeclaredMethod("escapeJson", String.class);
        method.setAccessible(true);

        String input = "a\"b\\c";
        String expected = "a\\\"b\\\\c";
        assertEquals(expected, method.invoke(null, input));
    }

    @Test
    void escapeJsonEscapesNewlineAndTab() throws Exception {
        Method method = dataBaseUtils.class.getDeclaredMethod("escapeJson", String.class);
        method.setAccessible(true);

        String input = "line1\nline2\tend\r";
        String expected = "line1\\nline2\\tend\\r";
        assertEquals(expected, method.invoke(null, input));
    }

    @Test
    void escapeJsonNoSpecialChars() throws Exception {
        Method method = dataBaseUtils.class.getDeclaredMethod("escapeJson", String.class);
        method.setAccessible(true);

        String input = "plain text 123";
        assertEquals(input, method.invoke(null, input));
    }
}