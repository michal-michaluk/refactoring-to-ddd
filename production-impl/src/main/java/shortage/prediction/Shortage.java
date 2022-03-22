package shortage.prediction;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Shortage {
    private final List<ShortageEntity> gap = new LinkedList<>();
    private final String productRefNo;

    public Shortage(String productRefNo) {
        this.productRefNo = productRefNo;
    }

    public void add(LocalDate day, long missing) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(missing));
        gap.add(entity);
    }

    public List<ShortageEntity> toList() {
        return gap;
    }
}
