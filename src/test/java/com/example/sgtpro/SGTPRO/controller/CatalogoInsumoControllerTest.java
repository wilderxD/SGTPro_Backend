package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import com.example.sgtpro.SGTPRO.service.ICatalogoInsumoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogoInsumoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CatalogoInsumoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICatalogoInsumoService insumoService;

    @MockBean
    private com.example.sgtpro.SGTPRO.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    private CatalogoInsumoDTO insumoDTOPrueba;

    @BeforeEach
    void setUp() {
        insumoDTOPrueba = CatalogoInsumoDTO.builder()
                .idInsumo(1)
                .codigoInterno("FIL-001")
                .nombre("Filtro de Aceite")
                .unidadMedida("Unidad")
                .costoUnitario(new BigDecimal("45.50"))
                .build();
    }

    @Test
    void listarCatalogoInsumos_DebeRetornarStatus200_YListaJSON() throws Exception {
        when(insumoService.listarCatalogoDeInsumos()).thenReturn(List.of(insumoDTOPrueba));

        mockMvc.perform(get("/api/catalogoInsumos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].codigoInterno").value("FIL-001"));
    }

    @Test
    void listarCatalogoInsumosPaginado_DebeRetornarStatus200_YPaginaJSON() throws Exception {
        Pageable miOrden = PageRequest.of(0, 8);
        Page<CatalogoInsumoDTO> pagina = new PageImpl<>(List.of(insumoDTOPrueba), miOrden, 1);

        when(insumoService.listarCatalogoInsumosPaginado(miOrden)).thenReturn(pagina);

        mockMvc.perform(get("/api/catalogoInsumos/paginado")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].codigoInterno").value("FIL-001"));
    }

    @Test
    void guardarInsumo_DebeRetornarStatus201_YInsumoJSON() throws Exception {
        when(insumoService.crearInsumo(any(CatalogoInsumoDTO.class))).thenReturn(insumoDTOPrueba);

        mockMvc.perform(post("/api/catalogoInsumos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(insumoDTOPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoInterno").value("FIL-001"));
    }

    @Test
    void buscarPorId_DebeRetornarStatus200_YInsumoJSON() throws Exception {
        when(insumoService.buscarPorId(1)).thenReturn(insumoDTOPrueba);

        mockMvc.perform(get("/api/catalogoInsumos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Filtro de Aceite"));
    }

    @Test
    void actualizarInsumo_DebeRetornarStatus200_YInsumoActualizadoJSON() throws Exception {
        CatalogoInsumoDTO datosActualizados = CatalogoInsumoDTO.builder()
                .costoUnitario(new BigDecimal("50.00"))
                .build();

        when(insumoService.actualizarInsumo(eq(1), any(CatalogoInsumoDTO.class))).thenReturn(insumoDTOPrueba);

        mockMvc.perform(put("/api/catalogoInsumos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosActualizados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoInterno").value("FIL-001"));
    }

    @Test
    void eliminarInsumo_DebeRetornarStatus204_NoContent() throws Exception {
        mockMvc.perform(delete("/api/catalogoInsumos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(insumoService).eliminarInsumo(1);
    }
}
