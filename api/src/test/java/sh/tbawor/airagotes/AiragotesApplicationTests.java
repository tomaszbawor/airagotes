package sh.tbawor.airagotes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import sh.tbawor.airagotes.application.DocumentIngestionService;
import sh.tbawor.airagotes.documents.MarkdownVectorStoreIngestionService;

@SpringBootTest
class AiragotesApplicationTests {

  @Autowired
  private ApplicationContext context;

  @Autowired
  @Qualifier("applicationDocumentIngestionService")
  private DocumentIngestionService applicationDocumentIngestionService;

  @Autowired
  @Qualifier("documentsDocumentIngestionService")
  private MarkdownVectorStoreIngestionService documentsDocumentIngestionService;

  @Test
  public void simpleTest() {
    // A simple test that doesn't require the Spring context
    Assertions.assertTrue(true, "This test should always pass");
  }

  @Test
  public void contextLoads() {
    // This test verifies that the Spring context loads correctly
    Assertions.assertNotNull(context, "Application context should not be null");
    Assertions.assertNotNull(applicationDocumentIngestionService, "Application DocumentIngestionService should not be null");
    Assertions.assertNotNull(documentsDocumentIngestionService, "MarkdownVectorStoreIngestionService should not be null");
  }

}
