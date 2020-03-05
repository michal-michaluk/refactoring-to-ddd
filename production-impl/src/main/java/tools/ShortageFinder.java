package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageFinder {

    private ShortageFinder() {
    }

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     */
    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {

        // Coding Dojo Refactoring to Domain Model
        // 1st goal: cut off dependencies to production-planing internals
        // get rid of dependency to List<ProductionEntity> productions
        // introduce *Custom Collection* called ProductionOutputs
        // with interface getOutput(date)
        // move initialisation of ProductionOutputs to separate method

        // 2nd goal: cut off dependencies to demand-forecasting internals
        // get rid of dependency to List<DemandEntity> demands
        // introduce *Custom Collection* called Demands
        // with interface getDemand(date) -> DailyDemand
        // introduce *Value Object* called DailyDemand
        // with interface getLevel -> long
        //                hasDeliverySchema(deliverySchema) -> true/false
        // move initialisation of Demands to separate method

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        String productRefNo = null;
        HashMap<LocalDate, List<ProductionEntity>> outputs = new HashMap<>();
        for (ProductionEntity production : productions) {
            if (!outputs.containsKey(production.getStart().toLocalDate())) {
                outputs.put(production.getStart().toLocalDate(), new ArrayList<>());
            }
            outputs.get(production.getStart().toLocalDate()).add(production);
            productRefNo = production.getForm().getRefNo();
        }
        HashMap<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }

        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        long level = stock.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : dates) {
            DemandEntity demand = demandsPerDay.get(day);
            if (demand == null) {
                for (ProductionEntity production : outputs.get(day)) {
                    level += production.getOutput();
                }
                continue;
            }
            long produced = 0;
            for (ProductionEntity production : outputs.get(day)) {
                produced += production.getOutput();
            }

            long levelOnDelivery;
            if (Util.getDeliverySchema(demand) == DeliverySchema.atDayStart) {
                levelOnDelivery = level - Util.getLevel(demand);
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - Util.getLevel(demand) + produced;
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new NotImplementedException();
            } else {
                // TODO implement other variants
                throw new NotImplementedException();
            }

            if (levelOnDelivery < 0) {
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(productRefNo);
                entity.setFound(LocalDate.now());
                entity.setAtDay(day);
                entity.setMissing(-levelOnDelivery);
                gap.add(entity);
            }
            long endOfDayLevel = level + produced - Util.getLevel(demand);
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }
}
