package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.service.IVehiculoService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper; 

    @MockBean
    private IVehiculoService vehiculoService;
    
    @MockBean
    private com.example.sgtpro.SGTPRO.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;
   
    private VehiculoDTO vehiculoDTOPrueba;

    @BeforeEach
    void setUp() {
        
        vehiculoDTOPrueba = VehiculoDTO.builder()
                .placa("BAZ-911")
                .marca("Volvo")
                .modelo("FH16")
                .kilometrajeActual(120500)
                .build();
    }

    @Test
    void listarVehiculos_DebeRetornarStatus200_YListaJSON() throws Exception {
        when(vehiculoService.obtenerTodos()).thenReturn(List.of(vehiculoDTOPrueba));

        mockMvc.perform(get("/api/vehiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.size()").value(1)) 
                .andExpect(jsonPath("$[0].placa").value("BAZ-911")) 
                .andExpect(jsonPath("$[0].marca").value("Volvo"));

        verify(vehiculoService).obtenerTodos();
    }
   
    @Test
    void listarPaginado_DebeRetornarStatus200_YPaginaJSON() throws Exception {
        Pageable miOrden = PageRequest.of(0, 8);
        Page<VehiculoDTO> paginaVehiculos = new PageImpl<>(List.of(vehiculoDTOPrueba), miOrden, 1);

        when(vehiculoService.obtenerTodosPaginado(miOrden)).thenReturn(paginaVehiculos);

        mockMvc.perform(get("/api/vehiculos/paginado")
                .param("page", "0") 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].placa").value("BAZ-911"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(vehiculoService).obtenerTodosPaginado(miOrden);
    }
    
    @Test
    void guardarVehiculo_DebeRetornarStatus201_YVehiculoJSON() throws Exception {
        when(vehiculoService.guardar(any(VehiculoDTO.class))).thenReturn(vehiculoDTOPrueba);

        mockMvc.perform(post("/api/vehiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehiculoDTOPrueba))) 
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.placa").value("BAZ-911"))
                .andExpect(jsonPath("$.kilometrajeActual").value(120500));

        verify(vehiculoService).guardar(any(VehiculoDTO.class));
    }

    @Test
    void obtenerUnVehiculo_DebeRetornarStatus200_YVehiculoJSON() throws Exception {
        when(vehiculoService.obtenerPorPlaca("BAZ-911")).thenReturn(vehiculoDTOPrueba);

        mockMvc.perform(get("/api/vehiculos/{placa}", "BAZ-911") 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("BAZ-911"))
                .andExpect(jsonPath("$.modelo").value("FH16"));

        verify(vehiculoService).obtenerPorPlaca("BAZ-911");
    }

    @Test
    void actualizarVehiculo_DebeRetornarStatus200_YVehiculoActualizadoJSON() throws Exception {
       
        VehiculoDTO datosActualizados = VehiculoDTO.builder()
                .placa("BAZ-911")
                .marca("Volvo")
                .modelo("FH16")
                .kilometrajeActual(130000)
                .build();

        VehiculoDTO vehiculoFinalDTO = VehiculoDTO.builder()
                .placa("BAZ-911")
                .marca("Volvo")
                .modelo("FH16")
                .kilometrajeActual(130000)
                .build();
        
        when(vehiculoService.actualizar(eq("BAZ-911"), any(VehiculoDTO.class))).thenReturn(vehiculoFinalDTO);

        mockMvc.perform(put("/api/vehiculos/{placa}", "BAZ-911")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosActualizados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("BAZ-911"))
                .andExpect(jsonPath("$.kilometrajeActual").value(130000));

        verify(vehiculoService).actualizar(eq("BAZ-911"), any(VehiculoDTO.class));
    }

    @Test
    void eliminarVehiculo_DebeRetornarStatus204_NoContent() throws Exception {
        
        mockMvc.perform(delete("/api/vehiculos/{placa}", "BAZ-911")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); 

        verify(vehiculoService).eliminarPorPlaca("BAZ-911");
    }

}
