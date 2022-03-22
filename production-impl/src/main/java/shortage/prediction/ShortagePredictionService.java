package shortage.prediction;

public class ShortagePredictionService {
    private ShortagePredictionFactory factory;

    public ShortagePredictionService(ShortagePredictionFactory factory) {
        this.factory = factory;
    }

    public Shortage predictShortages() {
        ShortageForecast forecast = factory.create();
        return forecast.predictShortages();
    }
}
