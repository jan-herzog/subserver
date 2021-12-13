package de.nebelniek.web.controller;

import org.springframework.stereotype.Controller;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.notFound;

@Controller
public class HomeController {

    public void setupRoutes() {
        get("/", (request, response) -> {
            String ref;
            try {
                ref = request.queryParams("ref");
            } catch (Exception e) {
                ref = "success";
            }
            Map<String, String> map = new HashMap<>();
            if (ref != null && ref.equals("success")) {
                String name;
                try {
                    name = request.queryParams("name");
                } catch (Exception e) {
                    name = "non-UTF8-name";
                }
                map.put("username", name);
                return new ModelAndView(map, "connected.ftl");
            }
            return new ModelAndView(new HashMap<>(), "index.ftl");
        }, new FreeMarkerEngine());
        get("/error", (request, response) -> {
            return new ModelAndView(new HashMap<>(), "error.ftl");
        }, new FreeMarkerEngine());
        get("/notfound", (request, response) -> {
            return new ModelAndView(new HashMap<>(), "notFound.ftl");
        }, new FreeMarkerEngine());
        notFound((request, response) -> {
            response.redirect("/notfound");
            return null;
        });
    }

}
