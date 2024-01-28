package metro;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Metro {
    private final String cityName;
    private final List<MetroLine> lines;
    private final Map<String, LocalDate> travelPass = new HashMap<>();
    private int travelPassCount = 0;

    public Metro(String cityName) {
        this.cityName = cityName;
        this.lines = new ArrayList<>();
    }

    public void addLine(LineColor colorLine) {
        checkLineExists(colorLine);
        MetroLine metroLine = new MetroLine(this, colorLine);
        lines.add(metroLine);
    }

    public void addFirstStation(LineColor colorLine, String stationName) {
        addFirstStation(colorLine, stationName, null);
    }

    public void addFirstStation(LineColor colorLine, String stationName,
                                Map<LineColor, String> changeStation) {
        MetroLine currentLine = getLineByColor(colorLine);
        checkStationExists(stationName);
        checkFirstStationExists(stationName, currentLine);
        currentLine.addFirstStation(stationName, changeStation);
    }

    public void addLastStation(LineColor colorLine, String stationName, Duration timeDuration) {
        addLastStation(colorLine, stationName, timeDuration, null);
    }

    public void addLastStation(LineColor colorLine, String stationName, Duration timeDuration,
                               Map<LineColor, String> changeStation) {
        MetroLine currentLine = getLineByColor(colorLine);
        checkStationExists(stationName);
        Station previousStation = getLastStation(currentLine);
        checkHasNotNextStation(previousStation);
        checkTimeDuration(stationName, timeDuration);
        currentLine.addLastStation(stationName, timeDuration, previousStation, changeStation);
    }

    private static void checkHasNotNextStation(Station station) {
        if (station.getNextStation() != null) {
            throw new RuntimeException(
                    String.format("Станция %s уже имеет следующую", station));
        }
    }

    private void checkTimeDuration(String stationName, Duration timeDuration) {
        if (timeDuration.isZero() || timeDuration.isNegative()) {
            throw new RuntimeException(
                    String.format("Время до предыдущей станции должно быть больше 0 для %s", stationName));
        }
    }

    private void checkLineExists(LineColor color) {
        if (lines.stream()
                .anyMatch((e) -> e.getColor() == color)) {
            throw new RuntimeException(
                    String.format("Линия с цветом %s уже существует", color));
        }
    }

    private void checkStationExists(String stationName) {
        try {
            getStation(stationName);
        } catch (NoSuchElementException e) {
            return;
        }
        throw new RuntimeException(
                String.format("Станция с именем %s уже существует", stationName));
    }

    private void checkFirstStationExists(String stationName, MetroLine currentLine) {
        if (!currentLine.isEmpty()) {
            throw new RuntimeException(
                    String.format("У линии %s уже есть первая станция", stationName));
        }
    }

    private MetroLine getLineByColor(LineColor color) {
        return lines.stream()
                .filter((e) -> e.getColor() == color)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                String.format("Линии с цветом %s не существует", color)));
    }

    private Station getStation(String stationName) throws NoSuchElementException {
        return lines.stream().flatMap(metroLine -> metroLine.getStations().stream())
                .filter((e) -> e.getName().equalsIgnoreCase(stationName))
                .findFirst().orElseThrow(() -> new NoSuchElementException(
                        String.format("Станция с именем %s не существует", stationName)));
    }

    private Station getLastStation(MetroLine line) {
        List<Station> stations = line.getStations();
        if (stations.isEmpty()) {
            throw new RuntimeException(
                    String.format("На линии '%s' отсутствут станции", line));
        }
        return stations.get(stations.size() - 1);
    }

    private Station findChangeStation(MetroLine startLine, MetroLine endLine) {
        return startLine.getStations().stream()
                .filter((e) -> e.hasChangeByLine(endLine))
                .findFirst().orElseThrow(() ->
                        new RuntimeException(
                                String.format("Между станциями %s и %s нет пересадок",
                                        startLine,
                                        endLine)));
    }

    private int countOfStageDirect(Station startStation, Station endStation) {
        return countOfStage(startStation, endStation, true);
    }

    private int countOfStageReverse(Station startStation, Station endStation) {
        return countOfStage(startStation, endStation, false);
    }

    private int countOfStage(Station startStation, Station endStation, boolean direct) {
        if (startStation == endStation) {
            return 0;
        }
        int count = 0;
        Station nextStation = startStation.getNearestStation(direct);
        while (true) {
            count++;
            if (nextStation == endStation) {
                break;
            }
            nextStation = nextStation.getNearestStation(direct);
            if (nextStation == null) {
                return  -1;
            }
        }
        return count;
    }

    private int countOfStageOnLine(Station startStation,
                                   Station endStation) throws BadTrackException {
        int count = countOfStageDirect(startStation, endStation);

        if (count >= 0) {
            return count;
        } else if (count == -1) {
            count = countOfStageReverse(endStation, startStation);
        } else {
            throw new RuntimeException("Неожиданный результат подсчета станций");
        }

        if (count >= 0) {
            return count;
        } else {
            throw new BadTrackException(
                    startStation.getName(),
                    endStation.getName());
        }
    }

    public int countOfStationBetween(Station startStation,
                                     Station endStation) throws BadTrackException {
        MetroLine startLine = startStation.getLine();
        MetroLine endLine = endStation.getLine();
        if (startLine == endLine) {
            return countOfStageOnLine(startStation, endStation);
        }
        Station changeStationOnLine = findChangeStation(startLine, endLine);
        int firstPart = countOfStageOnLine(startStation, changeStationOnLine);
        String nameChangeStation = changeStationOnLine.getNameChangeStation(endLine);
        Station stationOnChaneLine = getStation(nameChangeStation);
        int secPart = countOfStageOnLine(stationOnChaneLine, endStation);
        return firstPart + secPart;
    }

    @Override
    public String toString() {
        return "Metro{"
                + "city='" + cityName + '\''
                + ", lines=" + lines
                + '}';
    }

    public void buyTicketOnStation(LocalDate dateSale, String sellerName, String startStationName,
                                   String endStationName) {
        Station startStation = getStation(startStationName);
        Station endStation = getStation(endStationName);
        Station stationSeller = getStation(sellerName);
        if (startStation == endStation) {
            throw new RuntimeException("станция назначения равна станции отправления");
        }
        stationSeller.buyOneWayTicket(dateSale, startStation, endStation);
    }

    public void buyPassOnStation(LocalDate dateSale, String sellerName) {
        Station stationSeller = getStation(sellerName);
        if (stationSeller == null) {
            throw new RuntimeException("нет станции: " + sellerName);
        }
        String newSerialNumber = generateTravelPassNumber();
        stationSeller.buyMonthTravelPass(dateSale);
        travelPass.put(newSerialNumber, dateSale);
    }

    private String generateTravelPassNumber() {
        travelPassCount++;
        if (travelPassCount == 1000) {
            throw new RuntimeException("Превышено число возможных номеров");
        }
        return String. format("%s%04d", "a", travelPassCount);
    }

    public void renewPassOnStation(LocalDate dateSale, String passNumber, String sellerName) {
        if (!travelPass.containsKey(passNumber)) {
            throw new RuntimeException(String.format("Абонемент %s не найден", passNumber));
        }
        Station stationSeller = getStation(sellerName);
        LocalDate newDateSale = stationSeller.renewTravelPass(dateSale);
        travelPass.put(passNumber, newDateSale);
    }

    public boolean checkPassValidity(String passNumber) {
        if (passNumber == null) {
            throw new RuntimeException();
        }
        if (!travelPass.containsKey(passNumber)) {
            throw new RuntimeException(String.format("Абонемент %s не найден", passNumber));
        }
        LocalDate dateOfExpire = travelPass.get(passNumber);
        return dateOfExpire.compareTo(LocalDate.now()) > 0;
    }

    public void printTotalIncome() {
        TreeMap<LocalDate, BigDecimal> totalIncome = new TreeMap<>();

        lines.stream().flatMap(metroLine -> metroLine.getStations().stream())
                    .forEach(station -> station.getTicketOffice().forEach((key, value) ->
                                totalIncome.compute(key, (k, v) ->
                                        v == null ? value : v.add(value))));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        totalIncome.forEach((key, value) ->
                System.out.printf("%s - %s\n", key.format(pattern), value));
    }

}
