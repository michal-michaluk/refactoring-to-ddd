package shortage.prediction;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DateRange {
    private final List<LocalDate> dates;
    private final LocalDate start;

    public DateRange(LocalDate start, List<LocalDate> dates) {
        this.start = start;
        this.dates = dates;
    }

    public static DateRange from(LocalDate start, int daysAhead) {
        return new DateRange(start, Stream.iterate(start, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList())
        );
    }

    public Iterable<LocalDate> list() {
        return dates;
    }

    public LocalDate start() {
        return start;
    }
}
