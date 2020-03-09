package tools;

import entities.FormEntity;
import entities.LineEntity;
import entities.ProductionEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ExampleProductions {

    static AtomicLong ids = new AtomicLong(0);


    public static ProductionBuilder productionOf300900(long lineId, LocalDate date, int hour) {
        return ProductionBuilder.builder()
                .productionId(ids.getAndIncrement())
                .line(LineBuilder.builder()
                        .id(lineId)
                        .maxWeight(10_000))
                .form(FormBuilder.builder()
                        .refNo("300900")
                        .outputPerMinute(30)
                        .utilization(2.0)
                        .weight(5_000)
                        .startAndWormUp(Duration.ofMinutes(20))
                        .endAndCleaning(Duration.ofMinutes(10)))
                .start(date.atTime(hour, 0))
                .duration(Duration.ofHours(4))
                .startAndWormUp(Duration.ofMinutes(20))
                .endAndCleaning(Duration.ofMinutes(10))
                .speed(1.0)
                .outputCalculated()
                .utilization(2.0)
                .color(null)
                .note(null);
    }

    @NoArgsConstructor(staticName = "productions")
    public static class ProductionPlanBuilder {

        int line;
        List<ProductionBuilder> prods = new ArrayList<>();
        Supplier<ProductionBuilder> prototype = () -> productionOf300900(this.line, LocalDate.now(), 7);

        public static ProductionPlanBuilder forProductionLine(int lineId) {
            ProductionPlanBuilder builder = new ProductionPlanBuilder();
            builder.line = lineId;
            return builder;
        }

        public ProductionPlanBuilder plannedOutputs(String date, int startAt, int... outputs) {
            return plannedOutputs(LocalDate.parse(date), startAt, outputs);
        }

        public ProductionPlanBuilder plannedOutputs(LocalDate start, int startAt, int... outputs) {
            for (int output : outputs) {
                start = start.plusDays(1);
                ProductionBuilder builder = prototype.get()
                        .start(start.atTime(LocalTime.of(startAt, 0)))
                        .output(output);
                prods.add(builder);
            }
            return this;
        }

        Stream<ProductionBuilder> build() {
            return prods.stream();
        }
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class ProductionBuilder {
        private long productionId;
        private LineBuilder line;
        private FormBuilder form;
        private LocalDateTime start;
        private Duration duration;
        private Duration startAndWormUp;
        private Duration endAndCleaning;
        private double speed;
        private long output;
        private double utilization;
        private String color;
        private String note;

        public ProductionBuilder outputCalculated() {
            output = (long) (speed * duration.minus(startAndWormUp).minus(endAndCleaning).getSeconds() / 60 * form.outputPerMinute);
            return this;
        }

        public ProductionEntity build() {
            ProductionEntity entity = new ProductionEntity();
            entity.setProductionId(productionId);
            entity.setLine(line.build());
            entity.setForm(form.build());
            entity.setStart(start);
            entity.setDuration(duration);
            entity.setStartAndWormUp(startAndWormUp);
            entity.setEndAndCleaning(endAndCleaning);
            entity.setSpeed(speed);
            entity.setOutput(output);
            entity.setUtilization(utilization);
            entity.setColor(color);
            entity.setNote(note);
            return entity;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class FormBuilder {
        private String refNo;
        private double outputPerMinute;
        private double utilization;
        private double weight;
        private Duration startAndWormUp;
        private Duration endAndCleaning;

        public FormEntity build() {
            FormEntity entity = new FormEntity();
            entity.setRefNo(refNo);
            entity.setOutputPerMinute(outputPerMinute);
            entity.setUtilization(utilization);
            entity.setWeight(weight);
            entity.setStartAndWormUp(startAndWormUp);
            entity.setEndAndCleaning(endAndCleaning);
            return entity;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class LineBuilder {
        private long id;
        private double maxWeight;

        public LineEntity build() {
            LineEntity entity = new LineEntity();
            entity.setId(id);
            entity.setMaxWeight(maxWeight);
            return entity;
        }
    }
}
