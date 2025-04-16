package sh.tbawor.airagotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import sh.tbawor.airagotes.application.DocumentIngestionService;
import sh.tbawor.airagotes.domain.port.DocumentReader;
import sh.tbawor.airagotes.infrastructure.file.MarkdownFolderDocumentReaderFactory;

@Component
@ConditionalOnProperty(value = { "knowledgebase.init" }, havingValue = "true")
public class KnowledgebaseInitRunner implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(KnowledgebaseInitRunner.class);

  private final String notesFolder;
  private final DocumentIngestionService documentIngestionService;
  private final MarkdownFolderDocumentReaderFactory readerFactory;

  public KnowledgebaseInitRunner(
      @Value("${knowledgebase.folder}") String notesFolder,
      DocumentIngestionService documentIngestionService,
      MarkdownFolderDocumentReaderFactory readerFactory) {
    this.notesFolder = notesFolder;
    this.documentIngestionService = documentIngestionService;
    this.readerFactory = readerFactory;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Initialising Knowledgebase from folder {}", this.notesFolder);

    DocumentReader reader = readerFactory.createReader(notesFolder);
    int documentsIngested = documentIngestionService.ingestDocuments(reader);

    log.info("Knowledgebase initialization complete. Ingested {} document chunks", documentsIngested);
  }

}
