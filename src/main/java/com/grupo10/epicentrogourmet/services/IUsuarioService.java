package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.Usuario;

public interface IUsuarioService {
    List<Usuario> getAll();
    Optional<Usuario> findById(Long id);
    Usuario findByCorreo(String correo);
    Usuario insertOrUpdate(Usuario usuario);
    boolean remove(Long id);
}