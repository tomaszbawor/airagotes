package sh.tbawor.airagotes.ollama;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  public OllamaApi ollamaApi() {
    return new OllamaApi("http://localhost:11434");
  }

  @Bean
  public OllamaChatModel chatModel(OllamaApi ollamaApi) {
    OllamaOptions options = new OllamaOptions();
    options.setModel("gemma3:4b");

    OllamaChatModel chatModel = OllamaChatModel
        .builder()
        .ollamaApi(ollamaApi)
        .defaultOptions(options).build();
    return chatModel;

  }
}
