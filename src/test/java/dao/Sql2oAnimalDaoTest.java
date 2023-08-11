package dao;

import model.Animal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oAnimalDaoTest {

    private Sql2oAnimalDao animalDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/wildlife_tracker_test";
        Sql2o sql2o = new Sql2o(connectionString, "gitata", "password");
        animalDao = new Sql2oAnimalDao(sql2o);
        //Keep connection open through entire test
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        animalDao.clearAll();
        conn.close();
    }

    @Test
    public void add_setsAnimalId_animalId() throws Exception {
        Animal animal = newAnimal();
        int originalAnimalId = animal.getId();
        animalDao.add(animal);
        assertNotEquals(originalAnimalId, animal.getId());
    }

    @Test
    public void getAll_ReturnsAddedAnimals_true() throws Exception{
        Animal animal = newAnimal();
        animalDao.add(animal);

        assertEquals(1, animalDao.getAll().size());
    }

    @Test
    public void getAll_WhenNullReturnsEmptyList_true() {
        assertEquals(0, animalDao.getAll().size());
    }

    private Animal newAnimal() {
        return new Animal("Lion");
    }
}