package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void login_DebeRetornarToken_CuandoCredencialesSonCorrectas() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo("admin@test.com");
        usuario.setPassword("hash");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByCorreo("admin@test.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token-test");

        String loginJson = objectMapper.writeValueAsString(
                new AuthController.LoginRequest("admin@test.com", "123456"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-test"));
    }

    @Test
    void login_DebeRetornarStatus401_CuandoCredencialesSonInvalidas() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        String loginJson = objectMapper.writeValueAsString(
                new AuthController.LoginRequest("admin@test.com", "wrong"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }
}
