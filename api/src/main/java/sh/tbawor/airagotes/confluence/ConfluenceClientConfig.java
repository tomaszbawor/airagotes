package sh.tbawor.airagotes.confluence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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

    @Bean
    public WebClient confluenceWebClient() {
        return WebClient.builder().baseUrl(baseUrl)
                .defaultHeaders(headers ->{
                    headers.setBasicAuth(email, token) ;
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON)) ;
                    headers.set("COOKIE", cookie);
                } )
                .build();
    }
}
