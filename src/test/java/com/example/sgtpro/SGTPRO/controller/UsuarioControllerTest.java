package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.RolDTO;
import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.entity.Rol;
import com.example.sgtpro.SGTPRO.service.IUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUsuarioService usuarioService;
    
    @MockBean
    private com.example.sgtpro.SGTPRO.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    private UsuarioDTO usuarioDTOPrueba;
    private Rol rolPrueba;

    @BeforeEach
    void setUp() {
        rolPrueba = new Rol();
        rolPrueba.setIdRol(1);
        rolPrueba.setNombre("ROLE_JEFE_TALLER");

        RolDTO rolDTO = new RolDTO(rolPrueba.getIdRol(), rolPrueba.getNombre(), rolPrueba.getDescripcion());
        usuarioDTOPrueba = UsuarioDTO.builder()
                .idUsuario(1)
                .nombreCompleto("Carlos Administrador")
                .correo("admin@forli.com.pe")
                .password("123456")
                .rol(rolDTO)
                .build();
    }

    @Test
    void listarUsuarios_DebeRetornarStatus200_YListaJSON() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioDTOPrueba));

        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombreCompleto").value("Carlos Administrador"))
                .andExpect(jsonPath("$[0].correo").value("admin@forli.com.pe"));

        verify(usuarioService).listarUsuarios();
    }

    @Test
    void listarPaginado_DebeRetornarStatus200_YPaginaJSON() throws Exception {
        Pageable miOrden = PageRequest.of(0, 8);
        Page<UsuarioDTO> paginaUsuarios = new PageImpl<>(List.of(usuarioDTOPrueba), miOrden, 1);

        when(usuarioService.listarUsuariosPaginado(miOrden)).thenReturn(paginaUsuarios);

        mockMvc.perform(get("/api/usuarios/paginado")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].nombreCompleto").value("Carlos Administrador"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(usuarioService).listarUsuariosPaginado(miOrden);
    }

    @Test
    void crearUsuario_DebeRetornarStatus201_YUsuarioJSON() throws Exception {
        when(usuarioService.guardarUsuario(any(UsuarioDTO.class))).thenReturn(usuarioDTOPrueba);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTOPrueba)))
                .andExpect(status().isCreated()) // Esperamos HTTP 201
                .andExpect(jsonPath("$.correo").value("admin@forli.com.pe"));

        verify(usuarioService).guardarUsuario(any(UsuarioDTO.class));
    }

    @Test
    void obtenerUnUsuario_DebeRetornarStatus200_YUsuarioJSON() throws Exception {
        when(usuarioService.buscarPorId(1)).thenReturn(usuarioDTOPrueba);

        mockMvc.perform(get("/api/usuarios/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Carlos Administrador"));

        verify(usuarioService).buscarPorId(1);
    }

    @Test
    void actualizarUsuario_DebeRetornarStatus200_YUsuarioActualizadoJSON() throws Exception {
        RolDTO rolDTO2 = new RolDTO(rolPrueba.getIdRol(), rolPrueba.getNombre(), rolPrueba.getDescripcion());
        UsuarioDTO datosActualizados = UsuarioDTO.builder()
                .nombreCompleto("Carlos Jefe Editado")
                .correo("admin@forli.com.pe")
                .rol(rolDTO2)
                .build();

        UsuarioDTO usuarioFinalDTO = UsuarioDTO.builder()
                .idUsuario(1)
                .nombreCompleto("Carlos Jefe Editado")
                .correo("admin@forli.com.pe")
                .rol(rolDTO2)
                .build();

        when(usuarioService.actualizarUsuario(eq(1), any(UsuarioDTO.class))).thenReturn(usuarioFinalDTO);

        mockMvc.perform(put("/api/usuarios/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosActualizados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Carlos Jefe Editado"));

        verify(usuarioService).actualizarUsuario(eq(1), any(UsuarioDTO.class));
    }

    @Test
    void eliminarUsuario_DebeRetornarStatus204_NoContent() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); 

        verify(usuarioService).eliminarPorID(1);
    }
}
