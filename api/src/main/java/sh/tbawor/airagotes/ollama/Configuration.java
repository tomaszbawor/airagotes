package sh.tbawor.airagotes.ollama;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  public OllamaApi ollamaApi() {
    return new OllamaApi("http://localhost:11434");
  }
}
