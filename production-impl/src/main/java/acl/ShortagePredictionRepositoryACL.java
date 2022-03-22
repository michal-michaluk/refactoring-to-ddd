package acl;

import shortage.prediction.*;

class ShortagePredictionRepositoryACL implements ShortagePredictionRepository {

    private ShortagesProductionMediator productions;
    private ShortagesDemandsMediator demands;
    private ShortagesWarehouseMediator warehouse;


    @Override
    public ShortageForecast get(String refNo, DateRange range) {
        DateRange dates = range;
        ProductionOutputs outputs = productions.createProductionOutputs(refNo, range.start());
        Demands demands = this.demands.createDemands(refNo, range.start());
        WarehouseStock stock = warehouse.createWarehouseStock(refNo);
        return new ShortageForecast(dates, stock, outputs, demands);
    }

}
