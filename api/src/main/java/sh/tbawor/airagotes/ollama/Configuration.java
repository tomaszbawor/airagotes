package sh.tbawor.airagotes.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  public OllamaApi ollamaApi() {
    return new OllamaApi("http://localhost:11434");
  }

  @Bean
  public EmbeddingModel embeddingModel(OllamaApi ollamaApi) {
    OllamaOptions ollamaOptions = OllamaOptions.builder().model(OllamaModel.NOMIC_EMBED_TEXT).build();
    return OllamaEmbeddingModel.builder().ollamaApi(ollamaApi).defaultOptions(ollamaOptions).build();
  }

  @Bean
  public OllamaChatModel chatModel(OllamaApi ollamaApi) {
    OllamaOptions options = new OllamaOptions();
    options.setModel("gemma3:4b");
    options.setTemperature(0.7);

      return OllamaChatModel
          .builder()
          .ollamaApi(ollamaApi)
          .defaultOptions(options).build();

  }

  @Bean
  public ChatClient chatClient(ChatModel chatModel) {
   return ChatClient.create(chatModel) ;
  }
}
