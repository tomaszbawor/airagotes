package sh.tbawor.airagotes.domain.port;

import sh.tbawor.airagotes.domain.model.Document;

import java.util.List;

/**
 * Service interface for reranking operations.
 * This is a port in the hexagonal architecture that defines the contract for reranking documents.
 */
public interface RerankerService {

    /**
     * Reranks a list of documents based on their relevance to the query.
     *
     * @param documents the documents to rerank
     * @param query the query text
     * @return a reranked list of documents
     */
    List<Document> rerank(List<Document> documents, String query);
}
