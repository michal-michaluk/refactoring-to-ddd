package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import tools.ExampleProductions.ProductionBuilder;
import tools.ExampleProductions.ProductionPlanBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static tools.ExampleProductions.ProductionPlanBuilder.forProductionLine;
import static tools.ShortagesAssert.assertThat;

public class ShortageFinderTest {

    private LocalDate date = LocalDate.now();

    private CurrentStock stock;
    private List<ProductionEntity> productions;
    private List<DemandEntity> demands;
    private List<ShortageEntity> foundShortages;

    @Test
    public void findShortages() {
        given(
                stock(1000),
                productPlan(forProductionLine(0)
                        .plannedOutputs(date, 7, 6300, 6300, 6300, 6300, 6300, 6300, 6300)
                        .plannedOutputs(date, 14, 6300, 6300, 6300, 6300, 6300, 6300, 6300)
                ),
                demands(date.plusDays(1), 17000, 17000)
        );

        whenShortagesArePredicted();

        thenPredicted()
                .shortagesAtDates(date.plusDays(1), date.plusDays(2))
                .missingPartsAt(date.plusDays(1), 3400)
                .missingPartsAt(date.plusDays(2), 4400);
    }

    private void given(CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        this.stock = stock;
        this.productions = productions;
        this.demands = demands;
    }

    private void whenShortagesArePredicted() {
        foundShortages = ShortageFinder.findShortages(
                date.plusDays(1), 7,
                stock,
                productions,
                demands
        );
    }

    private ShortagesAssert thenPredicted() {
        return assertThat(foundShortages);
    }

    private List<ProductionEntity> productPlan(ProductionPlanBuilder productions) {
        return productions.build()
                .map(ProductionBuilder::build)
                .collect(Collectors.toList());
    }

    private CurrentStock stock(int stockLevel) {
        return new CurrentStock(stockLevel, 200);
    }

    private List<DemandEntity> demands(LocalDate date, int... demand) {
        return ExampleDemands.demandSequence(date, demand)
                .map(ExampleDemands.DemandBuilder::build)
                .collect(Collectors.toList());
    }
}
