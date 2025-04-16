package sh.tbawor.airagotes.infrastructure.reranking;

import org.junit.jupiter.api.Test;
import sh.tbawor.airagotes.domain.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleRerankerServiceTest {

    @Test
    void shouldReorderDocumentsBasedOnKeywordMatches() {
        // given
        SimpleRerankerService rerankerService = new SimpleRerankerService();

        List<Document> documents = new ArrayList<>();

        // Vector store document with no keyword matches
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("source", "vector_store");
        Document doc1 = new Document("1", "This is a document about something else", metadata1);

        // Vector store document with some keyword matches
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("source", "vector_store");
        Document doc2 = new Document("2", "Java programming is fun and powerful", metadata2);

        // Vector store document with more keyword matches
        Map<String, Object> metadata3 = new HashMap<>();
        metadata3.put("source", "vector_store");
        Document doc3 = new Document("3", "Java is a popular programming language for developers", metadata3);

        documents.add(doc1);
        documents.add(doc2);
        documents.add(doc3);

        String query = "java programming developers";

        // when
        List<Document> rerankedDocuments = rerankerService.rerank(documents, query);

        // then
        assertNotNull(rerankedDocuments);
        assertEquals(3, rerankedDocuments.size());

        // Vector store document with more keyword matches should be first
        assertEquals("3", rerankedDocuments.get(0).getId());

        // Vector store document with some keyword matches should be second
        assertEquals("2", rerankedDocuments.get(1).getId());

        // Vector store document with no keyword matches should be last
        assertEquals("1", rerankedDocuments.get(2).getId());
    }

    @Test
    void shouldHandleEmptyDocumentList() {
        // given
        SimpleRerankerService rerankerService = new SimpleRerankerService();
        List<Document> documents = new ArrayList<>();
        String query = "test query";

        // when
        List<Document> rerankedDocuments = rerankerService.rerank(documents, query);

        // then
        assertNotNull(rerankedDocuments);
        assertEquals(0, rerankedDocuments.size());
    }

    @Test
    void shouldHandleDocumentsWithNoMetadata() {
        // given
        SimpleRerankerService rerankerService = new SimpleRerankerService();

        List<Document> documents = new ArrayList<>();
        Document doc1 = new Document("1", "This is a document with no metadata", null);
        Document doc2 = new Document("2", "This is another document with no metadata", null);
        documents.add(doc1);
        documents.add(doc2);

        String query = "document metadata";

        // when
        List<Document> rerankedDocuments = rerankerService.rerank(documents, query);

        // then
        assertNotNull(rerankedDocuments);
        assertEquals(2, rerankedDocuments.size());
        // Documents should be ranked by keyword matches since they have no source metadata
        assertEquals("1", rerankedDocuments.get(0).getId());
        assertEquals("2", rerankedDocuments.get(1).getId());
    }
}
