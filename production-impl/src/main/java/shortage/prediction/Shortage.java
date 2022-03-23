package shortage.prediction;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class Shortage {
    private final String productRefNo;
    private final LocalDate found;
    private final SortedMap<LocalDate, Long> shortages;

    public Shortage(String productRefNo, LocalDate found, SortedMap<LocalDate, Long> shortages) {
        this.productRefNo = productRefNo;
        this.found = found;
        this.shortages = shortages;
    }

    static Builder builder(String productRefNo) {
        return new Builder(productRefNo);
    }

    public String refNo() {
        return productRefNo;
    }

    public LocalDate found() {
        return found;
    }

    public <T> List<T> map(BiFunction<LocalDate, Long, T> fun) {
        return shortages.entrySet().stream()
                .map(e -> fun.apply(e.getKey(), e.getValue()))
                .toList();
    }

    public static class Builder {
        private final String productRefNo;
        private final SortedMap<LocalDate, Long> shortages = new TreeMap<>();

        public Builder(String productRefNo) {
            this.productRefNo = productRefNo;
        }

        public void add(LocalDate day, long missing) {
            shortages.put(day, Math.abs(missing));
        }

        public Shortage build() {
            return new Shortage(
                    productRefNo,
                    LocalDate.now(),
                    Collections.unmodifiableSortedMap(shortages)
            );
        }
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
        return shortages.firstKey().isBefore(date);
    }
}
