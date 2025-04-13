package sh.tbawor.airagotes.confluence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = { "confluence.scrap" }, havingValue = "true")
public class ConfluenceScrapper implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceScrapper.class);

    private final ConfluenceRestClient confluenceRestClient;

    public ConfluenceScrapper(ConfluenceRestClient confluenceRestClient) {
        this.confluenceRestClient = confluenceRestClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting Confluence scraping...");
        var pages = confluenceRestClient.getPagesFromSpace("WMS", 10);

        for (var page : pages) {
            log.info("Page: {} - Title: {}, Content: {}", page.getId(), page.getTitle(), page.getContent());
        }
    }
}
