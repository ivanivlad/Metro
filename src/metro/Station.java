package metro;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Station {
    private final String name;
    private final MetroLine line;
    private Station nextStation;
    private Station previousStation;
    private Duration timeDuration;

    private final HashMap<LineColor, String> changeStationNames = new HashMap<>();
    private final TicketOffice ticketOffice = new TicketOffice();

    public Station(String name, MetroLine line) {
        this.name = name;
        this.line = line;
    }

    public Station(String name, MetroLine line, Map<LineColor, String> changeStationNames) {
        this(name, line);
        this.changeStationNames.putAll(changeStationNames);
    }

    public Station(String name, MetroLine line,
                   Station previousStation, Map<LineColor, String> changeStationNames) {
        this(name, line, changeStationNames);
        this.previousStation = previousStation;
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
                + (changeStationNames.isEmpty() ? "null" : changeStationNames.keySet().toString())
                + '}';
    }

    public boolean hasChangeByLine(MetroLine line) {
        return changeStationNames.containsKey(line.getColor());
    }

    public void buyOneWayTicket(LocalDate saleDate, String startStationName, String endStationName) {
        Station startStation = line.getStation(startStationName).orElseThrow();
        Station endStation = line.getStation(endStationName).orElseThrow();
        if (startStation == endStation) {
            throw new RuntimeException("станция назначения равна станции отправления");
        }
        int countOfStation;
        Metro metro = line.getMetro();
        try {
            countOfStation = metro.countOfStationBetween(startStation, endStation);
        } catch (BadTrackException e) {
            throw new RuntimeException(e);
        }
        ticketOffice.saleOneWayTicket(saleDate, countOfStation);
    }

    public void buyMonthTravelPass(LocalDate saleDate) {
        Metro metro = line.getMetro();
        metro.addMonthTravelPass(saleDate);
        ticketOffice.saleMonthTravelPass(saleDate);
    }

    public void renewTravelPass(String passNumber, LocalDate saleDate) {
        Metro metro = line.getMetro();
        metro.renewMonthTravelPass(passNumber, saleDate);
        ticketOffice.renewTravelPass(saleDate);
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }
}
