package shortage.prediction;

import dao.*;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;
import external.StockService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    private final ShortagePredictionRepository forecasts;
    private final ShortageDao shortageDao;

    private final NotificationsService notificationService;
    private final JiraService jiraService;
    private final Clock clock;

    private final int confShortagePredictionDaysAhead;
    private final long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortagePredictionRepository factory, ProductionDao productionDao, LineDao lineDao, FormDao formDao, ShortageDao shortageDao, StockService stockService, DemandDao demandDao, NotificationsService notificationService, JiraService jiraService, Clock clock, int confShortagePredictionDaysAhead, long confIncreaseQATaskPriorityInDays) {
        this.forecasts = factory;
        this.shortageDao = shortageDao;
        this.notificationService = notificationService;
        this.jiraService = jiraService;
        this.clock = clock;
        this.confShortagePredictionDaysAhead = confShortagePredictionDaysAhead;
        this.confIncreaseQATaskPriorityInDays = confIncreaseQATaskPriorityInDays;
    }

    public void processShortagesAfterPlanChanged(List<String> products) {
        LocalDate today = LocalDate.now(clock);
        DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);

        for (String refNo : products) {
            ShortageForecast forecast = forecasts.get(refNo, range);
            List<ShortageEntity> shortages = forecast.predictShortages().toList();
            List<ShortageEntity> previous = shortageDao.getForProduct(refNo);
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (forecast.hasAnyLocked() &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(refNo);
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(refNo);
            }
        }
    }

    public void findShortagesFromLogistic(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
        ShortageForecast forecast = forecasts.get(productRefNo, range);
        List<ShortageEntity> shortages = forecast.predictShortages().toList();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (forecast.hasAnyLocked() &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesFromQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
        ShortageForecast forecast = forecasts.get(productRefNo, range);
        List<ShortageEntity> shortages = forecast.predictShortages().toList();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (forecast.hasAnyLocked() &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesFromWarehouse(List<String> products) {
        for (String productRefNo : products) {
            LocalDate today = LocalDate.now(clock);
            DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
            ShortageForecast forecast = forecasts.get(productRefNo, range);
            List<ShortageEntity> shortages = forecast.predictShortages().toList();

            List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
            if (shortages != null && !shortages.equals(previous)) {
                notificationService.alertPlanner(shortages);
                if (forecast.hasAnyLocked() &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(productRefNo);
                }
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(productRefNo);
            }
        }
    }
}
