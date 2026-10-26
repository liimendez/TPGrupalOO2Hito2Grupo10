package com.grupo10.epicentrogourmet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.grupo10.epicentrogourmet.services.IFestivalService;
import com.grupo10.epicentrogourmet.services.IUnidadVentaService;

@Controller
@RequestMapping("/")
public class HomeController {

    private final IFestivalService festivalService;
    private final IUnidadVentaService unidadVentaService;

    public HomeController(IFestivalService festivalService, IUnidadVentaService unidadVentaService) {
        this.festivalService = festivalService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("/")
    public RedirectView redirectToHomeIndex() {
        return new RedirectView("/index");
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam(value = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView("home/index");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (principal instanceof User user) {
            modelAndView.addObject("username", user.getUsername());
        } else {
            modelAndView.addObject("username", "anonymous");
        }


        boolean esAdmin = false;
        if (authentication != null) {
            esAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ADMIN") || a.equals("ROLE_ADMIN"));
        }
        modelAndView.addObject("esAdmin", esAdmin);
        // ---------------------------------

        modelAndView.addObject("festivales", festivalService.getAll());
        modelAndView.addObject("unidades", unidadVentaService.getAll());
        modelAndView.addObject("success", success);

        return modelAndView;
    }
}