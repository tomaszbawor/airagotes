package sh.tbawor.airagotes.api;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import sh.tbawor.airagotes.documents.DocumentRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

  // Template for the system prompt
  private static final String SYSTEM_PROMPT = """
      You are an AI assistant that helps answer questions based on the provided context.
      Use the following pieces of context to answer the question at the end.
      If you don't know the answer, just say that you don't know, don't try to make up an answer.

      Context:
      {context}

      Answer the question in a comprehensive and informative way.
      """;
  private final DocumentRepository documentRepository;
  private final ChatClient chatClient;

  public RagController(DocumentRepository documentRepository, ChatClient chatClient) {
    this.documentRepository = documentRepository;
    this.chatClient = chatClient;
  }

  @PostMapping("/query")
  public QueryResponse query(@RequestBody QueryRequest request,
      @RequestParam(defaultValue = "10") int topK) {

    // Retrieve relevant documents from the vector store
    List<Document> relevantDocs = documentRepository.similiaritySearchWithTopK(request.query(), topK);

    // Extract and format the document content for the prompt
    String context = relevantDocs.stream()
        .map(Document::getFormattedContent)
        .reduce((a, b) -> a + "\n\n" + b)
        .orElse("No relevant information found.");

    Message systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT)
        .createMessage(Map.of("context", context));
    Message userMessage = new UserMessage(request.query());
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

    var response = chatClient.prompt(prompt).call();

    var sources = relevantDocs.stream()
        .map(document -> new Source(
            document.getFormattedContent(),
            document.getMetadata()))
        .toList();

    return new QueryResponse(
        response.chatResponse().getResult().getOutput().getText(),
        relevantDocs.size(),
        sources);
  }

  public record QueryResponse(
      String answer,
      int sourcesCount,
      List<Source> sources) {
  }

  record Source(
      String content,
      Map<String, Object> metadata) {
  }

  public record QueryRequest(String query) {
  }

}
