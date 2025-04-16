package sh.tbawor.airagotes.domain.model;

import java.util.List;

/**
 * Represents a response to a query in the domain model.
 * This is a core domain entity that represents the response to a user query.
 */
public class QueryResponse {
    private final String answer;
    private final List<Document> sources;

    public QueryResponse(String answer, List<Document> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public List<Document> getSources() {
        return sources;
    }

    public int getSourcesCount() {
        return sources.size();
    }
}
