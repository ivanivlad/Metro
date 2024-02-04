package metro;

import metro.exceptions.BadTrackException;
import metro.exceptions.StationNotExistsException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Metro {
    public static final int MAX_PASS_COUNT = 10000;
    private final String cityName;
    private final Set<MetroLine> lines = new HashSet<>();
    private final Map<String, LocalDate> travelPass = new HashMap<>();
    private int travelPassCount = 0;

    public Metro(String cityName) {
        this.cityName = cityName;
    }

    public void addLine(LineColor colorLine) {
        checkLineExists(colorLine);
        MetroLine metroLine = new MetroLine(this, colorLine);
        lines.add(metroLine);
    }

    public void addFirstStation(LineColor colorLine, String stationName) {
        addFirstStation(colorLine, stationName, List.of());
    }

    public void addFirstStation(LineColor colorLine, String stationName,
                                Collection<Station> changeStation) {
        MetroLine currentLine = getLineByColor(colorLine);
        checkStationExists(stationName);
        currentLine.checkFirstStationExists();
        currentLine.addFirstStation(stationName, changeStation);
    }

    public void addLastStation(LineColor colorLine, String stationName, Duration timeDuration) {
        addLastStation(colorLine, stationName, timeDuration, List.of());
    }

    public void addLastStation(LineColor colorLine, String stationName, Duration timeDuration,
                               Collection<Station> changeStation) {
        MetroLine currentLine = getLineByColor(colorLine);
        Station previousStation = currentLine.getLastStation();
        checkStationExists(stationName);
        checkHasNotNextStation(previousStation);
        checkTimeDuration(stationName, timeDuration);
        currentLine.addLastStation(stationName, timeDuration, previousStation, changeStation);
    }

    private void checkHasNotNextStation(Station station) {
        if (station.getNextStation() != null) {
            throw new RuntimeException(
                    String.format("Станция %s уже имеет следующую", station));
        }
    }

    private void checkTimeDuration(String stationName, Duration timeDuration) {
        if (timeDuration.isZero() || timeDuration.isNegative()) {
            throw new RuntimeException(
                    String.format("Время до предыдущей станции должно быть больше 0 для %s",
                            stationName));
        }
    }

    private void checkLineExists(LineColor color) {
        boolean lineExists = lines.stream()
                .anyMatch((e) -> e.getColor() == color);
        if (lineExists) {
            throw new RuntimeException(
                    String.format("Линия с цветом %s уже существует", color));
        }
    }

    private void checkStationExists(String stationName) {
        boolean isStationExists = lines.stream()
                .anyMatch(e -> e.getStation(stationName).isPresent());
        if (isStationExists) {
            throw new StationNotExistsException(stationName);
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

    public Station getStation(LineColor lineColor, String stationName) {
        MetroLine lineByColor = getLineByColor(lineColor);
        return lineByColor.getStation(stationName).orElseThrow(() -> new StationNotExistsException(stationName));
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
                                   Station endStation) {
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
        }
        throw new BadTrackException(
                    startStation.getName(),
                    endStation.getName());
    }

    public int countOfStationBetween(Station startStation,
                                     Station endStation) {
        MetroLine startLine = startStation.getLine();
        MetroLine endLine = endStation.getLine();
        if (startLine == endLine) {
            return countOfStageOnLine(startStation, endStation);
        }
        Station changeStationOnLine = startLine.findChangeStation(endLine);
        int firstPart = countOfStageOnLine(startStation, changeStationOnLine);
        Station stationOnChaneLine = endLine.findChangeStation(startLine);
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

    public void addMonthTravelPass(LocalDate dateSale) {
        String newSerialNumber = generateTravelPassNumber();
        travelPass.put(newSerialNumber, dateSale.plusMonths(1));
    }

    private String generateTravelPassNumber() {
        travelPassCount++;
        if (travelPassCount == MAX_PASS_COUNT) {
            throw new RuntimeException("Превышено число возможных номеров");
        }
        return String. format("%s%04d", "a", travelPassCount);
    }

    public void renewMonthTravelPass(String passNumber, LocalDate saleDate) {
        checkPassExists(passNumber);
        LocalDate newDateSale = saleDate.plusMonths(1);
        travelPass.put(passNumber, newDateSale);
    }

    public boolean isPassValidity(String passNumber) {
        checkPassExists(passNumber);
        LocalDate dateOfExpire = travelPass.get(passNumber);
        return dateOfExpire.compareTo(LocalDate.now()) > 0;
    }

    private void checkPassExists(String passNumber) {
        if (!travelPass.containsKey(passNumber)) {
            throw new RuntimeException(String.format("Абонемент %s не найден", passNumber));
        }
    }

    public void printTotalIncome() {
        TreeMap<LocalDate, BigDecimal> totalIncome = new TreeMap<>();

        lines.stream().flatMap(metroLine -> metroLine.getStations().stream())
                    .forEach(station -> station.getTicketOffice()
                            .getAllSales()
                            .forEach((key, value) -> totalIncome.compute(key, (k, v) ->
                                        v == null ? value : v.add(value))));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        totalIncome.forEach((key, value) ->
                System.out.printf("%s - %s\n", key.format(pattern), value));
    }
}
