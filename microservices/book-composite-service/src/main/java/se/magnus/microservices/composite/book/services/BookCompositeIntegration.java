package se.magnus.microservices.composite.book.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.api.core.comment.*;
import se.magnus.api.core.rating.*;

import se.magnus.util.http.ServiceUtil;
import se.magnus.util.http.*;
import se.magnus.util.exceptions.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import static reactor.core.publisher.Flux.empty;
import se.magnus.util.http.HttpErrorInfo;
import org.springframework.messaging.MessageChannel;
import se.magnus.api.event.Event;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(BookCompositeIntegration.MessageSources.class)
@Component
public class BookCompositeIntegration implements BookService, BookThemeNightService, CommentService, RatingService {
	
	private final ObjectMapper mapper;
	
	private final String bookServiceUrl = "http://book";
	private final String bookThemeNightServiceUrl = "http://bookthemenight";
	private final String commentServiceUrl = "http://comment";
	private final String ratingServiceUrl = "http://rating";
    private WebClient webClient;
    private WebClient.Builder webClientBuilder;
    private MessageSources messageSources;
    
    public interface MessageSources {

        String OUTPUT_BOOKS = "output-books";
        String OUTPUT_COMMENTS = "output-comments";
        String OUTPUT_BOOK_THEME_NIGHTS = "output-book-theme-nights";
        String OUTPUT_RATINGS = "output-ratings";

        @Output(OUTPUT_BOOKS)
        MessageChannel outputBooks();

        @Output(OUTPUT_COMMENTS)
        MessageChannel outputComments();

        @Output(OUTPUT_BOOK_THEME_NIGHTS)
        MessageChannel outputBookThemeNights();
        
        @Output(OUTPUT_RATINGS)
        MessageChannel outputRatings();
    }
	

	private static final Logger LOG = LoggerFactory.getLogger(BookCompositeIntegration.class);
	
	@Autowired
	public BookCompositeIntegration(WebClient.Builder webClientBuilder, ObjectMapper mapper, MessageSources messageSources) {
		this.mapper = mapper;
		this.webClientBuilder = webClientBuilder;
		this.messageSources = messageSources;
	}
	
	@Override
	public Mono<BookModel> getBook(int bookId) {
		String url = bookServiceUrl + "/book/" + bookId;
        LOG.debug("Will call the getBook API on URL: {}", url);

        return getWebClient().get().uri(url).retrieve().bodyToMono(BookModel.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}
	
	@Override
	public BookModel createBook(BookModel body) {
	       messageSources.outputBooks().send(MessageBuilder.withPayload(new Event(CREATE, body.getBookId(), body)).build());
	        return body;
	}
	
	@Override
	public void deleteBook(int bookId) {
        messageSources.outputBooks().send(MessageBuilder.withPayload(new Event(DELETE, bookId, null)).build());
	}
	
	@Override
    public BookThemeNight createBookThemeNight(BookThemeNight body) {
        messageSources.outputBookThemeNights().send(MessageBuilder.withPayload(new Event(CREATE, body.getBookId(), body)).build());
        return body;
    }
	
	@Override
    public Flux<BookThemeNight> getBookThemeNights(int bookId) {
        String url = bookThemeNightServiceUrl + "/book-theme-night?bookId=" + bookId;

        LOG.debug("Will call the getBookThemeNights API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(BookThemeNight.class).log().onErrorResume(error -> empty());
    }

    @Override
    public void deleteBookThemeNight(int bookId) {
        messageSources.outputBookThemeNights().send(MessageBuilder.withPayload(new Event(DELETE, bookId, null)).build());
    }
    
	@Override
    public Comment createComment(Comment body) {
        messageSources.outputComments().send(MessageBuilder.withPayload(new Event(CREATE, body.getBookId(), body)).build());
        return body;
    }
	
	@Override
    public Flux<Comment> getComments(int bookId) {

        String url = commentServiceUrl + "/comment?bookId=" + bookId;

        LOG.debug("Will call the getComments API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Comment.class).log().onErrorResume(error -> empty());
    }

    @Override
    public void deleteComment(int bookId) {
        messageSources.outputComments().send(MessageBuilder.withPayload(new Event(DELETE, bookId, null)).build());
    }
    
	@Override
    public Rating createRating(Rating body) {
        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(CREATE, body.getBookId(), body)).build());
        return body;
    }
	
	@Override
    public Flux<Rating> getRatings(int bookId) {
        String url = ratingServiceUrl + "/rating?bookId=" + bookId;

        LOG.debug("Will call the getRatings API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Rating.class).log().onErrorResume(error -> empty());

    }

    @Override
    public void deleteRating(int bookId) {
        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(DELETE, bookId, null)).build());
    }
	
    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
    
    public Mono<Health> getBookHealth() {
        return getHealth(bookServiceUrl);
    }

    public Mono<Health> getCommentHealth() {
        return getHealth(commentServiceUrl);
    }
    
    public Mono<Health> getBookThemeNightHealth() {
        return getHealth(bookThemeNightServiceUrl);
    }

    public Mono<Health> getRatingHealth() {
        return getHealth(ratingServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log();
    }
	
	private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage(wcre));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage(wcre));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
            LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
            return ex;
        }
    }
	
    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }
	
}
