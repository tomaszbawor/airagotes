package sh.tbawor.airagotes.domain.model;

/**
 * Represents a query in the domain model.
 * This is a core domain entity that represents a user query.
 */
public class Query {
    private final String text;
    private final int topK;

    public Query(String text, int topK) {
        this.text = text;
        this.topK = topK;
    }

    public String getText() {
        return text;
    }

    public int getTopK() {
        return topK;
    }
}
