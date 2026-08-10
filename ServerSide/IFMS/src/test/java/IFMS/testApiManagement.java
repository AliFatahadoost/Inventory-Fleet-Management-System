package IFMS;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;

class testApiManagement {

    // ──────────── Builder tests ────────────

    @Test
    void builderValidShouldNotThrow() {
        assertDoesNotThrow(() ->
            new apiManagement.dateModifyApiGen.builder()
                .setApiName("testAPI")
                .setMethodAccepted("POST")
                .setQuery("EXEC SOME_PROC ?, ?")
                .build()
        );
    }

    @Test
    void builderMissingApiNameShouldThrow() {
        apiManagement.dateModifyApiGen.builder builder =
            new apiManagement.dateModifyApiGen.builder()
                .setMethodAccepted("POST")
                .setQuery("EXEC SOME_PROC ?, ?");

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void builderMissingMethodAcceptedShouldThrow() {
        apiManagement.dateModifyApiGen.builder builder =
            new apiManagement.dateModifyApiGen.builder()
                .setApiName("test")
                .setQuery("EXEC SOME_PROC ?, ?");

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void builderMissingQueryShouldThrow() {
        apiManagement.dateModifyApiGen.builder builder =
            new apiManagement.dateModifyApiGen.builder()
                .setApiName("test")
                .setMethodAccepted("GET");

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void builderQueryWithoutPlaceholdersShouldThrow() {

        apiManagement.dateModifyApiGen.builder builder =
            new apiManagement.dateModifyApiGen.builder()
                .setApiName("test")
                .setMethodAccepted("POST")
                .setQuery("SELECT 1");   // no '?' → count == 0

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void builderCountsParametersCorrectly() throws Exception {

        Method countMethod = apiManagement.dateModifyApiGen.builder.class
                .getDeclaredMethod("countParameters", String.class);
        countMethod.setAccessible(true);
        apiManagement.dateModifyApiGen.builder builder =
            new apiManagement.dateModifyApiGen.builder();

        assertEquals(0, countMethod.invoke(builder, "SELECT 1"));
        assertEquals(1, countMethod.invoke(builder, "EXEC ?, ?")); 
        assertEquals(3, countMethod.invoke(builder, "?, ?, ?"));
        assertEquals(5, countMethod.invoke(builder, "a?b?c?d?e?f"));
    }

    // ──────────── Test the LOWER helper ────────────
    @Test
    void lowerHelperWorks() throws Exception {

        Class<?> innerClass = apiManagement.manageInventoryStockRequest.class;
        Method lowerMethod = innerClass.getDeclaredMethod("LOWER", String.class);
        lowerMethod.setAccessible(true);

        assertEquals("hello", lowerMethod.invoke(null, "HELLO"));
        assertEquals("hello", lowerMethod.invoke(null, "Hello"));
        assertEquals(null, lowerMethod.invoke(null, (String) null));
    }

    // ──────────── Test apiManagement constructor ────────────
    @Test
    void constructorCopiesInputs() {
        String[] args = {"a", "b", "c"};
        apiManagement api = new apiManagement("some query", args);

        try {
            java.lang.reflect.Field inputsField = api.getClass().getDeclaredField("inputs");
            inputsField.setAccessible(true);
            String[] stored = (String[]) inputsField.get(api);
            assertArrayEquals(args, stored);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}