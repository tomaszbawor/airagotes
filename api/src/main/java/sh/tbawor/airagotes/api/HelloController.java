package sh.tbawor.airagotes.api;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final OllamaChatModel chatModel;

  HelloController(OllamaChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @GetMapping("/hello")
  public String hello() {
    var response = chatModel.call("Hello, how are you?");
    return response;
  }
}
