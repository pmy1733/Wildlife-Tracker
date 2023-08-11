package dao;

import exc.DaoException;
import model.Animal;
import model.Sighting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.List;

import static org.junit.Assert.*;

public class Sql2oSightingDaoTest {

    private Sql2oAnimalDao animalDao;
    private Sql2oSightingDao sightingDao;
    private Animal animal;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/wildlife_tracker_test";
        Sql2o sql2o = new Sql2o(connectionString, "gitata", "password");
        conn = sql2o.open();
        animalDao = new Sql2oAnimalDao(sql2o);
        sightingDao = new Sql2oSightingDao(sql2o);
        animal = new Animal("Lion");
        animalDao.add(animal);
    }

    @After
    public void tearDown() throws Exception {
        sightingDao.clearAll();
        conn.close();
    }

    @Test
    public void add_NewSightingSetsNewId_true() throws Exception {
        Sighting sighting = new Sighting(animal.getId(), "Ranger One", "Zone A");
        int originalId = sighting.getId();
        sightingDao.add(sighting);

        assertNotEquals(originalId, animal.getId());
    }

    @Test
    public void getAll_FindsMultipleSightingsWhenTheyExistForAnAnimal_2() throws Exception{
        sightingDao.add(new Sighting(animal.getId(), "Ranger One", "Zone A"));
        sightingDao.add(new Sighting(animal.getId(), "Ranger Seven", "Zone D"));

        List<Sighting> reviews = sightingDao.findByAnimalId(animal.getId());
        assertEquals(2, reviews.size());
    }

    @Test
    public void getAll_FindsMultipleSightingsWhenTheyExistForAnAnimal_3() throws Exception{
        sightingDao.add(new Sighting(animal.getId(), "ranger", "age", "health", "location"));
        sightingDao.add(new Sighting(animal.getId(), "ranger", "age", "health", "location"));
        sightingDao.add(new Sighting(animal.getId(), "ranger", "age", "health", "location"));

        List<Sighting> reviews = sightingDao.findByAnimalId(animal.getId());
        assertEquals(3, reviews.size());
    }

    @Test(expected = DaoException.class)
    public void add_AddingSightingsToNonExistentAnimalFails_DaoException() throws Exception {
        Sighting sighting = new Sighting(42, "Ranger One","Zone A");
        sightingDao.add(sighting);
    }

    @Test(expected = DaoException.class)
    public void add_AddingSightingsToNonExistentAnimalFails2_DaoException() throws Exception {
        Sighting sighting = new Sighting(45, "Ranger One","age", "health", "location");
        sightingDao.add(sighting);
    }

    //TODO: Make this work
//    @Test
//    public void getAll_ReturnsAddedSightings_true() throws Exception{
//        Sighting sighting = new Sighting(1, "ranger", "age", "health", "location");
//        sightingDao.add(sighting);
//
//        assertEquals(1, sightingDao.getAll().size());
//    }
//
//    @Test
//    public void getAll_WhenNullReturnsEmptyList_true() {
//        assertEquals(0, sightingDao.getAll().size());
//    }

}