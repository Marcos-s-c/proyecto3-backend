package com.sistema.examenes.security;

import com.sistema.examenes.servicios.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity  // Habilita la seguridad web en la aplicación
@Configuration  // Marca esta clase como una clase de configuración de Spring
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Habilita la seguridad basada en métodos

public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler; // Manejador de respuestas no autorizadas

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;  // Implementación personalizada de detalles de usuario

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;  // Filtro personalizado para autenticación JWT

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();  // Configura el administrador de autenticación
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // Configura el codificador de contraseñas (¡esto no es seguro en un entorno de producción!)
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsServiceImpl).passwordEncoder(passwordEncoder()); // Configura el servicio de detalles de usuario y el codificador de contraseñas
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Deshabilita la protección CSRF
                .cors().disable()  // Deshabilita la configuración CORS
                .authorizeRequests()
                .antMatchers("/generate-token", "/usuarios/").permitAll()  // Permite el acceso público a estas rutas
                .antMatchers(HttpMethod.OPTIONS).permitAll()  // Permite las solicitudes OPTIONS
                .anyRequest().authenticated()  // Requiere autenticación para cualquier otra solicitud
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)  // Manejo de respuestas no autorizadas
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Configura la política de creación de sesiones como "sin estado" (STATELESS)

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Agrega el filtro de autenticación JWT antes del filtro de nombre de usuario y contraseña
    }
}
