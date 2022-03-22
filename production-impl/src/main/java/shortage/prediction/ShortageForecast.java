package shortage.prediction;

import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortageForecast {
    private final CurrentStock stock;
    private final List<LocalDate> dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortageForecast(CurrentStock stock, List<LocalDate> dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public Shortage predictShortages() {
        long level = stock.getLevel();

        Shortage gap = new Shortage(outputs.getProductRefNo());
        for (LocalDate day : dates) {
            if (demandsPerDay.notContains(day)) {
                level += outputs.getLevel(day);
                continue;
            }
            long produced = outputs.getLevel(day);

            Demands.DailyDemand demand = demandsPerDay.get(day);

            long levelOnDelivery = demand.levelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                gap.add(day, levelOnDelivery);
            }

            long endOfDayLevel = demand.endOfDayLevel(level, produced);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }
}
