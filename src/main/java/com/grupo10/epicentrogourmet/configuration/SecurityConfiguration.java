package com.grupo10.epicentrogourmet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.grupo10.epicentrogourmet.services.implementation.UsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UsuarioService userService;

    public SecurityConfiguration(UsuarioService userService) {
        this.userService = userService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/index","/login", "/loginprocess", "/loginsuccess",
                            "/css/**", "/js/**", "/img/**", "/imgs/**",
                            "/images/**", "/static/**",
                            "/vendor/**", "/api/v1/**", "/error/**",
                            "/unidad-venta/new", "/unidad-venta/save").permitAll();

                    auth.requestMatchers("/admin/**", "/festival/new", "/festival/editar/**", "/festival/eliminar/**",
                            "/festival/save", "/usuarios/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN");

                    auth.requestMatchers("/empleado/mi-informacion", "/empleado/mi-informacion/**").authenticated();

                    auth.requestMatchers("/empleado/**").hasAnyAuthority("RESPONSABLE", "ROLE_RESPONSABLE");

                    auth.requestMatchers("/mi-unidad/**", "/unidad-venta/detalle/**", "/unidad-venta/**")
                            .hasAnyAuthority("RESPONSABLE", "ROLE_RESPONSABLE", "ADMIN", "ROLE_ADMIN", "EMPLEADO", "ROLE_EMPLEADO");

                    auth.requestMatchers("/pedido/new", "/pedido/new/**", "/pedido/save",
                                    "/pedido/lista", "/pedido/unidad/**")
                            .hasAnyAuthority("EMPLEADO", "ROLE_EMPLEADO", "RESPONSABLE", "ROLE_RESPONSABLE");

                    auth.requestMatchers("/festival/{id}", "/festival/{id}/**").authenticated();

                    auth.anyRequest().authenticated();
                })
                .formLogin(login -> {
                    login.loginPage("/login");
                    login.loginProcessingUrl("/loginprocess");
                    login.usernameParameter("username");
                    login.passwordParameter("password");
                    // REDIRECCIÓN POR ROL
                    login.successHandler((request, response, authentication) -> {
                        var authorities = authentication.getAuthorities();
                        String redirectUrl = "/index";
                        for (var auth : authorities) {
                            String role = auth.getAuthority();
                            if (role.equals("ROLE_ADMIN") || role.equals("ADMIN")) {
                                redirectUrl = "/admin/dashboard";
                                break;
                            }
                        }
                        response.sendRedirect(redirectUrl);
                    });
                    login.permitAll();
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/login?logout");
                    logout.permitAll();
                })
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}