package com.sistema.examenes.security;

import com.sistema.examenes.servicios.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Servicio para cargar detalles de usuario

    @Autowired
    private JwtUtils jwtUtil; // Utilidad para manejar tokens JWT

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // Verifica si la solicitud incluye un encabezado "Authorization"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Extrae el token JWT eliminando el prefijo "Bearer "
            jwtToken = requestTokenHeader.substring(7);

            // Intenta resolver el nombre de usuario del token
            try {
                username = this.jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException exception) {
                System.out.println("El token ha expirado");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Token inválido, no comienza con la cadena 'Bearer'");
        }

        // Valida el usuario recuperado del token para determinar si es un token válido
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (this.jwtUtil.validateToken(jwtToken, userDetails)) {
                // Crea un objeto UsernamePasswordAuthenticationToken y establece la autenticación en el contexto de seguridad
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } else {
            System.out.println("El token no es válido");
        }

        // Continúa el procesamiento de la solicitud
        filterChain.doFilter(request, response);
    }
}
