package com.grupo10.epicentrogourmet.services.implementation;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.Usuario;
import com.grupo10.epicentrogourmet.repositories.IUsuarioRepository;

@Service("usuarioService")
public class UsuarioService implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username es el correo que escribís en el login
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.isActivo(), // <--- ACÁ ESTABA EL ERROR, vos tenés activo no enabled
                true, true, true,
                new ArrayList<SimpleGrantedAuthority>() {{
                    add(new SimpleGrantedAuthority(usuario.getRol().getRol().name()));
                }}
        );
    }
}