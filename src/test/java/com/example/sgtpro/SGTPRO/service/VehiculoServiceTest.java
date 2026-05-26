package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.VehiculoMapper;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class VehiculoServiceTest {
    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private VehiculoMapper vehiculoMapper;

    @InjectMocks
    private VehiculoService vehiculoService; 

    private Vehiculo vehiculoPrueba;
    private VehiculoDTO vehiculoDTOPrueba;
    
    @BeforeEach
    void setUp() {
        vehiculoPrueba = new Vehiculo();
        vehiculoPrueba.setPlaca("ABC-123");
        vehiculoPrueba.setMarca("Foton");
        vehiculoPrueba.setModelo("Aumark");
        vehiculoPrueba.setKilometrajeActual(45000);

        vehiculoDTOPrueba = VehiculoDTO.builder()
                .placa("ABC-123")
                .marca("Foton")
                .modelo("Aumark")
                .kilometrajeActual(45000)
                .build();
    }
                    
    @Test
    void obtenerTodos_DebeRetornarListaDeVehiculos() {
        when(vehiculoRepository.findAll()).thenReturn(List.of(vehiculoPrueba));
        when(vehiculoMapper.toDTO(any(Vehiculo.class))).thenReturn(vehiculoDTOPrueba);

        List<VehiculoDTO> resultado = vehiculoService.obtenerTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(vehiculoRepository, times(1)).findAll();
    }

    @Test
    void ObtenerTodosPaginado_DebeRetornarPaginaDeVehiculos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> paginaVehiculos = new PageImpl<>(List.of(vehiculoPrueba));
        
        when(vehiculoRepository.findAll(pageable)).thenReturn(paginaVehiculos);
        when(vehiculoMapper.toDTO(any(Vehiculo.class))).thenReturn(vehiculoDTOPrueba);

        Page<VehiculoDTO> resultado = vehiculoService.ObtenerTodosPaginado(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(vehiculoRepository, times(1)).findAll(pageable);
    }
    
    @Test
    void guardar_DebeRetornarVehiculoDTO_CuandoEsExitoso() {
        when(vehiculoRepository.existsById("ABC-123")).thenReturn(false);
        when(vehiculoMapper.toEntity(vehiculoDTOPrueba)).thenReturn(vehiculoPrueba);
        when(vehiculoRepository.save(vehiculoPrueba)).thenReturn(vehiculoPrueba);
        when(vehiculoMapper.toDTO(vehiculoPrueba)).thenReturn(vehiculoDTOPrueba);

        VehiculoDTO resultado = vehiculoService.guardar(vehiculoDTOPrueba);

        assertNotNull(resultado);
        assertEquals("ABC-123", resultado.getPlaca());
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void guardar_DebeLanzarBadRequestException_CuandoPlacaYaExiste() {
        when(vehiculoRepository.existsById("ABC-123")).thenReturn(true);

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            vehiculoService.guardar(vehiculoDTOPrueba);
        });

        assertEquals("Ya existe un vehiculo registrado con la placa: ABC-123", excepcion.getMessage());
        verify(vehiculoRepository, never()).save(any(Vehiculo.class));
    }

    @Test
    void obtenerPorPlaca_DebeRetornarVehiculoDTO_CuandoExiste() {
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(Optional.of(vehiculoPrueba));
        when(vehiculoMapper.toDTO(vehiculoPrueba)).thenReturn(vehiculoDTOPrueba);

        VehiculoDTO resultado = vehiculoService.obtenerPorPlaca("ABC-123");

        assertNotNull(resultado);
        assertEquals("ABC-123", resultado.getPlaca());
        verify(vehiculoRepository, times(1)).findByPlaca("ABC-123");
    }

    @Test
    void obtenerPorPlaca_DebeLanzarResourceNotFoundException_CuandoNoExiste() {
        when(vehiculoRepository.findByPlaca("XYZ-999")).thenReturn(Optional.empty());

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            vehiculoService.obtenerPorPlaca("XYZ-999");
        });

        assertEquals("Vehículo no encontrado con placa: XYZ-999", excepcion.getMessage());
    }

    @Test
    void actualizar_DebeModificarYRetornarVehiculo_CuandoExiste() {
    
        VehiculoDTO datosNuevos = VehiculoDTO.builder().kilometrajeActual(50000).build();
        
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(Optional.of(vehiculoPrueba));
        
        when(vehiculoMapper.toDTO(any(Vehiculo.class)))
                .thenReturn(vehiculoDTOPrueba) 
                .thenReturn(datosNuevos);      
        
        when(vehiculoMapper.toEntity(any(VehiculoDTO.class))).thenReturn(vehiculoPrueba);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculoPrueba);

        VehiculoDTO resultado = vehiculoService.actualizar("ABC-123", datosNuevos);

        assertNotNull(resultado);
        assertEquals(50000, resultado.getKilometrajeActual(), "El kilometraje debió actualizarse a 50000");
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void eliminarPorPlaca_DebeLlamarAlRepositorio_CuandoExiste() {
        when(vehiculoRepository.existsById("ABC-123")).thenReturn(true);
        doNothing().when(vehiculoRepository).deleteByPlaca("ABC-123");

        vehiculoService.eliminarPorPlaca("ABC-123");

        verify(vehiculoRepository, times(1)).deleteByPlaca("ABC-123");
    }

    @Test
    void eliminarPorPlaca_DebeLanzarRuntimeException_CuandoNoExiste() {
        when(vehiculoRepository.existsById("XYZ-999")).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            vehiculoService.eliminarPorPlaca("XYZ-999");
        });

        assertEquals("No se puede eliminar: El vehículo con placa XYZ-999 no existe.", excepcion.getMessage());
        verify(vehiculoRepository, never()).deleteByPlaca(anyString());
    }
    
}
