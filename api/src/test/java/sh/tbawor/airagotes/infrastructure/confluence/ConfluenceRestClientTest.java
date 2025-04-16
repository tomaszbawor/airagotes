package sh.tbawor.airagotes.infrastructure.confluence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;
import sh.tbawor.airagotes.domain.model.ConfluencePage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfluenceRestClientTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private ResponseSpec responseSpecMock;

    private ConfluenceRestClient confluenceRestClient;

    @BeforeEach
    void setUp() {
        confluenceRestClient = new ConfluenceRestClient(webClientMock);
    }

    @Test
    void getPagesFromSpace_shouldReturnEmptyList_whenNoResults() {
        // Given
        String spaceKey = "TEST";
        int limit = 10;

        Map<String, Object> response = new HashMap<>();
        response.put("results", List.of());
        Map<String, Object> links = new HashMap<>();
        links.put("next", null);
        response.put("_links", links);

        // Mock WebClient behavior
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(response));

        // When
        List<ConfluencePage> result = confluenceRestClient.getPagesFromSpace(spaceKey, limit);

        // Then
        assertTrue(result.isEmpty());
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri("/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view");
    }

    @Test
    void getPagesFromSpace_shouldReturnPages_whenResultsExist() {
        // Given
        String spaceKey = "TEST";
        int limit = 10;

        // Create a mock response with one page
        Map<String, Object> page = new HashMap<>();
        page.put("id", "12345");
        page.put("title", "Test Page");

        Map<String, Object> view = new HashMap<>();
        view.put("value", "<p>Test content</p>");

        Map<String, Object> body = new HashMap<>();
        body.put("view", view);

        page.put("body", body);

        Map<String, Object> response = new HashMap<>();
        response.put("results", List.of(page));

        Map<String, Object> links = new HashMap<>();
        links.put("next", null);
        response.put("_links", links);

        // Mock WebClient behavior
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(response));

        // When
        List<ConfluencePage> result = confluenceRestClient.getPagesFromSpace(spaceKey, limit);

        // Then
        assertEquals(1, result.size());
        ConfluencePage resultPage = result.get(0);
        assertEquals("12345", resultPage.getId());
        assertEquals("Test Page", resultPage.getTitle());
        assertEquals("Test content", resultPage.getContent());
    }

    @Test
    void getPagesFromSpace_shouldHandlePagination_whenNextLinkExists() {
        // Given
        String spaceKey = "TEST";
        int limit = 10;

        // First page response
        Map<String, Object> page1 = new HashMap<>();
        page1.put("id", "12345");
        page1.put("title", "Test Page 1");

        Map<String, Object> view1 = new HashMap<>();
        view1.put("value", "<p>Test content 1</p>");

        Map<String, Object> body1 = new HashMap<>();
        body1.put("view", view1);

        page1.put("body", body1);

        Map<String, Object> response1 = new HashMap<>();
        response1.put("results", List.of(page1));

        Map<String, Object> links1 = new HashMap<>();
        links1.put("next", "/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view&start=10");
        response1.put("_links", links1);

        // Second page response
        Map<String, Object> page2 = new HashMap<>();
        page2.put("id", "67890");
        page2.put("title", "Test Page 2");

        Map<String, Object> view2 = new HashMap<>();
        view2.put("value", "<p>Test content 2</p>");

        Map<String, Object> body2 = new HashMap<>();
        body2.put("view", view2);

        page2.put("body", body2);

        Map<String, Object> response2 = new HashMap<>();
        response2.put("results", List.of(page2));

        Map<String, Object> links2 = new HashMap<>();
        links2.put("next", null);
        response2.put("_links", links2);

        // Setup basic WebClient mock chain
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        // Setup URI specific responses
        when(requestHeadersUriSpecMock.uri(eq("/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view")))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(eq("/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view&start=10")))
            .thenReturn(requestHeadersSpecMock);

        // Setup response for first and second calls
        when(responseSpecMock.bodyToMono(Map.class))
            .thenReturn(Mono.just(response1))
            .thenReturn(Mono.just(response2));

        // When
        List<ConfluencePage> result = confluenceRestClient.getPagesFromSpace(spaceKey, limit);

        // Then
        assertEquals(2, result.size());

        ConfluencePage resultPage1 = result.get(0);
        assertEquals("12345", resultPage1.getId());
        assertEquals("Test Page 1", resultPage1.getTitle());
        assertEquals("Test content 1", resultPage1.getContent());

        ConfluencePage resultPage2 = result.get(1);
        assertEquals("67890", resultPage2.getId());
        assertEquals("Test Page 2", resultPage2.getTitle());
        assertEquals("Test content 2", resultPage2.getContent());

        verify(webClientMock, times(2)).get();
        verify(requestHeadersUriSpecMock).uri("/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view");
        verify(requestHeadersUriSpecMock).uri("/rest/api/content?type=page&spaceKey=TEST&limit=10&expand=body.view&start=10");
    }
}
