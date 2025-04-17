package sh.tbawor.airagotes.infrastructure.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor for markdown files that enhances the embedding process.
 * This class provides improved chunking, metadata extraction, and preprocessing for markdown content.
 */
public class MarkdownProcessor {

    private static final Logger log = LoggerFactory.getLogger(MarkdownProcessor.class);

    // Patterns for extracting metadata from markdown
    private static final Pattern TITLE_PATTERN = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```", Pattern.MULTILINE);

    /**
     * Processes a markdown file and returns a list of documents.
     *
     * @param file the markdown file to process
     * @return a list of documents
     */
    public List<Document> processFile(File file) {
        try {
            log.debug("Processing markdown file: {}", file.getPath());

            // Extract file-level metadata
            Map<String, Object> fileMetadata = extractFileMetadata(file);

            // Create resource and reader
            Resource resource = new FileSystemResource(file);
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder().build();

            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
            List<Document> docs = reader.get();

            // Preprocess content
            docs = preprocessContent(docs, fileMetadata);

            // Split into chunks with semantic awareness
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> splitDocs = splitter.apply(docs);

            log.debug("Processed file {} into {} document chunks", file.getPath(), splitDocs.size());
            return splitDocs;
        } catch (Exception e) {
            log.error("Error processing markdown file: {}", file.getPath(), e);
            return List.of();
        }
    }

    /**
     * Extracts metadata from a markdown file.
     *
     * @param file the markdown file
     * @return a map of metadata
     */
    private Map<String, Object> extractFileMetadata(File file) {
        Map<String, Object> metadata = new HashMap<>();

        // Add file path and name
        metadata.put("source", file.getPath());
        metadata.put("filename", file.getName());

        try {
            String content = Files.readString(file.toPath());

            // Extract title from first h1 header
            Matcher titleMatcher = TITLE_PATTERN.matcher(content);
            if (titleMatcher.find()) {
                metadata.put("title", titleMatcher.group(1).trim());
            } else {
                // Use filename as title if no h1 header
                String filename = file.getName();
                if (filename.endsWith(".md")) {
                    filename = filename.substring(0, filename.length() - 3);
                }
                metadata.put("title", filename.replace("-", " ").replace("_", " "));
            }

            // Extract all headers
            List<String> headers = new ArrayList<>();
            Matcher headerMatcher = HEADER_PATTERN.matcher(content);
            while (headerMatcher.find()) {
                headers.add(headerMatcher.group(2).trim());
            }
            metadata.put("headers", headers);

            // Count code blocks
            Matcher codeBlockMatcher = CODE_BLOCK_PATTERN.matcher(content);
            int codeBlockCount = 0;
            while (codeBlockMatcher.find()) {
                codeBlockCount++;
            }
            metadata.put("code_block_count", codeBlockCount);

        } catch (IOException e) {
            log.error("Error reading markdown file: {}", file.getPath(), e);
        }

        return metadata;
    }

    /**
     * Enhances the metadata of a document.
     *
     * @param existingMetadata the existing metadata
     * @return enhanced metadata
     */
    private Map<String, Object> enhanceMetadata(Map<String, Object> existingMetadata) {
        // This method is called by the MarkdownDocumentReader for each document
        // We can add additional metadata here if needed
        return existingMetadata;
    }

    /**
     * Preprocesses the content of documents.
     *
     * @param docs the documents to preprocess
     * @param fileMetadata the file-level metadata
     * @return preprocessed documents
     */
    private List<Document> preprocessContent(List<Document> docs, Map<String, Object> fileMetadata) {
        List<Document> processedDocs = new ArrayList<>();

        for (Document doc : docs) {
            // Get the content and metadata
            String content = doc.getFormattedContent();
            Map<String, Object> metadata = new HashMap<>(doc.getMetadata());

            // Add file-level metadata
            metadata.putAll(fileMetadata);

            // Clean and normalize content
            content = cleanContent(content);

            // Create a new document with the processed content and enhanced metadata
            Document processedDoc = new Document(doc.getId(), content, metadata);
            processedDocs.add(processedDoc);
        }

        return processedDocs;
    }

    /**
     * Cleans and normalizes markdown content.
     *
     * @param content the content to clean
     * @return cleaned content
     */
    private String cleanContent(String content) {
        // Remove extra whitespace
        content = content.replaceAll("\\s+", " ");

        // Normalize line breaks
        content = content.replaceAll("\\n{3,}", "\n\n");

        // Ensure headers have space after #
        content = content.replaceAll("(#{1,6})([^\\s#])", "$1 $2");

        return content;
    }
}
