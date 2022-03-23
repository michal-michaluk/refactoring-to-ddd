package demand.forecasting;

import dao.DemandDao;

import java.time.LocalDateTime;
import java.util.List;

public class DemandService {
    private DemandDao demandDao;

    public List<Demand> findFrom(String refNo, LocalDateTime form) {
        return demandDao.findFrom(form, refNo).stream()
                .map(Demand::new)
                .toList();
    }
}
