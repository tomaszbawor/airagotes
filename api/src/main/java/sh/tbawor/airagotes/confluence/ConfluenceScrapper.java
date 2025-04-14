package sh.tbawor.airagotes.confluence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sh.tbawor.airagotes.documents.DocumentIngestionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(value = { "confluence.scrap" }, havingValue = "true")
public class ConfluenceScrapper implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceScrapper.class);

    private final List<String> spaces;
    private final ConfluenceRestClient confluenceRestClient;
    private final DocumentIngestionService documentIngestionService;

    public ConfluenceScrapper(ConfluenceRestClient confluenceRestClient,
            DocumentIngestionService documentIngestionService, @Value("${confluence.spaces}") String spaces) {
        this.confluenceRestClient = confluenceRestClient;
        this.documentIngestionService = documentIngestionService;
        this.spaces = List.of(spaces.split(","));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting Confluence scraping for spaces {}...", spaces);

        for (String space : spaces) {
            var pages = confluenceRestClient.getPagesFromSpace(space, 10);

            log.info("Retrieved {} Confluence pages. Processing for vector store...", pages.size());
            List<Document> documents = processConfluencePages(pages, space);

            log.info("Adding {} document chunks to vector store", documents.size());
            documentIngestionService.addDocumentsToVectorStore(documents);
        }

        log.info("Confluence content successfully ingested into vector store");

    }

    private List<Document> processConfluencePages(List<ConfluencePage> pages, String spaceName) {
        List<Document> allDocuments = new ArrayList<>();

        // Initialize text splitter for chunking
        TokenTextSplitter splitter = new TokenTextSplitter();

        for (ConfluencePage page : pages) {
            log.debug("Processing page: {}", page.getTitle());

            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", "confluence");
            metadata.put("space", spaceName);
            metadata.put("pageId", page.getId());
            metadata.put("title", page.getTitle());

            // Create initial document
            Document doc = new Document(page.getContent(), metadata);

            // Split into manageable chunks
            List<Document> splitDocs = splitter.apply(List.of(doc));
            allDocuments.addAll(splitDocs);
        }

        return allDocuments;
    }
}
