package metro.exceptions;

public class BadTrackException extends RuntimeException {
    public BadTrackException(String start, String end) {
        super(String.format("нет пути из станции %s до %s", start, end));
    }
}
