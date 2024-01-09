package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @RequestMapping
    public String page() {
        // Ajoutez des attributs au modèle si nécessaire
        return "templates/index.html";
    }
}
