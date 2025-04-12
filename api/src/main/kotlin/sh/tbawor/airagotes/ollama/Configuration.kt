package sh.tbawor.airagotes.ollama

import org.slf4j.LoggerFactory
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration (
){
    private val logger = LoggerFactory.getLogger(Configuration::class.java)


    @Bean
    fun ollamaApi(): OllamaApi {
        logger.warn("Initializing Ollama API")
        return OllamaApi("http://localhost:11434")
    }

}