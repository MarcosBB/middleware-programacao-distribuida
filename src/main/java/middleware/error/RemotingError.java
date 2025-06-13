package middleware.error;

public class RemotingError extends Exception {
    public RemotingError(String message) {
        super(message);
    }
}