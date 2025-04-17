package sh.tbawor.airagotes.domain.port;

import sh.tbawor.airagotes.domain.model.Document;

import java.util.List;

/**
 * Service interface for reading documents from various sources.
 * This is a port in the hexagonal architecture that defines the contract for document reading operations.
 */
public interface DocumentReader {

    /**
     * Reads documents from a source.
     *
     * @return a list of documents
     */
    List<Document> readDocuments();
}
