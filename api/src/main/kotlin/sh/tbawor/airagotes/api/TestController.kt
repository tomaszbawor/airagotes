package sh.tbawor.airagotes.api

import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(val ollamaApi: OllamaApi) {

    @GetMapping("/hello")
    fun hello(): String {
        val options = OllamaOptions()
        options.model = "gemma3:4b"

        val chatClient = OllamaChatModel.builder().ollamaApi(this.ollamaApi).defaultOptions(options).build()

        val response = chatClient.call("Hello, how are you today?")
        return response
    }
}