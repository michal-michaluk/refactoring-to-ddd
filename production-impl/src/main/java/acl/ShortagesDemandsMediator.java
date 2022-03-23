package acl;

import demand.forecasting.Demand;
import demand.forecasting.DemandService;
import enums.DeliverySchema;
import shortage.prediction.Demands;
import shortage.prediction.LevelOnDeliveryCalculation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ShortagesDemandsMediator {
    private DemandService service;

    Demands createDemands(String refNo, LocalDate today) {
        List<Demand> demands = service.findFrom(refNo, today.atStartOfDay());

        return new Demands(demands.stream()
                .collect(Collectors.toMap(
                        Demand::getDate,
                        demand -> new Demands.DailyDemand(
                                demand.getLevel(),
                                pick(demand.getDeliverySchema())
                        )
                )));
    }

    static LevelOnDeliveryCalculation pick(DeliverySchema schema) {
        return Map.of(
                DeliverySchema.atDayStart, LevelOnDeliveryCalculation.atDayStart,
                DeliverySchema.tillEndOfDay, LevelOnDeliveryCalculation.tillEndOfDay
        ).getOrDefault(schema, LevelOnDeliveryCalculation.error);
    }
}
