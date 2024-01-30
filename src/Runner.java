import metro.BadTrackException;
import metro.LineColor;
import metro.Metro;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class Runner {
    public static void main(String[] args) throws BadTrackException {
        Metro permMetro = init();
        System.out.println(permMetro);

        permMetro.getStation(LineColor.RED, "Спортивная").buyOneWayTicket(
                LocalDate.of(2023, 1, 1),
                "Спортивная",
                "Медведковская");
        permMetro.getStation(LineColor.RED, "Молодежная").buyOneWayTicket(
                LocalDate.of(2023, 1, 1),
                "Спортивная",
                "Молодежная");

        permMetro.getStation(LineColor.RED, "Молодежная")
                .buyMonthTravelPass(
                        LocalDate.of(2023, 1, 2));
        permMetro.getStation(LineColor.RED, "Медведковская")
                .buyMonthTravelPass(LocalDate.of(2023, 1, 3));

        System.out.println(permMetro.checkPassValidity("a0001"));

        permMetro.getStation(LineColor.RED, "Спортивная").renewTravelPass(
                "a0001",
                LocalDate.of(2023, 1, 3));

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
                Duration.parse("PT3M"),
                Map.of(LineColor.BLUE, "Тяжмаш"));
        metro.addLastStation(LineColor.RED, "Пермь 2",
                Duration.parse("PT2M10S"));
        metro.addLastStation(LineColor.RED, "Дворец Культуры",
                Duration.parse("PT4M26S"));

        metro.addLine(LineColor.BLUE);
        metro.addFirstStation(LineColor.BLUE, "Пацанская");
        metro.addLastStation(LineColor.BLUE, "Улица Кирова",
                Duration.parse("PT1M30S"));
        metro.addLastStation(LineColor.BLUE, "Тяжмаш",
                Duration.parse("PT1M47S"),
                Map.of(LineColor.RED, "Пермь 1"));
        metro.addLastStation(LineColor.BLUE, "Нижнекамская",
                Duration.parse("PT3M19S"));
        metro.addLastStation(LineColor.BLUE, "Соборная",
                Duration.parse("PT1M48S"));

        return metro;
    }
}
