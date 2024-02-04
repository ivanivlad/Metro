import metro.exceptions.BadTrackException;
import metro.LineColor;
import metro.Metro;
import metro.Station;
import java.time.Duration;
import java.time.LocalDate;

public class Runner {
    public static void main(String[] args) throws BadTrackException {
        Metro permMetro = init();
        System.out.println(permMetro);

        Station stationSport = permMetro.getStation(LineColor.RED, "Спортивная");
        stationSport.buyOneWayTicket(
                LocalDate.of(2024, 1, 1),
                "Спортивная",
                "Медведковская");

        Station stationMolodezh = permMetro.getStation(LineColor.RED, "Молодежная");
        stationMolodezh.buyOneWayTicket(
                LocalDate.of(2024, 1, 1),
                "Спортивная",
                "Молодежная");
        stationMolodezh.buyMonthTravelPass(
                        LocalDate.of(2024,  1, 10));

        Station stationMedved = permMetro.getStation(LineColor.RED, "Медведковская");
        stationMedved.buyMonthTravelPass(LocalDate.of(2024, 1, 3));

        System.out.println(permMetro.isPassValidity("a0001"));
        System.out.println(permMetro.isPassValidity("a0002"));

        stationSport.renewTravelPass(
                "a0001",
                LocalDate.of(2024, 1, 3));

        permMetro.printTotalIncome();
    }

    public static Metro init() {
        Metro metro = new Metro("Пермь");
        metro.addLine(LineColor.RED);
        metro.addFirstStation(LineColor.RED, "Спортивная");
        metro.addLastStation(LineColor.RED, "Медведковская",
                Duration.parse("PT2M21S"));
        metro.addLastStation(LineColor.RED, "Молодежная",
                Duration.parse("PT1M58S"));
        metro.addLastStation(LineColor.RED, "Пермь 1",
                Duration.parse("PT3M"));
        metro.addLastStation(LineColor.RED, "Пермь 2",
                Duration.parse("PT2M10S"));
        metro.addLastStation(LineColor.RED, "Дворец Культуры",
                Duration.parse("PT4M26S"));

        metro.addLine(LineColor.BLUE);
        metro.addFirstStation(LineColor.BLUE, "Пацанская");
        metro.addLastStation(LineColor.BLUE, "Улица Кирова",
                Duration.parse("PT1M30S"));
        metro.addLastStation(LineColor.BLUE, "Тяжмаш",
                Duration.parse("PT1M47S"));
        metro.addLastStation(LineColor.BLUE, "Нижнекамская",
                Duration.parse("PT3M19S"));
        metro.addLastStation(LineColor.BLUE, "Соборная",
                Duration.parse("PT1M48S"));

        Station stationPerm1 = metro.getStation(LineColor.RED, "Пермь 1");
        Station stationTyazh = metro.getStation(LineColor.BLUE, "Тяжмаш");
        stationPerm1.addChangeStation(stationTyazh);
        stationTyazh.addChangeStation(stationPerm1);

        return metro;
    }
}
