package metro;

import metro.exceptions.BadTrackException;
import metro.exceptions.StationNotExistsException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class Station {
    private final String name;
    private final MetroLine line;
    private final Metro metro;
    private Station nextStation;
    private Station previousStation;
    private Duration timeDuration;
    private final Set<Station> changeStation = new HashSet<>();
    private final TicketOffice ticketOffice = new TicketOffice();

    public Station(String name, MetroLine line) {
        this.name = name;
        this.line = line;
        this.metro = line.getMetro();
    }

    public Station(String name, MetroLine line, Collection<Station> changeStation) {
        this(name, line);
        this.changeStation.addAll(changeStation);
    }

    public Station(String name, MetroLine line,
                   Station previousStation, Collection<Station> changeStation) {
        this(name, line, changeStation);
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
        String changeLines = changeStation.stream()
                    .map(e -> e.getLine().getColor().toString())
                    .reduce((f, s) -> f + ", " + s)
                    .orElse("null");
        return "Station{"
                + "name='" + name + '\''
                + ", changeLines=" + changeLines
                + '}';
    }

    public void buyOneWayTicket(LocalDate saleDate, String startStationName, String endStationName) {
        Station startStation = line.getStation(startStationName)
                .orElseThrow(() -> new StationNotExistsException(startStationName));
        Station endStation = line.getStation(endStationName)
                .orElseThrow(() -> new StationNotExistsException(endStationName));
        if (startStation == endStation) {
            throw new RuntimeException("станция назначения равна станции отправления");
        }
        int countOfStation;
        try {
            countOfStation = metro.countOfStationBetween(startStation, endStation);
        } catch (BadTrackException e) {
            throw new RuntimeException(e);
        }
        ticketOffice.saleOneWayTicket(saleDate, countOfStation);
    }

    public void buyMonthTravelPass(LocalDate saleDate) {
        metro.addMonthTravelPass(saleDate);
        ticketOffice.saleMonthTravelPass(saleDate);
    }

    public void renewTravelPass(String passNumber, LocalDate saleDate) {
        metro.renewMonthTravelPass(passNumber, saleDate);
        ticketOffice.renewTravelPass(saleDate);
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }

    public void addChangeStation(Station station) {
        changeStation.add(station);
    }

    public boolean hasStationToChange(Collection<Station> stations) {
        return changeStation.stream().anyMatch(stations::contains);
    }

    public Set<Station> getChangeStations() {
        return changeStation;
    }
}
