package metro.exceptions;

public class StationNotExistsException extends RuntimeException {
    public StationNotExistsException(String message) {
        super(message);
    }
}
