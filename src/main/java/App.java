import dao.AnimalDao;
import dao.SightingDao;
import dao.Sql2oAnimalDao;
import dao.Sql2oSightingDao;
import exc.ApiError;
import exc.DaoException;
import model.Sighting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {
    private static URI dbUri;
    public static Sql2o sql2o;
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ProcessBuilder process = new ProcessBuilder();
        Integer port;

        // This tells our app that if Heroku sets a port for us, we need to use that port.
        // Otherwise, if they do not, continue using port 4567.

        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }

        port(port);

            try {
                if (System.getenv("DATABASE_URL") == null) {
                    dbUri = new URI("postgres://localhost:5432/to_do");
                    sql2o = new Sql2o("jdbc:postgresql://" + "localhost:5432" + "wildlife_tracker", "gitata", "password");
                } else {
                    dbUri = new URI(System.getenv("DATABASE_URL"));
                    int dbport = dbUri.getPort();
                    String host = dbUri.getHost();
                    String path = dbUri.getPath();
                    String username = (dbUri.getUserInfo() == null) ? null : dbUri.getUserInfo().split(":")[0];
                    String password = (dbUri.getUserInfo() == null) ? null : dbUri.getUserInfo().split(":")[1];
                    sql2o = new Sql2o("jdbc:postgresql://" + host + ":" + dbport + path, username, password);
                }

            } catch (URISyntaxException e) {
                logger.error("Unable to connect to database.");
            }

        staticFileLocation("/public");
        AnimalDao animalDao = new Sql2oAnimalDao(sql2o);
        SightingDao sightingDao = new Sql2oSightingDao(sql2o);

        before((req, res) -> {
            if (req.cookie("username") != null) {
                req.attribute("username", req.cookie("username"));
            }
        });

        before("/animals", (req, res) -> {
            if (req.attribute("username") == null) {
                res.redirect("/");
                halt();
            }
        });

        before("/animals/:id", (req, res) -> {
            if (req.attribute("username") == null) {
                res.redirect("/");
                halt();
            }
        });

        before("/animals/:id/add-sighting", (req, res) -> {
            if (req.attribute("username") == null) {
                res.redirect("/");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.attribute("username"));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            String username = req.queryParams("username").toUpperCase();
            res.cookie("username", username);
            model.put("username", username);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/animals", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("animals", animalDao.getAll());
            return new ModelAndView(model, "animals.hbs");
        }, new HandlebarsTemplateEngine());

        get("/animals/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Map<String, Object> model = new HashMap<>();
            model.put("animal", animalDao.getById(id));
            return new ModelAndView(model, "animal.hbs");
        }, new HandlebarsTemplateEngine());

        get("/animals/:id/add-sighting", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Map<String, Object> model = new HashMap<>();
            model.put("animal", animalDao.getById(id));
            return new ModelAndView(model, "add-sighting.hbs");
        }, new HandlebarsTemplateEngine());

        post("/animals/:id/add-sighting", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int animalId = Integer.parseInt(req.params("id"));
            String ranger = req.attribute("username");
            String age = req.queryParams("age");
            String health = req.queryParams("health");
            String location = req.queryParams("location");

            Sighting sighting = new Sighting(animalId, ranger, age, health, location);
            sighting.setAnimalId(animalId);
            try {
                sightingDao.add(sighting);
                model.put("sightings", sighting);
            } catch (DaoException exc) {
                throw new ApiError(500, exc.getLocalizedMessage());
            }
            return new ModelAndView(model, "custom-sighting.hbs");
        }, new HandlebarsTemplateEngine());

        get("/sightings", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("sightings", sightingDao.getAll());
            return new ModelAndView(model, "sightings.hbs");
        }, new HandlebarsTemplateEngine());

    }
}
