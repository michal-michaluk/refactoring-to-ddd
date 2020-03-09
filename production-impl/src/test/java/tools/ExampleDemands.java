package tools;

import entities.DemandEntity;
import entities.ManualAdjustmentEntity;
import entities.OriginalDemandEntity;
import enums.DeliverySchema;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ExampleDemands {

    static AtomicLong ids = new AtomicLong(0);

    public static DemandBuilder demandWithoutAdjustments(LocalDate date, int level) {
        return DemandBuilder.builder()
                .id(ids.getAndIncrement())
                .callofDate(date)
                .productRefNo("300900")
                .atDay(date)
                .original(OriginalDemandBuilder.builder()
                        .atDay(date)
                        .level(level)
                        .deliverySchema(DeliverySchema.tillEndOfDay));
    }

    public static Stream<DemandBuilder> demandSequence(LocalDate startDate, int... demand) {
        Stream.Builder<DemandBuilder> entries = Stream.builder();
        LocalDate date = startDate;
        for (int demanded : demand) {
            entries.add(demandWithoutAdjustments(date, demanded));
            date = date.plusDays(1);
        }
        return entries.build();
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class DemandBuilder {
        private long id;
        private LocalDate callofDate;
        private String productRefNo;
        private LocalDate atDay;
        private OriginalDemandBuilder original;
        private List<ManualAdjustmentBuilder> adjustment = new LinkedList<>();

        public DemandEntity build() {
            DemandEntity entity = new DemandEntity();
            entity.setId(id);
            entity.setCallofDate(callofDate);
            entity.setProductRefNo(productRefNo);
            entity.setAtDay(atDay);
            entity.setOriginal(original.build());
            entity.setAdjustment(adjustment.stream()
                    .map(ManualAdjustmentBuilder::build)
                    .collect(Collectors.toList())
            );
            return entity;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class OriginalDemandBuilder {
        private LocalDate atDay;
        private long level;
        private DeliverySchema deliverySchema;

        public OriginalDemandEntity build() {
            OriginalDemandEntity entity = new OriginalDemandEntity();
            entity.setAtDay(atDay);
            entity.setLevel(level);
            entity.setDeliverySchema(deliverySchema);
            return entity;
        }
    }

    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(staticName = "builder")
    public static class ManualAdjustmentBuilder {
        private long level;
        private String note;
        private DeliverySchema deliverySchema;

        public ManualAdjustmentEntity build() {
            ManualAdjustmentEntity entity = new ManualAdjustmentEntity();
            entity.setLevel(level);
            entity.setNote(note);
            entity.setDeliverySchema(deliverySchema);
            return entity;
        }
    }
}
