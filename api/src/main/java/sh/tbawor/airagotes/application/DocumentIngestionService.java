package sh.tbawor.airagotes.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.port.DocumentReader;
import sh.tbawor.airagotes.domain.port.DocumentRepository;

import java.util.List;

/**
 * Application service for document ingestion.
 * This service orchestrates the process of reading documents and storing them in the repository.
 */
@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final DocumentRepository documentRepository;

    public DocumentIngestionService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Ingests documents from a reader into the repository.
     *
     * @param reader the document reader
     * @return the number of documents ingested
     */
    public int ingestDocuments(DocumentReader reader) {
        log.info("Ingesting documents from reader");

        List<Document> documents = reader.readDocuments();

        if (documents.isEmpty()) {
            log.warn("No documents to ingest");
            return 0;
        }

        log.info("Adding {} documents to repository", documents.size());
        documentRepository.addDocuments(documents);

        return documents.size();
    }
}
