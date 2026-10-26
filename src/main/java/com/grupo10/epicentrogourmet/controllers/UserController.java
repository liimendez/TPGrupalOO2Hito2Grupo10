package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Usuario;
import com.grupo10.epicentrogourmet.helpers.ViewRouteHelper;
import com.grupo10.epicentrogourmet.services.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final IUsuarioService usuarioService;

    public UserController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return ViewRouteHelper.USER_LOGIN;
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess() {
        return "redirect:/index";
    }

    @GetMapping("/usuarios/lista")
    public String listaUsuarios(Model model) {
        var responsables = usuarioService.getAll().stream()
                .filter(u -> u.getRol() != null
                        && u.getRol().getRol() != null
                        && u.getRol().getRol().name().equals("RESPONSABLE"))
                .toList();
        model.addAttribute("usuarios", responsables);
        return "user/lista";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public ModelAndView eliminar(@PathVariable Long id) {
        if (id == 1) {
            ModelAndView mav = new ModelAndView("user/lista");
            mav.addObject("usuarios", usuarioService.getAll().stream()
                    .filter(u -> !u.getId().equals(1L)
                            && u.getRol() != null
                            && u.getRol().getRol().name().equals("RESPONSABLE")).toList());
            mav.addObject("error", "No podés dar de baja al admin principal");
            return mav;
        }
        Usuario u = usuarioService.findById(id).orElseThrow();
        u.setActivo(false);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista");
    }

    @GetMapping("/usuarios/alta/{id}")
    public ModelAndView alta(@PathVariable Long id) {
        Usuario u = usuarioService.findById(id).orElseThrow();
        u.setActivo(true);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista");
    }

    @GetMapping("/usuarios/editar/{id}")
    public ModelAndView editarForm(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("user/editar");
        mav.addObject("usuario", usuarioService.findById(id).orElseThrow());
        return mav;
    }

    @PostMapping("/usuarios/editar/{id}")
    public ModelAndView editarSave(@PathVariable Long id, @RequestParam String correo) {
        Usuario u = usuarioService.findById(id).orElseThrow();
        u.setCorreo(correo);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista");
    }
}