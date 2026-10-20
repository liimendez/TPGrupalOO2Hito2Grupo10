package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository("usuarioRepository")
public interface IUsuarioRepository extends JpaRepository<Usuario, Serializable> {
    Optional<Usuario> findByCorreo(String correo);

}