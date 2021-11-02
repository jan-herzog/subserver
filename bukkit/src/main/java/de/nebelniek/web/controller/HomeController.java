package de.nebelniek.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@RequestParam(name = "ref", defaultValue = "null") String ref, Model model) {

        return "index";
    }

    @GetMapping("/error")
    public String error(Model model) {

        return "error";
    }

}
