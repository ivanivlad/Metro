package metro;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetroLine {
    private final Metro metro;
    private final LineColor color;
    private final LinkedHashMap<String, Station> stations = new LinkedHashMap<>();

    public MetroLine(Metro metro, LineColor colorLine) {
        this.metro = metro;
        this.color = colorLine;
    }

    public LineColor getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations.values().stream().toList();
    }

    public void addFirstStation(String stationName, Map<LineColor, String> changeStation) {
        Station station = new Station(stationName, this, changeStation);
        stations.put(stationName, station);
    }

    public void addLastStation(String stationName, Duration timeDuration,
                               Station previousStation,
                               Map<LineColor, String> changeStation) {
        Station station = new Station(stationName, this, previousStation, changeStation);
        previousStation.setNextStation(station, timeDuration);
        stations.put(stationName, station);
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
        return Optional.ofNullable(stations.get(stationName));
    }

    public Station getLastStation() {
        if (stations.isEmpty()) {
            throw new RuntimeException(
                    String.format("На линии '%s' отсутствут станции", this));
        }
        return stations.values().stream()
                .filter(e -> e.getNextStation() == null)
                .findFirst().orElseThrow();
    }

    public void checkFirstStationExists() {
        if (!stations.isEmpty()) {
            throw new RuntimeException(
                    String.format("У линии %s уже есть первая станция", this));
        }
    }

    public Station findChangeStation(MetroLine endLine) {
        return stations.values().stream()
                .filter((e) -> e.hasChangeByLine(endLine))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                String.format("Между станциями %s и %s нет пересадок",
                                        this,
                                        endLine)));
    }
}
