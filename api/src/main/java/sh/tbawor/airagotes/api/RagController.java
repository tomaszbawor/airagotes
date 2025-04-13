package sh.tbawor.airagotes.api;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sh.tbawor.airagotes.documents.DocumentRepository;

@RestController
@RequestMapping("/api/rag")
public class RagController {

  private final DocumentRepository documentRepository;
  private final ChatClient chatClient;

  // Template for the system prompt
  private static final String SYSTEM_PROMPT = """
      You are an AI assistant that helps answer questions based on the provided context.
      Use the following pieces of context to answer the question at the end.
      If you don't know the answer, just say that you don't know, don't try to make up an answer.

      Context:
      {context}

      Answer the question in a comprehensive and informative way.
      """;

  public RagController(DocumentRepository documentRepository, ChatClient chatClient) {
    this.documentRepository = documentRepository;
    this.chatClient = chatClient;
  }

  @PostMapping("/query")
  public Map<String, Object> query(@RequestBody QueryRequest request,
      @RequestParam(defaultValue = "5") int topK) {

    // 1. Retrieve relevant documents from the vector store
    List<Document> relevantDocs = documentRepository.similiaritySearchWithTopK(request.query(), topK);

    // 2. Extract and format the document content for the prompt
    String context = relevantDocs.stream()
        .map(Document::getFormattedContent)
        .reduce((a, b) -> a + "\n\n" + b)
        .orElse("No relevant information found.");

    // 3. Create the system prompt with the context
    Message systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT)
        .createMessage(Map.of("context", context));

    // 4. Create the user message with the query
    Message userMessage = new UserMessage(request.query());

    // 5. Create the prompt with both messages
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

    // 6. Generate the response

    var response = chatClient.prompt(prompt).call();

    // 7. Return the response with some metadata
    return Map.of(
        "answer", response.chatResponse(),
        "sourcesCount", relevantDocs.size(),
        "sources", relevantDocs.stream()
            .map(doc -> Map.of(
                "content",
                doc.getFormattedContent().substring(0, Math.min(doc.getFormattedContent().length(), 200)) + "...",
                "metadata", doc.getMetadata()))
            .toList());
  }

  // Request record
  public record QueryRequest(String query) {
  }
}
