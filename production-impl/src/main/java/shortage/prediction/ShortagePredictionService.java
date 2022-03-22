package shortage.prediction;

import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    private final ShortageRepository shortages;
    private final ShortagePredictionRepository forecasts;

    private final NotificationsService notificationService;
    private final JiraService jiraService;
    private final Clock clock;

    private final int confShortagePredictionDaysAhead;
    private final long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortageRepository shortages, ShortagePredictionRepository forecasts, NotificationsService notificationService, JiraService jiraService, Clock clock, int confShortagePredictionDaysAhead, long confIncreaseQATaskPriorityInDays) {
        this.shortages = shortages;
        this.forecasts = forecasts;
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
            Shortage calculated = forecast.predictShortages();
            Shortage previous = shortages.get(refNo);
            if (calculated.newShortagesThan(previous)) {
                notificationService.markOnPlan(calculated.toList());
                if (forecast.hasAnyLocked() &&
                        calculated.hasShortageBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(refNo);
                }
                shortages.save(calculated);
            }
            if (calculated.isShortageSolved(previous)) {
                shortages.delete(refNo);
            }
        }
    }

    public void findShortagesFromLogistic(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
        ShortageForecast forecast = forecasts.get(productRefNo, range);
        Shortage calculated = forecast.predictShortages();
        Shortage previous = shortages.get(productRefNo);

        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (calculated.newShortagesThan(previous)) {
            notificationService.alertPlanner(calculated.toList());
            // TODO REFACTOR: policy why to increase task priority
            if (forecast.hasAnyLocked() &&
                    calculated.hasShortageBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortages.save(calculated);
        }
        if (calculated.isShortageSolved(previous)) {
            shortages.delete(productRefNo);
        }
    }

    public void processShortagesFromQuality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
        ShortageForecast forecast = forecasts.get(productRefNo, range);
        Shortage calculated = forecast.predictShortages();
        Shortage previous = shortages.get(productRefNo);

        if (calculated.newShortagesThan(previous)) {
            notificationService.softNotifyPlanner(calculated.toList());
            if (forecast.hasAnyLocked() &&
                    calculated.hasShortageBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortages.save(calculated);
        }
        if (calculated.isShortageSolved(previous)) {
            shortages.delete(productRefNo);
        }
    }

    public void processShortagesFromWarehouse(List<String> products) {
        for (String productRefNo : products) {
            LocalDate today = LocalDate.now(clock);
            DateRange range = DateRange.from(today, confShortagePredictionDaysAhead);
            ShortageForecast forecast = forecasts.get(productRefNo, range);
            Shortage calculated = forecast.predictShortages();
            Shortage previous = shortages.get(productRefNo);

            if (calculated.newShortagesThan(previous)) {
                notificationService.alertPlanner(calculated.toList());
                if (forecast.hasAnyLocked() &&
                        calculated.hasShortageBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(productRefNo);
                }
                shortages.save(calculated);
            }
            if (calculated.isShortageSolved(previous)) {
                shortages.delete(productRefNo);
            }
        }
    }
}
