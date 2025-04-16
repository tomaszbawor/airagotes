package sh.tbawor.airagotes.domain.port;

import sh.tbawor.airagotes.domain.model.Document;

import java.util.List;

/**
 * Repository interface for document operations.
 * This is a port in the hexagonal architecture that defines the contract for document storage and retrieval.
 */
public interface DocumentRepository {

    /**
     * Adds documents to the repository.
     *
     * @param documents the documents to add
     */
    void addDocuments(List<Document> documents);

    /**
     * Performs a similarity search for documents matching the given query.
     *
     * @param query the query text
     * @param topK the maximum number of results to return
     * @return a list of documents matching the query
     */
    List<Document> findSimilarDocuments(String query, int topK);
}
