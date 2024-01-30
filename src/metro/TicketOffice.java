package metro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TicketOffice {
    public static final BigDecimal ONE_STAGE_PRICE = new BigDecimal(5);
    public static final BigDecimal BASE_TICKET_PRICE = new BigDecimal(20);
    public static final BigDecimal PASS_PRICE = new BigDecimal(3000);

    private final Map<LocalDate, BigDecimal> sales = new HashMap<>();

    private void sale(LocalDate saleDate, BigDecimal price) {
        BigDecimal totalAmount = sales.getOrDefault(saleDate, BigDecimal.ZERO);
        sales.put(saleDate, totalAmount.add(price));
    }

    public Map<LocalDate, BigDecimal> getAllSales() {
        return sales;
    }

    public void saleOneWayTicket(LocalDate saleDate, int countOfStation) {
        BigDecimal price = new BigDecimal(countOfStation)
                .multiply(ONE_STAGE_PRICE)
                .add(BASE_TICKET_PRICE);
        sale(saleDate, price);
    }

    public void saleMonthTravelPass(LocalDate saleDate) {
        sale(saleDate, PASS_PRICE);
    }

    public void renewTravelPass(LocalDate saleDate) {
        sale(saleDate, PASS_PRICE);
    }
}
