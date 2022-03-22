package shortage.prediction;

public interface ShortagePredictionRepository {
    ShortageForecast get(String refNo, DateRange range);
}
