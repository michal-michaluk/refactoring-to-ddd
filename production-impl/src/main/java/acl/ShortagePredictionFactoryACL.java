package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import shortage.prediction.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ShortagePredictionFactoryACL implements ShortagePredictionFactory {
    private LocalDate today;
    private int daysAhead;
    private CurrentStock stock;
    private List<ProductionEntity> productions;
    private List<DemandEntity> demands;
    private ShortagesProductionMediator productionsMediator = new ShortagesProductionMediator();
    private ShortagesDemandsMediator demandsMediator = new ShortagesDemandsMediator();

    public ShortagePredictionFactoryACL(LocalDate today, int daysAhead, CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        this.today = today;
        this.daysAhead = daysAhead;
        this.stock = stock;
        this.productions = productions;
        this.demands = demands;
    }

    @Override
    public ShortageForecast create() {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = productionsMediator.createProductionOutputs(productions);
        Demands demandsPerDay = demandsMediator.createDemands(demands);

        return new ShortageForecast(createWarehouseStock(), dates, outputs, demandsPerDay);
    }

    private WarehouseStock createWarehouseStock() {
        return new WarehouseStock(stock.getLevel());
    }
}
