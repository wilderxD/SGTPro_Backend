package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Seguridad y Accesos", description = "Endpoints públicos para obtener el Token JWT")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioRepository usuarioRepository){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));
        
        Usuario user = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();
        
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
    
    
    public static class LoginRequest{
        private String correo;
        private String password;

        public LoginRequest(String correo, String password) {
            this.correo = correo;
            this.password = password;
        }

        public LoginRequest() {
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }                
    }
        
    
    public static class AuthResponse{
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }

        public AuthResponse() {
        }

        public String getToken() {
            return token;
        }
        
    }
    
}
