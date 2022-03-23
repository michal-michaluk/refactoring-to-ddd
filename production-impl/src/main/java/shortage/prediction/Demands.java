package shortage.prediction;

import java.time.LocalDate;
import java.util.Map;

public class Demands {
    private final Map<LocalDate, DailyDemand> demands;

    public Demands(Map<LocalDate, DailyDemand> demands) {
        this.demands = demands;
    }

    public boolean notContains(LocalDate day) {
        return !demands.containsKey(day);
    }

    public DailyDemand get(LocalDate day) {
        if (!demands.containsKey(day)) {
            return null;
        }
        return demands.get(day);
    }

    public static class DailyDemand {
        private final long demand;
        private final LevelOnDeliveryCalculation calculation;

        public DailyDemand(long demand, LevelOnDeliveryCalculation calculation) {
            this.demand = demand;
            this.calculation = calculation;
        }

        public long levelOnDelivery(long level, long produced) {
            return calculation.levelOnDelivery(demand, level, produced);
        }

        public long endOfDayLevel(long level, long produced) {
            return level + produced - demand;
        }
    }
}
