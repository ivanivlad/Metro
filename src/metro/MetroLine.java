package metro;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class MetroLine {
    private final Metro metro;
    private final LineColor color;
    private final Set<Station> stations = new LinkedHashSet<>();

    public MetroLine(Metro metro, LineColor colorLine) {
        this.metro = metro;
        this.color = colorLine;
    }

    public LineColor getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations.stream().toList();
    }

    public void addFirstStation(String stationName, Collection<Station> changeStation) {
        Station station = new Station(stationName, this, changeStation);
        stations.add(station);
    }

    public void addLastStation(String stationName, Duration timeDuration,
                               Station previousStation,
                               Collection<Station> changeStation) {
        Station station = new Station(stationName, this, previousStation, changeStation);
        previousStation.setNextStation(station, timeDuration);
        stations.add(station);
    }

    @Override
    public String toString() {
        return "Line{"
                + "color='" + color + '\''
                + ", stations=" + stations
                + '}';
    }

    public Metro getMetro() {
        return metro;
    }

    public Optional<Station> getStation(String stationName) {
        return stations.stream().filter(e -> e.getName().equals(stationName)).findFirst();
    }

    public Station getLastStation() {
        if (stations.isEmpty()) {
            throw new RuntimeException(
                    String.format("На линии '%s' отсутствут станции", this));
        }
        return stations.stream()
                .filter(e -> e.getNextStation() == null)
                .findFirst().orElseThrow(() ->
                        new RuntimeException(
                            String.format("На линии %s не назначена последняя станция", this)));
    }

    public void checkFirstStationExists() {
        if (!stations.isEmpty()) {
            throw new RuntimeException(
                    String.format("У линии %s уже есть первая станция", this));
        }
    }

    public Station findChangeStation(MetroLine endLine) {
        List<Station> allChanges = endLine.getStations().stream()
                .flatMap(e -> e.getChangeStations().stream())
                .collect(Collectors.toList());
        return stations.stream()
                .filter(e -> e.hasStationToChange(allChanges))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                    String.format("Между станциями %s и %s нет пересадок",
                            this,
                            endLine)));
    }
}
