package acl;

import dao.ShortageDao;
import shortage.prediction.Shortage;
import shortage.prediction.ShortageRepository;

public class ShortageRepositoryLegacyDatabase implements ShortageRepository {
    private ShortageDao shortageDao;

    @Override
    public Shortage get(String refNo) {
        return new Shortage(refNo, shortageDao.getForProduct(refNo));
    }

    @Override
    public void save(Shortage shortages) {
        shortageDao.save(shortages.toList());
    }

    @Override
    public void delete(String refNo) {
        shortageDao.delete(refNo);

    }
}
