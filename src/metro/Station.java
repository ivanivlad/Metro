package metro;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Station {
    public static final BigDecimal ONE_STAGE_PRICE = new BigDecimal(5);
    public static final BigDecimal BASE_TICKET_PRICE = new BigDecimal(20);
    public static final BigDecimal PASS_PRICE = new BigDecimal(3000);
    private final String name;
    private final MetroLine line;
    private Station nextStation;
    private Station previousStation;
    private Duration timeDuration;
    private Map<LineColor, String> changeLines;
    private final Map<LocalDate, BigDecimal> ticketOffice = new HashMap<>();

    public Station(String name, MetroLine line) {
        this.name = name;
        this.line = line;
    }

    public Station(String name, MetroLine line, Map<LineColor, String> changeStation) {
        this(name, line);
        if (changeStation != null) {
            this.changeLines = new HashMap<>();
            this.changeLines.putAll(changeStation);
        }
    }

    public Station(String name, MetroLine line, Duration prevTimeDuration,
                   Station previousStation, Map<LineColor, String> changeStation) {
        this(name, line, changeStation);
        this.previousStation = previousStation;
        previousStation.setNextStation(this, prevTimeDuration);
    }

    public void setNextStation(Station nextStation, Duration timeDuration) {
        this.nextStation = nextStation;
        if (!timeDuration.isZero()) {
            this.timeDuration = timeDuration;
        }
    }

    public String getName() {
        return name;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public Station getNearestStation(boolean direct) {
        return direct ? getNextStation() : getPreviousStation();
    }

    public MetroLine getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Station{"
                + "name='" + name + '\''
                + ", changeLines="
                + ((changeLines != null) ? changeLines.values().toString() : "null")
                + '}';
    }

    public boolean hasChangeByLine(MetroLine line) {
        return changeLines != null && changeLines.containsKey(line.getColor());
    }

    public String getNameChangeStation(MetroLine line) {
        return changeLines.get(line.getColor());
    }

    public void buyOneWayTicket(LocalDate saleDate, Station startStation, Station endStation) {
        int countOfStation;
        Metro metro = line.getMetro();
        try {
            countOfStation = metro.countOfStationBetween(startStation, endStation);
        } catch (BadTrackException e) {
            throw new RuntimeException(e);
        }
        BigDecimal price = new BigDecimal(countOfStation)
                        .multiply(ONE_STAGE_PRICE)
                        .add(BASE_TICKET_PRICE);
        sale(saleDate, price);
    }

    public void buyMonthTravelPass(LocalDate saleDate) {
        sale(saleDate, PASS_PRICE);
    }


    public void sale(LocalDate saleDate, BigDecimal price) {
        BigDecimal totalAmount = ticketOffice.getOrDefault(saleDate, BigDecimal.ZERO);
        ticketOffice.put(saleDate, totalAmount.add(price));
    }

    public LocalDate renewTravelPass(LocalDate saleDate) {
        sale(saleDate, PASS_PRICE);
        return saleDate.plusMonths(1);
    }

    public Map<LocalDate, BigDecimal> getTicketOffice() {
        return ticketOffice;
    }
}
