package sh.tbawor.airagotes.domain.model;

import java.util.Map;

/**
 * Represents a document in the domain model.
 * This is a core domain entity that represents a document with content and metadata.
 */
public class Document {
    private final String id;
    private final String content;
    private final Map<String, Object> metadata;

    public Document(String id, String content, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
