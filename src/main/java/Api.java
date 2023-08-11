import com.google.gson.Gson;
import dao.AnimalDao;
import dao.Sql2oAnimalDao;
import exc.ApiError;
import model.Animal;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Api {
    public static void main(String[] args) {
        String connectionString = "jdbc:postgresql://localhost:5432/wildlife_tracker";
        Sql2o sql2o = new Sql2o(connectionString, "gitata", "password");
        Gson gson = new Gson();

        AnimalDao animalDao = new Sql2oAnimalDao(sql2o);

        post("/animals", "application/json", (req, res) -> {
            Animal animal = gson.fromJson(req.body(), Animal.class);
            animalDao.add(animal);
            res.status(201);
            return animal;
        }, gson::toJson);

        get("/animals", "application/json", (req, res) ->
                animalDao.getAll(), gson::toJson);

        get("/animals/:id", "application/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Animal animal = animalDao.getById(id);
            if (animal == null) {
                throw new ApiError(404, "Could not find animal with id " + id);
            }
            return animal;
        }, gson::toJson);

        exception(ApiError.class,(exc, req, res) -> {
            ApiError err = (ApiError) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json");
            res.status(err.getStatus());
            res.body(gson.toJson(jsonMap));
        } );

        after((req, res) -> {
            res.type("application/json");
        } );
        //TODO: Implement API tests
    }
}