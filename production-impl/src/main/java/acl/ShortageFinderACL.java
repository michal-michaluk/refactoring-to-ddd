package acl;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import shortage.prediction.Shortage;
import shortage.prediction.ShortagePredictionFactory;
import shortage.prediction.ShortagePredictionService;
import tools.ShortageFinder;

import java.time.LocalDate;
import java.util.List;

public class ShortageFinderACL {

    private static boolean compareWithNewModel = true;

    private ShortageFinderACL() {
    }

    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {
        List<ShortageEntity> oldCalculation = ShortageFinder.findShortages(today, daysAhead, stock, productions, demands);
        if (compareWithNewModel) {
            ShortagePredictionFactory factory = new ShortagePredictionFactoryACL(today, daysAhead, stock, productions, demands);
            ShortagePredictionService service = new ShortagePredictionService(factory);
            Shortage shortage = service.predictShortages();

            List<ShortageEntity> newCalculation = translateToLegacy(shortage);
            diff(oldCalculation, newCalculation);
        }
        return oldCalculation;
    }

    private static void diff(List<ShortageEntity> oldCalculation, List<ShortageEntity> newCalculation) {

    }

    private static List<ShortageEntity> translateToLegacy(Shortage shortage) {
        return shortage.toList();
    }

}
