package shortage.prediction;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demands {
    private final Map<LocalDate, DemandEntity> demands;

    public Demands(List<DemandEntity> demands) {
        Map<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
        this.demands = Collections.unmodifiableMap(demandsPerDay);
    }

    public boolean notContains(LocalDate day) {
        return !demands.containsKey(day);
    }

    public DailyDemand get(LocalDate day) {
        if (!demands.containsKey(day)) {
            return null;
        }
        return new DailyDemand(demands.get(day));
    }

    public static class DailyDemand {
        private final DemandEntity demand;

        public DailyDemand(DemandEntity demand) {
            this.demand = demand;
        }

        public long levelOnDelivery(long level, long produced) {
            if (getDeliverySchema() == DeliverySchema.atDayStart) {
                return level - getLevel();
            } else if (getDeliverySchema() == DeliverySchema.tillEndOfDay) {
                return level - getLevel() + produced;
            } else if (getDeliverySchema() == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new UnsupportedOperationException();
            } else {
                // TODO implement other variants
                throw new UnsupportedOperationException();
            }
        }

        public long endOfDayLevel(long level, long produced) {
            return level + produced - getLevel();
        }

        private DeliverySchema getDeliverySchema() {
            return Util.getDeliverySchema(demand);
        }

        public long getLevel() {
            return Util.getLevel(demand);
        }
    }
}
