package sh.tbawor.airagotes.domain.port;

/**
 * Service interface for chat operations.
 * This is a port in the hexagonal architecture that defines the contract for chat operations.
 */
public interface ChatService {

    /**
     * Generates a response to the given query with the provided context.
     *
     * @param query the user query
     * @param context the context to use for generating the response
     * @return the generated response
     */
    String generateResponse(String query, String context);
}
