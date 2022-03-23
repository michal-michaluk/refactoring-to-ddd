package shortage.prediction;

import java.time.LocalDate;

public class ShortageForecast {
    private final DateRange dates;
    private final WarehouseStock stock;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortageForecast(DateRange dates, WarehouseStock stock, ProductionOutputs outputs, Demands demandsPerDay) {
        this.dates = dates;
        this.stock = stock;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public Shortage predictShortages() {
        long level = stock.level();

        Shortage.Builder shortages = Shortage.builder(outputs.getProductRefNo());
        for (LocalDate day : dates.list()) {
            if (demandsPerDay.notContains(day)) {
                level += outputs.getLevel(day);
                continue;
            }
            long produced = outputs.getLevel(day);

            Demands.DailyDemand demand = demandsPerDay.get(day);

            long levelOnDelivery = demand.levelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                shortages.add(day, levelOnDelivery);
            }

            long endOfDayLevel = demand.endOfDayLevel(level, produced);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages.build();
    }

    public boolean hasAnyLocked() {
        return stock.locked() > 0;
    }
}
