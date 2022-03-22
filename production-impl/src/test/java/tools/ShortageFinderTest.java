package tools;

import entities.*;
import enums.DeliverySchema;
import external.CurrentStock;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ShortageFinderTest {

    AtomicLong ids = new AtomicLong(0);
    private LocalDate date = LocalDate.now();

    @Test
    public void findShortages() {
        CurrentStock stock = new CurrentStock(1000, 200);
        print(stock);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                date.plusDays(1), 7,
                stock,
                productions(
                        prod(0, 1, 7), prod(0, 1, 14),
                        prod(0, 2, 7), prod(0, 2, 14),
                        prod(0, 3, 7), prod(0, 3, 14),
                        prod(0, 4, 7), prod(0, 4, 14),
                        prod(0, 5, 7), prod(0, 5, 14),
                        prod(0, 6, 7), prod(0, 6, 14),
                        prod(0, 7, 7), prod(0, 7, 14)
                ),
                demands(demand(2, 17000), demand(3, 17000))
        );
        print(shortages);
        Assert.assertEquals(2, shortages.size());
        Assert.assertEquals(date.plusDays(2), shortages.get(0).getAtDay());
        Assert.assertEquals(3400, shortages.get(0).getMissing());
        Assert.assertEquals(date.plusDays(3), shortages.get(1).getAtDay());
        Assert.assertEquals(7800, shortages.get(1).getMissing());
    }

    private void print(CurrentStock stock) {
        System.out.println("shortages: " + stock.getLevel());
    }

    private void print(List<ShortageEntity> shortages) {
        System.out.println("shortages: " + shortages.stream().map(s -> s.getAtDay() + " " + s.getMissing())
                .collect(Collectors.joining(", ")));
    }

    private List<ProductionEntity> productions(ProductionEntity... productions) {
        System.out.println("production: " + Stream.of(productions)
                .map(prod -> prod.getStart().toLocalDate() + " " + prod.getOutput())
                .collect(Collectors.joining(", ")));
        return asList(productions);
    }

    private List<DemandEntity> demands(DemandEntity... demands) {
        System.out.println("demands: " + Stream.of(demands)
                .map(prod -> prod.getDay() + " " + prod.getOriginal().getLevel())
                .collect(Collectors.joining(", ")));
        return asList(demands);
    }

    private DemandEntity demand(int plusDays, int level) {
        DemandEntity entity = new DemandEntity();
        entity.setId(ids.getAndIncrement());
        entity.setCallofDate(date.minusDays(2));
        entity.setProductRefNo("300900");
        entity.setAtDay(date.plusDays(plusDays));
        OriginalDemandEntity original = new OriginalDemandEntity();
        original.setAtDay(date.plusDays(plusDays));
        original.setLevel(level);
        original.setDeliverySchema(DeliverySchema.atDayStart);
        entity.setOriginal(original);
        entity.setAdjustment(new ArrayList<>());
        return entity;
    }

    private ProductionEntity prod(long lineId, int plusDays, int hour) {
        ProductionEntity entity = new ProductionEntity();
        LineEntity line = createLine(lineId);
        FormEntity form = createForm300900();
        entity.setProductionId(ids.getAndIncrement());
        entity.setLine(line);
        entity.setForm(form);
        entity.setStart(date.plusDays(plusDays).atTime(hour, 0));
        entity.setDuration(Duration.ofHours(4));
        entity.setStartAndWormUp(Duration.ofMinutes(20));
        entity.setEndAndCleaning(Duration.ofMinutes(10));
        entity.setSpeed(1.0);
        entity.setOutput(
                (long) (entity.getSpeed() *
                        entity.getDuration()
                                .minus(entity.getStartAndWormUp())
                                .minus(entity.getEndAndCleaning()).getSeconds()
                        / 60 * form.getOutputPerMinute())
        );
        entity.setUtilization(2.0);
        entity.setColor(null);
        entity.setNote(null);
        return entity;
    }

    private LineEntity createLine(long id) {
        LineEntity line = new LineEntity();
        line.setId(id);
        line.setMaxWeight(10_000);
        return line;
    }

    private FormEntity createForm300900() {
        FormEntity form = new FormEntity();
        form.setRefNo("300900");
        form.setOutputPerMinute(30);
        form.setUtilization(2.0);
        form.setWeight(5_000);
        form.setStartAndWormUp(Duration.ofMinutes(20));
        form.setEndAndCleaning(Duration.ofMinutes(10));
        return form;
    }
}
