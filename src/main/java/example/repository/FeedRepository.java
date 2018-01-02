package example.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import example.model.Feed;

@Repository
public interface FeedRepository extends ReactiveMongoRepository<Feed, String> {

}