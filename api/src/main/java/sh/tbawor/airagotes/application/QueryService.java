package sh.tbawor.airagotes.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.model.Query;
import sh.tbawor.airagotes.domain.model.QueryResponse;
import sh.tbawor.airagotes.domain.port.ChatService;
import sh.tbawor.airagotes.domain.port.DocumentRepository;

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

    public QueryService(DocumentRepository documentRepository, ChatService chatService) {
        this.documentRepository = documentRepository;
        this.chatService = chatService;
    }

    /**
     * Processes a query using RAG.
     *
     * @param query the query to process
     * @return the query response
     */
    public QueryResponse processQuery(Query query) {
        log.info("Processing query: {}", query.getText());

        // Retrieve relevant documents
        List<Document> relevantDocuments = documentRepository.findSimilarDocuments(
                query.getText(), query.getTopK());

        // Format context from documents
        String context = formatContext(relevantDocuments);

        // Generate response using the chat service
        String answer = chatService.generateResponse(query.getText(), context);

        return new QueryResponse(answer, relevantDocuments);
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
