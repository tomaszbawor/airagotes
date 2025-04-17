package sh.tbawor.airagotes.infrastructure.confluence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class ConfluenceClientConfig {

  @Value("${confluence.url}")
  private String baseUrl;

  @Value("${confluence.email}")
  private String email;

  @Value("${confluence.token}")
  private String token;

  @Value("${confluence.cookie}")
  private String cookie;

  @Value("${confluence.max-buffer-size:10485760}")
  private int maxBufferSize; // Default to 10MB

  @Bean
  public WebClient confluenceWebClient() {
    // Configure exchange strategies with increased buffer size
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxBufferSize))
        .build();
    return WebClient.builder().baseUrl(baseUrl)
        .exchangeStrategies(exchangeStrategies)
        .defaultHeaders(headers -> {
          headers.setBasicAuth(email, token);
          headers.setAccept(List.of(MediaType.APPLICATION_JSON));
          headers.set("COOKIE", cookie);
        })
        .build();
  }
}
