package dao;

import exc.DaoException;
import model.Sighting;

import java.util.List;

public interface SightingDao {
    void add(Sighting sighting) throws DaoException;

    List<Sighting> getAll();

    List<Sighting> findByAnimalId(int animalId);

    void clearAll();
}
