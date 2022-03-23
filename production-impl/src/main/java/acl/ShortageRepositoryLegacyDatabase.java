package acl;

import dao.ShortageDao;
import entities.ShortageEntity;
import shortage.prediction.Shortage;
import shortage.prediction.ShortageRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ShortageRepositoryLegacyDatabase implements ShortageRepository {
    private ShortageDao shortageDao;
//    private ShortageDocumentDao shortageDocumentDao;

    @Override
    public Shortage get(String refNo) {
        List<ShortageEntity> entities = shortageDao.getForProduct(refNo);
        LocalDate found = entities.stream()
                .map(ShortageEntity::getFound)
                .findFirst().orElse(null);

        return new Shortage(
                refNo,
                found,
                entities.stream().collect(Collectors.toMap(
                        ShortageEntity::getAtDay,
                        ShortageEntity::getMissing,
                        Long::sum,
                        TreeMap::new
                )));
    }

    @Override
    public void save(Shortage shortages) {
        List<ShortageEntity> entities = shortages.map(
                (date, missing) -> createEntity(
                        shortages.refNo(),
                        shortages.found(),
                        date,
                        missing
                ));
        shortageDao.save(entities);
//        shortageDocumentDao.save(new ShortageDocumentEntity(shortages));
    }

    private ShortageEntity createEntity(String productRefNo, LocalDate found, LocalDate date, long missing) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(found);
        entity.setAtDay(date);
        entity.setMissing(missing);
        return entity;
    }

    @Override
    public void delete(String refNo) {
        shortageDao.delete(refNo);
//        shortageDocumentDao.deleteIfExist(refNo);
    }
}
