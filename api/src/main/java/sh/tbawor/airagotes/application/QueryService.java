package sh.tbawor.airagotes.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.model.Query;
import sh.tbawor.airagotes.domain.model.QueryResponse;
import sh.tbawor.airagotes.domain.port.ChatService;
import sh.tbawor.airagotes.domain.port.DocumentRepository;
import sh.tbawor.airagotes.domain.port.RerankerService;

import java.util.List;

/**
 * Application service for handling queries.
 * This service orchestrates the RAG (Retrieval-Augmented Generation) process.
 */
@Service
public class QueryService {

    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

    private final DocumentRepository documentRepository;
    private final ChatService chatService;
    private final RerankerService rerankerService;

    public QueryService(DocumentRepository documentRepository, ChatService chatService,
                        RerankerService rerankerService) {
        this.documentRepository = documentRepository;
        this.chatService = chatService;
        this.rerankerService = rerankerService;
    }

    /**
     * Processes a query using RAG.
     *
     * @param query the query to process
     * @return the query response
     */
    public QueryResponse processQuery(Query query) {
        log.info("Processing query: {}", query.getText());

        // Retrieve relevant documents from vector store
        List<Document> vectorStoreDocuments = documentRepository.findSimilarDocuments(
                query.getText(), query.getTopK());
        log.info("Retrieved {} documents from vector store", vectorStoreDocuments.size());

        // Add source metadata to vector store documents
        vectorStoreDocuments.forEach(doc -> {
            if (doc.getMetadata() != null) {
                doc.getMetadata().put("source", "vector_store");
            }
        });

        // Rerank results
        List<Document> rerankedDocuments = rerankerService.rerank(
                vectorStoreDocuments, query.getText());
        log.info("Reranked {} documents", rerankedDocuments.size());

        // Limit to topK
        List<Document> finalDocuments = rerankedDocuments.stream()
                .limit(query.getTopK())
                .collect(java.util.stream.Collectors.toList());
        log.info("Final document count: {}", finalDocuments.size());

        // Format context from documents
        String context = formatContext(finalDocuments);

        // Generate response using the chat service
        String answer = chatService.generateResponse(query.getText(), context);

        return new QueryResponse(answer, finalDocuments);
    }

    private String formatContext(List<Document> documents) {
        if (documents.isEmpty()) {
            return "No relevant information found.";
        }

        return documents.stream()
                .map(Document::getContent)
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("");
    }
}
