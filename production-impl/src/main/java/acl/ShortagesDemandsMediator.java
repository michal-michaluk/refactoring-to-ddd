package acl;

import entities.DemandEntity;
import shortage.prediction.Demands;
import tools.Util;

import java.util.List;
import java.util.stream.Collectors;

class ShortagesDemandsMediator {
    Demands createDemands(List<DemandEntity> demands) {
        return new Demands(demands.stream()
                .collect(Collectors.toMap(
                                DemandEntity::getDay,
                                demand -> new Demands.DailyDemand(
                                        Util.getLevel(demand),
                                        Util.getDeliverySchema(demand))
                        )
                ));
    }
}
