package tools;

import entities.ShortageEntity;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ShortagesAssert {

    private final List<ShortageEntity> shortages;
    private final Map<LocalDate, Long> sums;

    public ShortagesAssert(List<ShortageEntity> shortages) {
        this.shortages = shortages;
        sums = shortages.stream().collect(Collectors.groupingBy(
                ShortageEntity::getAtDay,
                Collectors.summingLong(ShortageEntity::getMissing)
        ));
    }

    public static ShortagesAssert assertThat(List<ShortageEntity> shortages) {
        return new ShortagesAssert(shortages);
    }

    public ShortagesAssert missingPartsAt(LocalDate date, long missing) {
        Assertions.assertThat(sums)
                .describedAs("sums of missing parts per day")
                .containsEntry(date, missing);
        return this;
    }

    public ShortagesAssert shortagesAtDates(LocalDate... plusDays) {
        Assertions.assertThat(sums).containsOnlyKeys(plusDays);
        return this;
    }
}
