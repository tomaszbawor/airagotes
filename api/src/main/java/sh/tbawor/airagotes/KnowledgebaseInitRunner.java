package sh.tbawor.airagotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sh.tbawor.airagotes.documents.DocumentIngestionService;

@Component
@ConditionalOnProperty(value = { "knowledgebase.init" }, havingValue = "true")
public class KnowledgebaseInitRunner implements ApplicationRunner {

  private static Logger log = LoggerFactory.getLogger(KnowledgebaseInitRunner.class);

  private final String notesFolder;
  private final DocumentIngestionService documentIngestionService;

  public KnowledgebaseInitRunner(@Value("${knowledgebase.folder}") String notesFolder,
      DocumentIngestionService documentIngestionService) {
    this.notesFolder = notesFolder;
    this.documentIngestionService = documentIngestionService;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Initialising Knowledgebase from folder {}", this.notesFolder);

    int documentsIngested = documentIngestionService.ingestFolder(notesFolder);
    log.info("Knowledgebase initialization complete. Ingested {} document chunks", documentsIngested);
  }

}
