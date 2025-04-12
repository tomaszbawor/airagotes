package sh.tbawor.airagotes.api;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final OllamaApi ollamaApi;

  HelloController(OllamaApi ollamaApi) {
    this.ollamaApi = ollamaApi;
  }

  @GetMapping("/hello")
  public String hello() {
    OllamaOptions options = new OllamaOptions();
    options.setModel("gemma3:4b");

    OllamaChatModel chatModel = OllamaChatModel.builder().ollamaApi(this.ollamaApi).defaultOptions(options).build();

    var response = chatModel.call("Hello, how are you?");

    return response;
  }
}
