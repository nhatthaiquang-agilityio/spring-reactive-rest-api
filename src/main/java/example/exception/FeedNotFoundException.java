package example.exception;

/**
 * Created by NhatThai.
 */
public class FeedNotFoundException extends RuntimeException {

    public FeedNotFoundException(String feedId) {
        super("Feed not found with id " + feedId);
    }
}