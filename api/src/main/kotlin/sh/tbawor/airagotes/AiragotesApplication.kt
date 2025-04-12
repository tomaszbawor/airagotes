package sh.tbawor.airagotes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AiragotesApplication

fun main(args: Array<String>) {
	runApplication<AiragotesApplication>(*args)
}
