package acl;

import entities.ProductionEntity;
import shortage.prediction.ProductionOutputs;

import java.util.List;
import java.util.stream.Collectors;

class ShortagesProductionMediator {
    ProductionOutputs createProductionOutputs(List<ProductionEntity> productions) {
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
