package com.grupo10.epicentrogourmet.controllers;

import com.grupo10.epicentrogourmet.entities.Usuario;
import com.grupo10.epicentrogourmet.services.IUsuarioService;
import org.springframework.stereotype.Controller;
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
        return "user/login";
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess() {
        return "redirect:/";
    }

    @GetMapping("/usuarios/lista")
    public ModelAndView lista() {
        ModelAndView mav = new ModelAndView("user/lista");
        mav.addObject("usuarios", usuarioService.getAll()); // <- getAll
        return mav;
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public ModelAndView eliminar(@PathVariable Long id) {
        if (id == 1) {
            ModelAndView mav = new ModelAndView("user/lista");
            mav.addObject("usuarios", usuarioService.getAll()); // <- getAll
            mav.addObject("error", "No podés dar de baja al admin principal");
            return mav;
        }
        Usuario u = usuarioService.findById(id).orElseThrow();
        u.setActivo(false);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista?eliminado=true");
    }

    @GetMapping("/usuarios/alta/{id}")
    public ModelAndView alta(@PathVariable Long id) {
        Usuario u = usuarioService.findById(id).orElseThrow();
        u.setActivo(true);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista?alta=true");
    }

    @GetMapping("/usuarios/editar/{id}")
    public ModelAndView editarForm(@PathVariable Long id) {
        Usuario u = usuarioService.findById(id).orElseThrow();
        ModelAndView mav = new ModelAndView("user/editar");
        mav.addObject("usuario", u);
        return mav;
    }

    @PostMapping("/usuarios/editar/{id}")
    public ModelAndView editarSave(@PathVariable Long id, @RequestParam String correo) {
        Usuario u = usuarioService.findById(id).orElseThrow();
        Usuario otro = usuarioService.findByCorreo(correo);
        if (otro != null && !otro.getId().equals(id)) {
            ModelAndView mav = new ModelAndView("user/editar");
            mav.addObject("usuario", u);
            mav.addObject("error", "No se puede usar ese correo, está en uso: " + correo);
            return mav;
        }
        u.setCorreo(correo);
        usuarioService.insertOrUpdate(u);
        return new ModelAndView("redirect:/usuarios/lista?modificado=true");
    }
}