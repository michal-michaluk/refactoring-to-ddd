package acl;

import external.CurrentStock;
import external.StockService;
import shortage.prediction.WarehouseStock;

public class ShortagesWarehouseMediator {
    private StockService stockService;

    public WarehouseStock createWarehouseStock(String refNo) {
        CurrentStock stock = stockService.getCurrentStock(refNo);
        return new WarehouseStock(stock.getLevel(), stock.getLocked());
    }
}
