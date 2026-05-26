package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import com.example.sgtpro.SGTPRO.mapper.OrdenTrabajoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.RequerimientoInsumoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrdenTrabajoServiceTest {
    @Mock
    private OrdenTrabajoRepository ordenRepository;

    @Mock
    private CatalogoInsumoRepository insumoRepository;

    @Mock
    private RequerimientoInsumoRepository requerimientoRepository;

    @Mock
    private OrdenTrabajoMapper ordenMapper;

    @InjectMocks
    private OrdenTrabajoService ordenTrabajoService;

    private OrdenTrabajo ordenPrueba;
    private OrdenTrabajoDTO ordenDTOPrueba;
    private CatalogoInsumo insumoPrueba;
    private RequerimientoInsumo requerimientoPrueba;
    private RequerimientoInsumoDTO solicitudDTO;

    @BeforeEach
    void setUp() {
        
        ordenPrueba = new OrdenTrabajo();
        ordenPrueba.setIdOt(1);
        ordenPrueba.setIdJefeTaller(10);
        ordenPrueba.setIdMecanico(20);
        ordenPrueba.setEstado("EN_REVISION");
        ordenPrueba.setCostoTotal(BigDecimal.ZERO);
        ordenPrueba.setFechaInternamiento(LocalDateTime.now());

        ordenDTOPrueba = OrdenTrabajoDTO.builder()
                .idOt(1)
                .idJefeTaller(10)
                .idMecanico(20)
                .estado("EN_REVISION")
                .costoTotal(BigDecimal.ZERO)
                .build();
        
        insumoPrueba = new CatalogoInsumo();
        insumoPrueba.setIdInsumo(100);
        insumoPrueba.setCostoUnitario(new BigDecimal("50.00"));

        solicitudDTO = RequerimientoInsumoDTO.builder()
                .idInsumo(100)
                .cantidadSolicitada(new BigDecimal("2"))
                .build();        
        
        requerimientoPrueba = new RequerimientoInsumo();
        requerimientoPrueba.setIdRequerimiento(500);
        requerimientoPrueba.setOrdenTrabajo(ordenPrueba);
        requerimientoPrueba.setInsumo(insumoPrueba);
        requerimientoPrueba.setCantidadSolicitada(new BigDecimal("2"));
        requerimientoPrueba.setCantidadEntregada(BigDecimal.ZERO);
        requerimientoPrueba.setSubtotal(BigDecimal.ZERO);
    }

    @Test
    void crearOrden_DebeInicializarOrdenConEstadoYCostoCero() {
        // GIVEN
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        // WHEN
        OrdenTrabajoDTO resultado = ordenTrabajoService.crearOrden(ordenDTOPrueba);

        // THEN
        assertNotNull(resultado);
        assertEquals("EN_REVISION", resultado.getEstado());
        assertEquals(BigDecimal.ZERO, resultado.getCostoTotal());
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }

    @Test
    void solicitarInsumo_DebeCrearRequerimiento_CuandoOrdenEInsumoExisten() {
        
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));
        when(insumoRepository.findById(100)).thenReturn(Optional.of(insumoPrueba));
        
        when(requerimientoRepository.save(any(RequerimientoInsumo.class))).thenReturn(requerimientoPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        OrdenTrabajoDTO resultado = ordenTrabajoService.solicitarInsumo(1, solicitudDTO);

        assertNotNull(resultado);
        verify(requerimientoRepository, times(1)).save(any(RequerimientoInsumo.class));
        verify(ordenRepository, times(2)).findById(1); // Se llama al inicio y al final para refrescar
    }

    @Test
    void solicitarInsumo_DebeLanzarExcepcion_CuandoOrdenNoExiste() {
       
        when(ordenRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ordenTrabajoService.solicitarInsumo(99, solicitudDTO);
        });

        assertEquals("Orden de trabajo no encontrada.!", excepcion.getMessage());
        verify(requerimientoRepository, never()).save(any(RequerimientoInsumo.class));
    }

    @Test
    void despacharInsumo_DebeCalcularSubtotalYActualizarOrden() {
        
        BigDecimal cantidadAEntregar = new BigDecimal("2");
        when(requerimientoRepository.findById(500)).thenReturn(Optional.of(requerimientoPrueba));
       
        OrdenTrabajoDTO ordenActualizadaDTO = OrdenTrabajoDTO.builder()
                .estado("EN_REPARACION")
                .costoTotal(new BigDecimal("100.00"))
                .build();
                
        when(requerimientoRepository.save(any(RequerimientoInsumo.class))).thenReturn(requerimientoPrueba);
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenActualizadaDTO);

        OrdenTrabajoDTO resultado = ordenTrabajoService.despacharInsumo(500, cantidadAEntregar);

        assertNotNull(resultado);
        assertEquals("EN_REPARACION", resultado.getEstado());
 
        assertTrue(resultado.getCostoTotal().compareTo(new BigDecimal("100.00")) == 0, 
                   "El costo total debió subir a 100.00");

        verify(requerimientoRepository, times(1)).save(any(RequerimientoInsumo.class));
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }

    @Test
    void despacharInsumo_DebeLanzarExcepcion_CuandoRequerimientoNoExiste() {
     
        when(requerimientoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ordenTrabajoService.despacharInsumo(999, new BigDecimal("2"));
        });

        assertEquals("Requerimientono encontrado.!", excepcion.getMessage());
        verify(ordenRepository, never()).save(any(OrdenTrabajo.class));
    }
}
