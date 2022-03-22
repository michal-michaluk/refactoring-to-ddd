package shortage.prediction;

public interface ShortageRepository {
    Shortage get(String refNo);

    void save(Shortage calculated);

    void delete(String refNo);
}
