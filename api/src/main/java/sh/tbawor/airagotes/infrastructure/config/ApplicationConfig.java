package sh.tbawor.airagotes.infrastructure.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sh.tbawor.airagotes.domain.port.ChatService;
import sh.tbawor.airagotes.domain.port.DocumentRepository;
import sh.tbawor.airagotes.infrastructure.ai.OllamaChatService;
import sh.tbawor.airagotes.infrastructure.persistence.VectorStoreDocumentRepository;

/**
 * Configuration for the application.
 * This class configures the beans for the application, including the infrastructure adapters.
 */
@Configuration
public class ApplicationConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(ollamaUrl);
    }

    @Bean
    public EmbeddingModel embeddingModel(OllamaApi ollamaApi) {
        OllamaOptions ollamaOptions = OllamaOptions.builder().model(OllamaModel.NOMIC_EMBED_TEXT).build();
        return OllamaEmbeddingModel.builder().ollamaApi(ollamaApi).defaultOptions(ollamaOptions).build();
    }

    @Bean
    public OllamaChatModel chatModel(OllamaApi ollamaApi) {
        OllamaOptions options = new OllamaOptions();
        options.setModel(ollamaModel);
        options.setTemperature(0.3); // Low Temperature to improve consistency and avoid hallucinations

        return OllamaChatModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options).build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ChatService chatService(ChatClient chatClient) {
        return new OllamaChatService(chatClient);
    }

    @Bean
    public DocumentRepository documentRepository(VectorStore vectorStore) {
        return new VectorStoreDocumentRepository(vectorStore);
    }
}
