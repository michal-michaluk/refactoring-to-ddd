package acl;

import dao.DemandDao;
import entities.DemandEntity;
import shortage.prediction.Demands;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

class ShortagesDemandsMediator {
    private DemandDao demandDao;

    Demands createDemands(String refNo, LocalDate today) {
        List<DemandEntity> demands = demandDao.findFrom(today.atStartOfDay(), refNo);

        return new Demands(demands.stream()
                .collect(Collectors.toMap(
                                DemandEntity::getDay,
                                demand -> new Demands.DailyDemand(
                                        demand.getLevel(),
                                        demand.getDeliverySchema())
                        )
                ));
    }
}
