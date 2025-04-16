package sh.tbawor.airagotes.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.model.Query;
import sh.tbawor.airagotes.domain.model.QueryResponse;
import sh.tbawor.airagotes.domain.port.ChatService;
import sh.tbawor.airagotes.domain.port.DocumentRepository;
import sh.tbawor.airagotes.domain.port.RerankerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class QueryServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private RerankerService rerankerService;

    private QueryService queryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        queryService = new QueryService(documentRepository, chatService, rerankerService);
    }

    @Test
    void shouldRerankResultsFromVectorStore() {
        // given
        String queryText = "java programming";
        int topK = 3;
        Query query = new Query(queryText, topK);

        // Vector store documents
        List<Document> vectorStoreDocuments = new ArrayList<>();
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("source", "vector_store");
        Document vsDoc1 = new Document("vs1", "Java is a programming language", metadata1);
        Document vsDoc2 = new Document("vs2", "Python is another programming language", metadata1);
        vectorStoreDocuments.add(vsDoc1);
        vectorStoreDocuments.add(vsDoc2);

        // Reranked documents
        List<Document> rerankedDocuments = new ArrayList<>();
        rerankedDocuments.add(vsDoc1);
        rerankedDocuments.add(vsDoc2);

        // Mock repository and services
        when(documentRepository.findSimilarDocuments(anyString(), anyInt())).thenReturn(vectorStoreDocuments);
        when(rerankerService.rerank(anyList(), anyString())).thenReturn(rerankedDocuments);
        when(chatService.generateResponse(anyString(), anyString())).thenReturn("This is a response about Java programming.");

        // when
        QueryResponse response = queryService.processQuery(query);

        // then
        assertNotNull(response);
        assertEquals("This is a response about Java programming.", response.getAnswer());
        assertEquals(2, response.getSourcesCount());
        assertEquals("vs1", response.getSources().get(0).getId());
        assertEquals("vs2", response.getSources().get(1).getId());
    }

    @Test
    void shouldLimitResultsToTopK() {
        // given
        String queryText = "java programming";
        int topK = 1;
        Query query = new Query(queryText, topK);

        // Vector store documents
        List<Document> vectorStoreDocuments = new ArrayList<>();
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("source", "vector_store");
        Document vsDoc1 = new Document("vs1", "Java is a programming language", metadata1);
        Document vsDoc2 = new Document("vs2", "Python is another programming language", metadata1);
        vectorStoreDocuments.add(vsDoc1);
        vectorStoreDocuments.add(vsDoc2);

        // Reranked documents (more than topK)
        List<Document> rerankedDocuments = new ArrayList<>();
        rerankedDocuments.add(vsDoc1);
        rerankedDocuments.add(vsDoc2);

        // Mock repository and services
        when(documentRepository.findSimilarDocuments(anyString(), anyInt())).thenReturn(vectorStoreDocuments);
        when(rerankerService.rerank(anyList(), anyString())).thenReturn(rerankedDocuments);
        when(chatService.generateResponse(anyString(), anyString())).thenReturn("This is a response about Java programming.");

        // when
        QueryResponse response = queryService.processQuery(query);

        // then
        assertNotNull(response);
        assertEquals(topK, response.getSourcesCount()); // Should be limited to topK
    }
}
