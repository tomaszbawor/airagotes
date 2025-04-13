package sh.tbawor.airagotes.confluence;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ConfluenceRestClient {

    private final static Logger log = LoggerFactory.getLogger(ConfluenceRestClient.class);

    private final WebClient confluenceWebClient;


    public ConfluenceRestClient(WebClient confluenceWebClient){
        this.confluenceWebClient = confluenceWebClient;
    }

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
            }

            Map<String, Object> links = (Map<String, Object>) response.get("_links");
            nextUrl = links.get("next") != null ? (String) links.get("next") : null;
        }

        return pages;
    }
}
