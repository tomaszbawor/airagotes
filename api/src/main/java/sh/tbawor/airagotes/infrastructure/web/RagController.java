package sh.tbawor.airagotes.infrastructure.web;

import org.springframework.web.bind.annotation.*;

import sh.tbawor.airagotes.application.QueryService;
import sh.tbawor.airagotes.domain.model.Document;
import sh.tbawor.airagotes.domain.model.Query;
import sh.tbawor.airagotes.domain.model.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for RAG (Retrieval-Augmented Generation) operations.
 * This is an adapter in the hexagonal architecture that exposes the application services via HTTP.
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final QueryService queryService;

    public RagController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/query")
    public QueryResponseDto query(@RequestBody QueryRequestDto request,
                               @RequestParam(defaultValue = "10") int topK) {

        Query query = new Query(request.query(), topK);
        QueryResponse response = queryService.processQuery(query);

        return mapToDto(response);
    }

    private QueryResponseDto mapToDto(QueryResponse response) {
        List<SourceDto> sources = response.getSources().stream()
                .map(document -> new SourceDto(
                        document.getContent(),
                        document.getMetadata()))
                .collect(Collectors.toList());

        return new QueryResponseDto(
                response.getAnswer(),
                response.getSourcesCount(),
                sources);
    }

    public record QueryResponseDto(
            String answer,
            int sourcesCount,
            List<SourceDto> sources) {
    }

    public record SourceDto(
            String content,
            Map<String, Object> metadata) {
    }

    public record QueryRequestDto(String query) {
    }
}
