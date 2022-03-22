package shortage.prediction;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Shortage {
    private final String productRefNo;
    private final List<ShortageEntity> shortages;

    public Shortage(String productRefNo) {
        this.productRefNo = productRefNo;
        this.shortages = new LinkedList<>();
    }

    public Shortage(String productRefNo, List<ShortageEntity> shortages) {
        this.productRefNo = productRefNo;
        this.shortages = shortages;
    }

    public void add(LocalDate day, long missing) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(missing));
        shortages.add(entity);
    }

    public List<ShortageEntity> toList() {
        return shortages;
    }

    public boolean newShortagesThan(Shortage other) {
        return this.isNotEmpty() && this.isDifferent(other);
    }

    private boolean isNotEmpty() {
        return !shortages.isEmpty();
    }

    private boolean isDifferent(Shortage other) {
        return !equals(other);
    }

    public boolean isShortageSolved(Shortage other) {
        return shortages.isEmpty() && !other.shortages.isEmpty();
    }

    public boolean hasShortageBefore(LocalDate date) {
        return shortages.get(0).getAtDay().isBefore(date);
    }
}
