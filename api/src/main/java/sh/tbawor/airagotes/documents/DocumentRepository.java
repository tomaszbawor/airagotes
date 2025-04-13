package sh.tbawor.airagotes.documents;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRepository {

  private final VectorStore vectorStore;

  public DocumentRepository(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public List<Document> similiaritySearchWithTopK(String prompt, int topK) {
    SearchRequest request = SearchRequest.builder().query(prompt).topK(topK).build();

    var documents = vectorStore.similaritySearch(request);
    return documents;
  }

}
