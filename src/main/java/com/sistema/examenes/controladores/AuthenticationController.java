package com.sistema.examenes.controladores;

import com.sistema.examenes.security.JwtUtils;
import com.sistema.examenes.modelo.JwtRequest;
import com.sistema.examenes.modelo.JwtResponse;
import com.sistema.examenes.modelo.Usuario;
import com.sistema.examenes.servicios.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager; // Gestor de autenticación de Spring Security

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Implementación personalizada de detalles de usuario

    @Autowired
    private JwtUtils jwtUtils; // Utilidad para manejar tokens JWT

    // Endpoint para generar un token JWT
    @PostMapping("/generate-token")
    public ResponseEntity<?> generarToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            autenticar(jwtRequest.getUsername(), jwtRequest.getPassword());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Usuario no encontrado");
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    // Método para autenticar al usuario
    private void autenticar(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException exception) {
            throw new Exception("USUARIO DESHABILITADO " + exception.getMessage());
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales inválidas " + e.getMessage());
        }
    }

    // Endpoint para obtener información del usuario autenticado
    @GetMapping("/actual-usuario")
    public Usuario obtenerUsuarioActual(Principal principal) {
        return (Usuario) this.userDetailsService.loadUserByUsername(principal.getName());
    }
}
