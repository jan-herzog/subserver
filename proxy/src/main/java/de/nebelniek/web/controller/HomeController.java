package de.nebelniek.web.controller;

import org.springframework.stereotype.Controller;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.notFound;

@Controller
public class HomeController {

    public void setupRoutes() {
        get("/", ((request, response) -> {
            String ref = request.queryParams("ref");
            Map<String, String> map = new HashMap<>();
            if (ref.equals("success")) {
                String name = request.queryParams("name");
                map.put("name", name);
                return new ModelAndView(map, "connected.ftl");
            }
            return new ModelAndView(new HashMap<>(), "index.ftl");
        }));
        get("/error", ((request, response) -> {
            return new ModelAndView(new HashMap<>(), "error.ftl");
        }));
        notFound(((request, response) -> {
            return new ModelAndView(new HashMap<>(), "notFound.ftl");
        }));
    }

}
