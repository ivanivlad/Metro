package metro;

public class BadTrackException extends Exception {
    public BadTrackException(String start, String end) {
        super(String.format("нет пути из станции %s до %s", start, end));
    }
}
