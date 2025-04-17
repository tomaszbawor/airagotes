package sh.tbawor.airagotes.infrastructure.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import sh.tbawor.airagotes.domain.port.ChatService;

import java.util.List;

/**
 * Implementation of the ChatService interface using Spring AI's ChatClient.
 * This is an adapter in the hexagonal architecture that implements the port defined in the domain.
 */
@Service
public class OllamaChatService implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatService.class);

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant that helps answer questions based on the provided context.
            Use the following pieces of context to answer the question at the end.
            If you don't know the answer, just say that you don't know, don't try to make up an answer.

            Context:
            %s

            Answer the question in a comprehensive and informative way.
            """;

    private final ChatClient chatClient;

    public OllamaChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateResponse(String query, String context) {
        log.info("Generating response for query: {}", query);

        Message systemMessage = new SystemMessage(String.format(SYSTEM_PROMPT, context));
        Message userMessage = new UserMessage(query);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        var response = chatClient.prompt(prompt).call();

        return response.chatResponse().getResult().getOutput().getText();
    }
}
