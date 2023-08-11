package dao;

import exc.DaoException;
import model.Sighting;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oSightingDao implements SightingDao {

    private final Sql2o sql2o;

    public Sql2oSightingDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Sighting sighting) throws DaoException {
        String sql = "INSERT INTO sightings(animal_id, ranger, age, health, location) VALUES (:animalId, :ranger, :age, :health, :location)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(sighting)
                    .executeUpdate()
                    .getKey();
            sighting.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException(ex, "Problem adding sighting.");
        }
    }

    @Override
    public List<Sighting> getAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM sightings")
                    .executeAndFetch(Sighting.class);
        }
    }

    @Override
    public List<Sighting> findByAnimalId(int animalId) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM sightings WHERE animal_id = :animal_id")
                    .addColumnMapping("animal_id", "animalId")
                    .addParameter("animal_id", animalId)
                    .executeAndFetch(Sighting.class);
        }
    }

    @Override
    public void clearAll() {
        String sql = "DELETE from sightings";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql).executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }
}
