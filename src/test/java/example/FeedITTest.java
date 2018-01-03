package example;

import java.util.Map;
import java.util.Collections;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.Credentials.basicAuthenticationCredentials;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import example.model.Feed;
import example.repository.FeedRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FeedITTest {

	// @Autowired
	private WebTestClient webTestClient;

	@Autowired
    FeedRepository feedRepository;

    @Autowired
    ApplicationContext context;

    @Before
    public void setUp() throws Exception {
        webTestClient = WebTestClient
            .bindToApplicationContext(context)
            .apply(springSecurity())
            .configureClient()
            .filter(basicAuthentication())
            .baseUrl("http://localhost:8080/")
            .build();
    }

    private Consumer<Map<String, Object>> adminCredentials() {
		return basicAuthenticationCredentials("admin", "password");
    }

    private Consumer<Map<String, Object>> userCredentials() {
		return basicAuthenticationCredentials("user", "password");
    }

    @Test
    public void testGetAllFeeds() {
	    webTestClient.get().uri("/feeds")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .attributes(userCredentials())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Feed.class);
    }

    @Test
    public void testGetFeed() {
        Feed feed = feedRepository.save(new Feed("Running", "Hello, World!")).block();

        webTestClient.get()
            .uri("/feeds/{id}", Collections.singletonMap("id", feed.getId()))
            .attributes(userCredentials())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(response ->
                Assertions.assertThat(response.getResponseBody()).isNotNull());
    }

    @Test
	public void testCreateFeed() {
		Feed feed = new Feed("This is a Test Feed", "Hello world");

        webTestClient
            .mutateWith(csrf())
            .post().uri("/feeds")
            .attributes(adminCredentials())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(feed), Feed.class)
            .exchange().expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.title").isEqualTo("This is a Test Feed")
            .jsonPath("$.body").isEqualTo("Hello world");
	}

    @Test
    public void testUpdateFeed() {
        Feed feed = feedRepository.save(new Feed("Initial Feed", "Spring Boot")).block();
        Feed newFeedData = new Feed("Updated Feed", "Spring Boot Example");

        webTestClient
            .mutateWith(csrf())
            .put()
            .uri("/feeds/{id}", Collections.singletonMap("id", feed.getId()))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(newFeedData), Feed.class)
            .attributes(adminCredentials())
            .exchange().expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Updated Feed");
    }

    @Test
    public void testDeleteFeed() {
	    Feed feed = feedRepository.save(new Feed("To be deleted", "Spring Example")).block();

        webTestClient
            .mutateWith(csrf())
            .delete()
            .uri("/feeds/{id}", Collections.singletonMap("id",  feed.getId()))
            .attributes(adminCredentials())
            .exchange().expectStatus().isOk();
    }

}
