package sh.tbawor.airagotes.documents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    public void addDocumentsToVectorStore(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            log.warn("No documents to add to vector store");
            return;
        }

        log.info("Adding {} documents to vector store", documents.size());
        vectorStore.add(documents);
    }

    /**
     * Ingests markdown documents from a folder into the vector store
     *
     * @param folderPath the path to the folder containing markdown documents
     * @return the number of documents ingested
     */
    public int ingestFolder(String folderPath) {
        log.info("Ingesting documents from folder: {}", folderPath);

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Folder does not exist or is not a directory: {}", folderPath);
            return 0;
        }

        List<File> markdownFiles = findMarkdownFiles(folder);
        log.info("Found {} markdown files", markdownFiles.size());

        List<Document> documents = new ArrayList<>();

        for (File file : markdownFiles) {
            try {
                // Create document reader for this file
                // DocumentReader reader = new MarkdownDocumentReader(file.toPath(),
                // MetadataMode.ALL);
                Resource resource = new FileSystemResource(file);
                DocumentReader reader = new MarkdownDocumentReader(resource, MarkdownDocumentReaderConfig.builder().build());
                List<Document> docs = reader.get();

                // Split documents into smaller chunks for better embedding
                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> splitDocs = splitter.apply(docs);

                documents.addAll(splitDocs);
                log.debug("Processed file: {}", file.getPath());
            } catch (Exception e) {
                log.error("Error processing file: {}", file.getPath(), e);
            }
        }

        log.info("Storing {} document chunks in vector database", documents.size());
        vectorStore.add(documents);

        return documents.size();
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
}
