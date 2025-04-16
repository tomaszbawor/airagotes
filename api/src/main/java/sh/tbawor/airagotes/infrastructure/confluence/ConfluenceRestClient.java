package sh.tbawor.airagotes.infrastructure.confluence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import sh.tbawor.airagotes.domain.model.ConfluencePage;
import sh.tbawor.airagotes.domain.port.ConfluenceClient;

@Component
public class ConfluenceRestClient implements ConfluenceClient {

  private final static Logger log = LoggerFactory.getLogger(ConfluenceRestClient.class);

  private final WebClient confluenceWebClient;

  public ConfluenceRestClient(WebClient confluenceWebClient) {
    this.confluenceWebClient = confluenceWebClient;
  }

  @Override
  public List<ConfluencePage> getPagesFromSpace(String spaceKey, int limit) {
    List<ConfluencePage> pages = new ArrayList<>();
    String nextUrl = "/rest/api/content?type=page&spaceKey=" + spaceKey + "&limit=" + limit + "&expand=body.view";

    while (nextUrl != null) {
      Map<String, Object> response = confluenceWebClient
          .get()
          .uri(nextUrl)
          .retrieve()
          .bodyToMono(Map.class)
          .block();

      List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
      for (Map<String, Object> result : results) {
        String id = (String) result.get("id");
        String title = (String) result.get("title");

        Map<String, Object> body = (Map<String, Object>) result.get("body");
        Map<String, Object> view = (Map<String, Object>) body.get("view");
        String html = (String) view.get("value");
        String plainText = Jsoup.parse(html).text();

        pages.add(new ConfluencePage(id, title, plainText));
        log.debug("Added page: {} - {}", title, id);
      }

      Map<String, Object> links = (Map<String, Object>) response.get("_links");
      nextUrl = links.get("next") != null ? (String) links.get("next") : null;
    }
    log.info("Completed scraping {} Confluence pages from space {}", pages.size(), spaceKey);
    return pages;
  }
}
