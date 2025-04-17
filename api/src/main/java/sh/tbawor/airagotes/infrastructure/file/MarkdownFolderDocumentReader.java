package sh.tbawor.airagotes.infrastructure.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.port.DocumentReader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the DocumentReader interface for reading markdown files from a folder.
 * This is an adapter in the hexagonal architecture that implements the port defined in the domain.
 * Uses the MarkdownProcessor for enhanced embedding with improved metadata extraction and content preprocessing.
 */
public class MarkdownFolderDocumentReader implements DocumentReader {

    private static final Logger log = LoggerFactory.getLogger(MarkdownFolderDocumentReader.class);

    private final String folderPath;
    private final MarkdownProcessor markdownProcessor;

    public MarkdownFolderDocumentReader(String folderPath) {
        this.folderPath = folderPath;
        this.markdownProcessor = new MarkdownProcessor();
    }

    @Override
    public List<Document> readDocuments() {
        log.info("Reading documents from folder: {}", folderPath);

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Folder does not exist or is not a directory: {}", folderPath);
            return List.of();
        }

        List<File> markdownFiles = findMarkdownFiles(folder);
        log.info("Found {} markdown files", markdownFiles.size());

        List<org.springframework.ai.document.Document> springDocuments = new ArrayList<>();

        for (File file : markdownFiles) {
            try {
                // Process the file using the enhanced MarkdownProcessor
                List<org.springframework.ai.document.Document> processedDocs = markdownProcessor.processFile(file);
                springDocuments.addAll(processedDocs);
                log.debug("Processed file: {}", file.getPath());
            } catch (Exception e) {
                log.error("Error processing file: {}", file.getPath(), e);
            }
        }

        log.info("Read {} document chunks", springDocuments.size());

        return springDocuments.stream()
                .map(this::mapToDomainDocument)
                .collect(Collectors.toList());
    }

    private List<File> findMarkdownFiles(File folder) {
        List<File> markdownFiles = new ArrayList<>();
        try (var paths = Files.walk(folder.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                    .forEach(path -> markdownFiles.add(path.toFile()));
        } catch (Exception e) {
            log.error("Error finding markdown files", e);
        }
        return markdownFiles;
    }

    private Document mapToDomainDocument(org.springframework.ai.document.Document springDocument) {
        return new Document(
                springDocument.getId(),
                springDocument.getFormattedContent(),
                springDocument.getMetadata()
        );
    }
}
