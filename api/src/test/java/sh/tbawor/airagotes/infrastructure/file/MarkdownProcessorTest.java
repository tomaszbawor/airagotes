package sh.tbawor.airagotes.infrastructure.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownProcessorTest {

    private MarkdownProcessor markdownProcessor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        markdownProcessor = new MarkdownProcessor();
    }

    @Test
    void shouldProcessMarkdownFileAndExtractMetadata() throws IOException {
        // given
        String markdown = "# Test Document\n\n" +
                "This is a test document with some content.\n\n" +
                "## Section 1\n\n" +
                "Some content in section 1.\n\n" +
                "## Section 2\n\n" +
                "Some content in section 2.\n\n" +
                "```java\n" +
                "public class Test {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, world!\");\n" +
                "    }\n" +
                "}\n" +
                "```\n";

        Path filePath = tempDir.resolve("test-document.md");
        Files.writeString(filePath, markdown);
        File file = filePath.toFile();

        // when
        List<Document> documents = markdownProcessor.processFile(file);

        // then
        assertNotNull(documents);
        assertFalse(documents.isEmpty());

        // Check metadata
        Document firstDoc = documents.get(0);
        Map<String, Object> metadata = firstDoc.getMetadata();

        assertEquals(file.getPath(), metadata.get("source"));
        assertEquals("test-document.md", metadata.get("filename"));
        assertEquals("Test Document", metadata.get("title"));

        // Check headers
        @SuppressWarnings("unchecked")
        List<String> headers = (List<String>) metadata.get("headers");
        assertNotNull(headers);
        assertEquals(3, headers.size());
        assertEquals("Test Document", headers.get(0));
        assertEquals("Section 1", headers.get(1));
        assertEquals("Section 2", headers.get(2));

        // Check code blocks
        assertEquals(1, metadata.get("code_block_count"));

        // Check content
        String content = firstDoc.getFormattedContent();
        assertNotNull(content);
        assertTrue(content.contains("Test Document"));
        assertTrue(content.contains("Section 1"));
        assertTrue(content.contains("Section 2"));
    }

    @Test
    void shouldHandleMarkdownFileWithNoHeaders() throws IOException {
        // given
        String markdown = "This is a document with no headers.\n\n" +
                "It only has plain text content.\n\n" +
                "And some more paragraphs.";

        Path filePath = tempDir.resolve("no-headers.md");
        Files.writeString(filePath, markdown);
        File file = filePath.toFile();

        // when
        List<Document> documents = markdownProcessor.processFile(file);

        // then
        assertNotNull(documents);
        assertFalse(documents.isEmpty());

        // Check metadata
        Document firstDoc = documents.get(0);
        Map<String, Object> metadata = firstDoc.getMetadata();

        assertEquals(file.getPath(), metadata.get("source"));
        assertEquals("no-headers.md", metadata.get("filename"));
        assertEquals("no headers", metadata.get("title")); // Derived from filename

        // Check headers
        @SuppressWarnings("unchecked")
        List<String> headers = (List<String>) metadata.get("headers");
        assertNotNull(headers);
        assertTrue(headers.isEmpty());

        // Check code blocks
        assertEquals(0, metadata.get("code_block_count"));

        // Check content
        String content = firstDoc.getFormattedContent();
        assertNotNull(content);
        assertTrue(content.contains("This is a document with no headers"));
    }

    @Test
    void shouldHandleEmptyMarkdownFile() throws IOException {
        // given
        String markdown = "";

        Path filePath = tempDir.resolve("empty.md");
        Files.writeString(filePath, markdown);
        File file = filePath.toFile();

        // when
        List<Document> documents = markdownProcessor.processFile(file);

        // then
        assertNotNull(documents);
        // The behavior might vary depending on the Spring AI implementation
        // Either it returns an empty list or a list with one empty document
        if (!documents.isEmpty()) {
            Document firstDoc = documents.get(0);
            Map<String, Object> metadata = firstDoc.getMetadata();

            assertEquals(file.getPath(), metadata.get("source"));
            assertEquals("empty.md", metadata.get("filename"));
            assertEquals("empty", metadata.get("title")); // Derived from filename

            @SuppressWarnings("unchecked")
            List<String> headers = (List<String>) metadata.get("headers");
            assertNotNull(headers);
            assertTrue(headers.isEmpty());

            assertEquals(0, metadata.get("code_block_count"));
        }
    }
}
