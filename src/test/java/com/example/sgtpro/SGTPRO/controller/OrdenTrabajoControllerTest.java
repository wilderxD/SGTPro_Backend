package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.service.OrdenTrabajoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdenTrabajoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrdenTrabajoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdenTrabajoService ordenService;

    @MockBean
    private com.example.sgtpro.SGTPRO.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    private OrdenTrabajoDTO ordenDTOPrueba;

    @BeforeEach
    void setUp() {
        ordenDTOPrueba = OrdenTrabajoDTO.builder()
                .idOt(1)
                .idJefeTaller(10)
                .idMecanico(20)
                .placaVehiculo("ABC-123")
                .estado("EN_REVISION")
                .costoTotal(BigDecimal.ZERO)
                .build();
    }

    @Test
    void listarOrdenes_DebeRetornarStatus200_YListaJSON() throws Exception {
        when(ordenService.listarOrdenes(null, null)).thenReturn(List.of(ordenDTOPrueba));

        mockMvc.perform(get("/api/ordenes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].idOt").value(1));
    }

    @Test
    void listarOrdenes_ConFiltros_DebeRetornarFiltrado() throws Exception {
        when(ordenService.listarOrdenes("EN_REVISION", "ABC-123")).thenReturn(List.of(ordenDTOPrueba));

        mockMvc.perform(get("/api/ordenes")
                .param("estado", "EN_REVISION")
                .param("placa", "ABC-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void buscarPorId_DebeRetornarStatus200_YOTJSON() throws Exception {
        when(ordenService.buscarPorId(1)).thenReturn(ordenDTOPrueba);

        mockMvc.perform(get("/api/ordenes/{idOt}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOt").value(1))
                .andExpect(jsonPath("$.estado").value("EN_REVISION"));
    }

    @Test
    void crearOrden_DebeRetornarStatus201_YOTJSON() throws Exception {
        when(ordenService.crearOrden(any(OrdenTrabajoDTO.class))).thenReturn(ordenDTOPrueba);

        mockMvc.perform(post("/api/ordenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ordenDTOPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placaVehiculo").value("ABC-123"));
    }

    @Test
    void solicitarInsumo_DebeRetornarStatus200() throws Exception {
        RequerimientoInsumoDTO solicitud = RequerimientoInsumoDTO.builder()
                .idInsumo(100)
                .cantidadSolicitada(new BigDecimal("2"))
                .build();

        when(ordenService.solicitarInsumo(eq(1), any(RequerimientoInsumoDTO.class))).thenReturn(ordenDTOPrueba);

        mockMvc.perform(post("/api/ordenes/{idOt}/requerimientos", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOt").value(1));
    }

    @Test
    void despacharInsumo_DebeRetornarStatus200() throws Exception {
        when(ordenService.despacharInsumo(eq(500), any(BigDecimal.class))).thenReturn(ordenDTOPrueba);

        mockMvc.perform(patch("/api/ordenes/requerimientos/{idRequerimiento}/despachar", 500)
                .param("cantidadEntregada", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void finalizarOrden_DebeRetornarStatus200() throws Exception {
        OrdenTrabajoDTO finalizada = OrdenTrabajoDTO.builder()
                .idOt(1)
                .estado("FINALIZADO")
                .build();

        when(ordenService.finalizarOrden(1)).thenReturn(finalizada);

        mockMvc.perform(patch("/api/ordenes/{idOt}/finalizar", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));
    }

    @Test
    void cancelarOrden_DebeRetornarStatus200() throws Exception {
        OrdenTrabajoDTO cancelada = OrdenTrabajoDTO.builder()
                .idOt(1)
                .estado("CANCELADO")
                .build();

        when(ordenService.cancelarOrden(1)).thenReturn(cancelada);

        mockMvc.perform(patch("/api/ordenes/{idOt}/cancelar", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        verify(ordenService).cancelarOrden(1);
    }
}
