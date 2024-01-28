package metro;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetroLine {
    private final Metro metro;
    private final LineColor color;
    private final List<Station> stations = new ArrayList<>();

    public MetroLine(Metro metro, LineColor colorLine) {
        this.metro = metro;
        this.color = colorLine;
    }

    public LineColor getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }

    public boolean isEmpty() {
        return stations.isEmpty();
    }

    public void addFirstStation(String stationName, Map<LineColor, String> changeStation) {
        Station station = new Station(stationName, this, changeStation);
        stations.add(station);
    }

    public void addLastStation(String stationName, Duration timeDuration,
                               Station previousStation,
                               Map<LineColor, String> changeStation) {
        Station station = new Station(stationName, this, timeDuration,
                previousStation, changeStation);
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
}
