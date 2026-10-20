package com.grupo10.epicentrogourmet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.grupo10.epicentrogourmet.services.IFestivalService;

@Controller
@RequestMapping("/")
public class HomeController {

    private final IFestivalService festivalService;

    public HomeController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping("/")
    public RedirectView redirectToHomeIndex() {
        return new RedirectView("/index");
    }

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("home/index");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (principal instanceof User user) {
            modelAndView.addObject("username", user.getUsername());
        } else {
            modelAndView.addObject("username", "anonymous");
        }

        // Cargar los festivales precargados para la vista de inicio
        modelAndView.addObject("festivales", festivalService.getAll());

        return modelAndView;
    }
}