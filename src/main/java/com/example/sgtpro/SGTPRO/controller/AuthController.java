package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.security.JwtService;
import com.example.sgtpro.SGTPRO.service.RateLimitingService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Seguridad y Accesos", description = "Endpoints públicos para obtener el Token JWT")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitingService rateLimitingService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          RateLimitingService rateLimitingService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitingService = rateLimitingService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest){
        String ip = getClientIP(httpRequest);

        if (rateLimitingService.isBlocked(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));
        } catch (BadCredentialsException e) {
            rateLimitingService.registerFailed(ip);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        rateLimitingService.registerSuccess(ip);
        
        Usuario user = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", user.getIdUsuario());
        claims.put("nombre", user.getNombreCompleto());
        claims.put("rol", user.getRol().getNombre());

        String jwtToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(claims, user);
        return ResponseEntity.ok(new AuthResponse(jwtToken, refreshToken));
    }

    @Operation(summary = "Refrescar token", description = "Genera un nuevo access token a partir del refresh token.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String token = request.get("refreshToken");
        if (token == null || !jwtService.canBeRefreshed(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String correo = jwtService.extractUsernameNoExpirationCheck(token);
        Usuario user = usuarioRepository.findByCorreo(correo).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", user.getIdUsuario());
        claims.put("nombre", user.getNombreCompleto());
        claims.put("rol", user.getRol().getNombre());

        String newToken = jwtService.generateToken(claims, user);
        return ResponseEntity.ok(new AuthResponse(newToken, null));
    }

    @Operation(summary = "Obtener perfil actual", description = "Retorna la información del usuario autenticado.")
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getProfile() {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new UserInfo(user.getIdUsuario(), user.getCorreo(), user.getNombreCompleto(), user.getRol().getNombre()));
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza nombre y/o correo del usuario autenticado, retorna nuevo token.")
    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody ProfileUpdateRequest request) {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            user.setNombreCompleto(request.getNombre());
        }
        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            if (!request.getCorreo().equals(user.getCorreo()) && usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            user.setCorreo(request.getCorreo());
        }

        usuarioRepository.save(user);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", user.getIdUsuario());
        claims.put("nombre", user.getNombreCompleto());
        claims.put("rol", user.getRol().getNombre());

        String jwtToken = jwtService.generateToken(claims, user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado.")
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "La contraseña actual no es correcta."));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }

    private String getClientIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static class LoginRequest {
        private String correo;
        private String password;

        public LoginRequest() {}
        public LoginRequest(String correo, String password) {
            this.correo = correo;
            this.password = password;
        }

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String refreshToken;

        public AuthResponse() {}
        public AuthResponse(String token) { this.token = token; }
        public AuthResponse(String token, String refreshToken) { this.token = token; this.refreshToken = refreshToken; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class UserInfo {
        private Integer id;
        private String correo;
        private String nombre;
        private String rol;

        public UserInfo() {}
        public UserInfo(Integer id, String correo, String nombre, String rol) {
            this.id = id;
            this.correo = correo;
            this.nombre = nombre;
            this.rol = rol;
        }

        public Integer getId() { return id; }
        public String getCorreo() { return correo; }
        public String getNombre() { return nombre; }
        public String getRol() { return rol; }
    }

    public static class ProfileUpdateRequest {
        private String nombre;
        private String correo;

        public ProfileUpdateRequest() {}
        public ProfileUpdateRequest(String nombre, String correo) {
            this.nombre = nombre;
            this.correo = correo;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public ChangePasswordRequest() {}
        public ChangePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
}
