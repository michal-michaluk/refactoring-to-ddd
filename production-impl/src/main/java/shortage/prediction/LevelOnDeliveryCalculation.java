package shortage.prediction;

public interface LevelOnDeliveryCalculation {

    LevelOnDeliveryCalculation atDayStart = (demand, level, produced) -> level - demand;
    LevelOnDeliveryCalculation tillEndOfDay = (demand, level, produced) -> level - demand + produced;
    LevelOnDeliveryCalculation error = (demand, level, produced) -> {
        throw new UnsupportedOperationException();
    };

    long levelOnDelivery(long demand, long level, long produced);
}
