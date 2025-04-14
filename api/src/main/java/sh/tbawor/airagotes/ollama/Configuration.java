package sh.tbawor.airagotes.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Value("${spring.ai.ollama.base-url}")
  public String ollamaUrl;

  @Value("${spring.ai.ollama.chat.options.model}")
  public String ollamaModel;

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
}
