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

        public DeliverySchema getDeliverySchema() {
            return Util.getDeliverySchema(demand);
        }

        public long getLevel() {
            return Util.getLevel(demand);
        }
    }
}
