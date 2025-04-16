package sh.tbawor.airagotes.infrastructure.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.port.DocumentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the DocumentRepository interface using Spring AI's VectorStore.
 * This is an adapter in the hexagonal architecture that implements the port defined in the domain.
 */
@Repository
public class VectorStoreDocumentRepository implements DocumentRepository {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreDocumentRepository.class);

    private final VectorStore vectorStore;

    public VectorStoreDocumentRepository(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void addDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            log.warn("No documents to add to vector store");
            return;
        }

        log.info("Adding {} documents to vector store", documents.size());

        List<org.springframework.ai.document.Document> springDocuments = documents.stream()
                .map(this::mapToSpringDocument)
                .collect(Collectors.toList());

        vectorStore.add(springDocuments);
    }

    @Override
    public List<Document> findSimilarDocuments(String query, int topK) {
        SearchRequest request = SearchRequest.builder().query(query).topK(topK).build();
        List<org.springframework.ai.document.Document> springDocuments = vectorStore.similaritySearch(request);

        return springDocuments.stream()
                .map(this::mapToDomainDocument)
                .collect(Collectors.toList());
    }

    private org.springframework.ai.document.Document mapToSpringDocument(Document document) {
        return new org.springframework.ai.document.Document(
                document.getId() != null ? document.getId() : UUID.randomUUID().toString(),
                document.getContent(),
                document.getMetadata()
        );
    }

    private Document mapToDomainDocument(org.springframework.ai.document.Document springDocument) {
        return new Document(
                springDocument.getId(),
                springDocument.getFormattedContent(),
                springDocument.getMetadata()
        );
    }
}
