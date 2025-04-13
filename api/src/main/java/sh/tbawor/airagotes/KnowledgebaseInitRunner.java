package sh.tbawor.airagotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = { "knowledgebase.init" }, havingValue = "true")
public class KnowledgebaseInitRunner implements ApplicationRunner {

  private static Logger log = LoggerFactory.getLogger(KnowledgebaseInitRunner.class);

  private String notesFolder;

  public KnowledgebaseInitRunner(@Value("${knowledgebase.folder}") String notesFolder) {
    this.notesFolder = notesFolder;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Initialising Knowledgebase from folder {}", this.notesFolder);
  }

}
