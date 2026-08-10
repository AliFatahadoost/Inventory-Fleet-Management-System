package IFMS;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

class testPageHandlerOpener {

    private pageHandlerOpener instance;

    @BeforeEach
    void createInstance() throws Exception {
        // Allocate an instance WITHOUT calling the constructor (no DB hit)
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        instance = (pageHandlerOpener) unsafe.allocateInstance(pageHandlerOpener.class);
    }

    // Helper to convert string ↔ byte[]
    private byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private String filter(String html, String token) {
        byte[] result = instance.filterHtmlByAccess(bytes(html), token);
        return new String(result, StandardCharsets.UTF_8);
    }

    // ──── Null token: everything is kept ────
    @Test
    void nullToken_plainHtml_passedThrough() {
        String input = "<div><p>Hello</p></div>";
        String output = filter(input, null);
        assertEquals(input, output);
    }

    @Test
    void nullToken_tagWithAccessCode_kept() {
        String input = "<div data-AccessCode=\"123\">Content</div>";
        String output = filter(input, null);
        assertEquals(input, output);
    }

    @Test
    void nullToken_nestedTags_kept() {
        String input = "<div><span data-AccessCode=\"5\">Inner</span></div>";
        String output = filter(input, null);
        assertEquals(input, output);
    }

    @Test
    void nullToken_selfClosingTag_kept() {
        String input = "<img data-AccessCode=\"42\" src=\"x.jpg\" />";
        String output = filter(input, null);
        assertEquals(input, output);
    }

    // ──── Token present but no data-AccessCode → no DB call, still kept ────
    @Test
    void tokenNoAccessCode_kept() {
        String input = "<p>No access code here</p>";
        String output = filter(input, "anyToken");
        assertEquals(input, output);
    }

    // ──── Self‑closing tag detection ────
    @Test
    void selfClosingWithoutSpaceBeforeSlash() {
        String input = "<br/>";
        String output = filter(input, null);
        assertEquals(input, output);
    }

    // ──── Mixed content: some with, some without access codes ────
    @Test
    void mixedTags_allKeptWhenTokenNull() {
        String input = "<div data-AccessCode=\"1\">A</div><span>B</span><img data-AccessCode=\"2\"/>";
        String output = filter(input, null);
        assertEquals(input, output);
    }
}