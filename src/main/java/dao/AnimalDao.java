package dao;

import exc.DaoException;
import model.Animal;

import java.util.List;

public interface AnimalDao {
    void add(Animal animal) throws DaoException;

    List<Animal> getAll();

    void clearAll();

    Animal getById(int id);
}
