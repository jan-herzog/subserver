package de.nebelniek.web.controller;

import org.springframework.stereotype.Controller;
import spark.ModelAndView;

import java.util.HashMap;

import static spark.Spark.get;

@Controller
public class HomeController {

    public void setupRoutes() {
        get("/", ((request, response) -> {
            String ref = request.queryParams("ref");
            return new ModelAndView(new HashMap<>(), "index");
        }));
        get("/error", ((request, response) -> {

            return new ModelAndView(new HashMap<>(), "error");
        }));
    }

}
