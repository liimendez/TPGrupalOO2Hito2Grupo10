package com.grupo10.epicentrogourmet.services.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo10.epicentrogourmet.entities.Usuario;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;
import com.grupo10.epicentrogourmet.services.IUsuarioService;
import org.springframework.security.core.userdetails.User;

@Service("usuarioService")
public class UsuarioService implements IUsuarioService, UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    // --- PARA EL LOGIN (Security) ---
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, true, true,
                new ArrayList<SimpleGrantedAuthority>() {{
                    add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getRol().name()));
                }}
        );
    }

    // --- PARA BAJA Y MODIFICACION (ABM ) ---
    @Override
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElse(null);
    }

    @Override
    public Usuario insertOrUpdate(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean remove(Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setActivo(false); // <- en vez de borrar, lo desactiva
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }
}