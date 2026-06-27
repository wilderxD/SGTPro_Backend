package com.example.sgtpro.SGTPRO;

import com.example.sgtpro.SGTPRO.entity.Rol;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.repository.RolRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class SgtproIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        Rol rol = new Rol();
        rol.setNombre("ROLE_JEFE_TALLER");
        rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setCorreo("admin@test.com");
        usuario.setNombreCompleto("Admin Test");
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    @Test
    void flujoCompleto_LoginYAccesoAEndpointProtegido() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"correo": "admin@test.com", "password": "123456"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readValue(loginResponse, Map.class).get("token").toString();
        assertNotNull(token);

        mockMvc.perform(get("/api/vehiculos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void login_DebeFallar_CuandoPasswordEsIncorrecto() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"correo": "admin@test.com", "password": "wrong"}
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegido_DebeFallar_SinToken() throws Exception {
        mockMvc.perform(get("/api/vehiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
