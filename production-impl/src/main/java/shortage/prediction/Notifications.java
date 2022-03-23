package shortage.prediction;

public interface Notifications {
    void alertPlanner(Shortage shortages);

    void softNotifyPlanner(Shortage shortages);

    void markOnPlan(Shortage shortages);

    void increasePriorityFor(String productRefNo);
}
