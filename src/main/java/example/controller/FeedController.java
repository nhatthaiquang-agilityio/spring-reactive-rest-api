package example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import example.model.Feed;
import example.repository.FeedRepository;
import example.exception.FeedNotFoundException;
import example.exception.ErrorResponse;

/**
 * Created by NhatThai.
 */
@RestController
public class FeedController {

    @Autowired
    private FeedRepository feedRepository;

    @GetMapping("/feeds")
    public Flux<Feed> getAllFeeds() {
        return feedRepository.findAll();
    }

    @PostMapping("/feeds")
    public Mono<Feed> createFeeds(@Valid @RequestBody Feed feed) {
        return feedRepository.save(feed);
    }

    @GetMapping("/feeds/{id}")
    public Mono<Feed> getFeedById(@PathVariable(value = "id") String feedId) {
        return feedRepository.findById(feedId)
                .switchIfEmpty(Mono.error(new FeedNotFoundException(feedId)));
    }

    @PutMapping("/feeds/{id}")
    public Mono<ResponseEntity<Feed>> updateFeed(
            @PathVariable(value = "id") String feedId,
            @Valid @RequestBody Feed feed) {
        return feedRepository.findById(feedId)
                .flatMap(existingFeed -> {
                    existingFeed.setTitle(feed.getTitle());
                    existingFeed.setBody(feed.getBody());
                    return feedRepository.save(existingFeed);
                })
                .map(updateFeed -> new ResponseEntity<>(updateFeed, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/feeds/{id}")
    public Mono<ResponseEntity<Void>> deleteFeed(@PathVariable(value = "id") String feedId) {

        return feedRepository.findById(feedId)
                .flatMap(existingFeed ->
                    feedRepository.delete(existingFeed)
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Feeds are Sent to the client as Server Sent Events
    @GetMapping(value = "/stream/feeds", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Feed> streamAllFeeds() {
        return feedRepository.findAll();
    }


    // Exception Handling Examples
    @ExceptionHandler
    public ResponseEntity handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse("A Feed with the same text already exists"));
    }

    @ExceptionHandler
    public ResponseEntity handleNotFoundException(FeedNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

}