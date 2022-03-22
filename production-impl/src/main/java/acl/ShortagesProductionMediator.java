package acl;

import dao.ProductionDao;
import entities.ProductionEntity;
import shortage.prediction.ProductionOutputs;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

class ShortagesProductionMediator {
    private ProductionDao productionDao;

    ProductionOutputs createProductionOutputs(String refNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(refNo, today.atStartOfDay());

        return new ProductionOutputs(
                productions.stream()
                        .map(production -> production.getForm().getRefNo())
                        .findAny().orElse(null),
                productions.stream().collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput))
                ));
    }
}
