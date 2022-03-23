package demand.forecasting;

import entities.DemandEntity;
import enums.DeliverySchema;

import java.time.LocalDate;

public class Demand {

    private final DemandEntity demand;

    public Demand(DemandEntity demand) {
        this.demand = demand;
    }

    public LocalDate getDate() {
        return demand.getDay();
    }

    public long getLevel() {
        if (demand.getAdjustment().isEmpty()) {
            return demand.getOriginal().getLevel();
        } else {
            return demand.getAdjustment().get(demand.getAdjustment().size() - 1).getLevel();
        }
    }

    public DeliverySchema getDeliverySchema() {
        DeliverySchema deliverySchema;
        if (demand.getAdjustment().isEmpty()) {
            deliverySchema = demand.getOriginal().getDeliverySchema();
        } else {
            deliverySchema = demand.getAdjustment().get(demand.getAdjustment().size() - 1).getDeliverySchema();
        }
        if (deliverySchema == null) {
            return DefaultDeliverySchemaPolicy.defaultFor(demand.getProductRefNo());
        }
        return deliverySchema;
    }
}
