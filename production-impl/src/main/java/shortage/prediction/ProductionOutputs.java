package shortage.prediction;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.*;

public class ProductionOutputs {
    private final Map<LocalDate, List<ProductionEntity>> outputs;
    private final String productRefNo;

    public ProductionOutputs(List<ProductionEntity> productions) {
        String productRefNo = null;
        Map<LocalDate, List<ProductionEntity>> outputs = new HashMap<>();
        for (ProductionEntity production : productions) {
            if (!outputs.containsKey(production.getStart().toLocalDate())) {
                outputs.put(production.getStart().toLocalDate(), new ArrayList<>());
            }
            outputs.get(production.getStart().toLocalDate()).add(production);
            productRefNo = production.getForm().getRefNo();
        }
        this.outputs = Collections.unmodifiableMap(outputs);
        this.productRefNo = productRefNo;
    }

    public long getLevel(LocalDate day) {
        if (!outputs.containsKey(day)) {
            return 0;
        }
        long level = 0;
        for (ProductionEntity production : outputs.get(day)) {
            level += production.getOutput();
        }
        return level;
    }

    public String getProductRefNo() {
        return productRefNo;
    }
}
