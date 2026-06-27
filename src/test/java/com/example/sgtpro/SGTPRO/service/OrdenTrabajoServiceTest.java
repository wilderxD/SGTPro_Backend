package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.OrdenTrabajoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.RequerimientoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class OrdenTrabajoServiceTest {

    @Mock
    private OrdenTrabajoRepository ordenRepository;
    @Mock
    private CatalogoInsumoRepository insumoRepository;
    @Mock
    private RequerimientoInsumoRepository requerimientoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VehiculoRepository vehiculoRepository;
    @Mock
    private OrdenTrabajoMapper ordenMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrdenTrabajoService ordenTrabajoService;

    private Usuario jefeTaller;
    private Usuario mecanico;
    private Usuario solicitante;
    private Vehiculo vehiculo;
    private OrdenTrabajo ordenPrueba;
    private OrdenTrabajoDTO ordenDTOPrueba;
    private CatalogoInsumo insumoPrueba;
    private RequerimientoInsumo requerimientoPrueba;
    private RequerimientoInsumoDTO solicitudDTO;

    @BeforeEach
    void setUp() {
        jefeTaller = new Usuario();
        jefeTaller.setIdUsuario(10);
        jefeTaller.setNombreCompleto("Jefe Taller");

        mecanico = new Usuario();
        mecanico.setIdUsuario(20);
        mecanico.setNombreCompleto("Mecanico");

        solicitante = new Usuario();
        solicitante.setIdUsuario(30);
        solicitante.setNombreCompleto("Solicitante");

        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Volvo");

        ordenPrueba = new OrdenTrabajo();
        ordenPrueba.setIdOt(1);
        ordenPrueba.setJefeTaller(jefeTaller);
        ordenPrueba.setMecanico(mecanico);
        ordenPrueba.setVehiculo(vehiculo);
        ordenPrueba.setEstado(OrdenTrabajo.ESTADO_EN_REVISION);
        ordenPrueba.setCostoTotal(BigDecimal.ZERO);
        ordenPrueba.setFechaInternamiento(LocalDateTime.now());

        ordenDTOPrueba = OrdenTrabajoDTO.builder()
                .idOt(1)
                .idJefeTaller(10)
                .idMecanico(20)
                .placaVehiculo("ABC-123")
                .estado(OrdenTrabajo.ESTADO_EN_REVISION)
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
        requerimientoPrueba.setSolicitadoPor(solicitante);
        requerimientoPrueba.setCantidadSolicitada(new BigDecimal("2"));
        requerimientoPrueba.setCantidadEntregada(BigDecimal.ZERO);
        requerimientoPrueba.setSubtotal(BigDecimal.ZERO);
    }

    @Test
    void crearOrden_DebeInicializarOrdenConEstadoYCostoCero() {
        when(usuarioRepository.findById(10)).thenReturn(Optional.of(jefeTaller));
        when(usuarioRepository.findById(20)).thenReturn(Optional.of(mecanico));
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(Optional.of(vehiculo));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        OrdenTrabajoDTO resultado = ordenTrabajoService.crearOrden(ordenDTOPrueba);

        assertNotNull(resultado);
        assertEquals(OrdenTrabajo.ESTADO_EN_REVISION, resultado.getEstado());
        assertEquals(BigDecimal.ZERO, resultado.getCostoTotal());
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }

    @Test
    void crearOrden_DebeLanzarBadRequest_CuandoJefeTallerNoExiste() {
        when(usuarioRepository.findById(10)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenTrabajoService.crearOrden(ordenDTOPrueba));

        assertEquals("El jefe de taller especificado no existe.", ex.getMessage());
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void crearOrden_DebeLanzarBadRequest_CuandoVehiculoNoExiste() {
        when(usuarioRepository.findById(10)).thenReturn(Optional.of(jefeTaller));
        when(usuarioRepository.findById(20)).thenReturn(Optional.of(mecanico));
        when(vehiculoRepository.findByPlaca("ABC-123")).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenTrabajoService.crearOrden(ordenDTOPrueba));

        assertEquals("El vehiculo con placa ABC-123 no existe.", ex.getMessage());
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void solicitarInsumo_DebeCrearRequerimiento_CuandoOrdenEInsumoExisten() {
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));
        when(insumoRepository.findById(100)).thenReturn(Optional.of(insumoPrueba));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(solicitante);
        SecurityContextHolder.setContext(securityContext);

        when(requerimientoRepository.save(any(RequerimientoInsumo.class))).thenReturn(requerimientoPrueba);
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        OrdenTrabajoDTO resultado = ordenTrabajoService.solicitarInsumo(1, solicitudDTO);

        assertNotNull(resultado);
        verify(requerimientoRepository, times(1)).save(any(RequerimientoInsumo.class));
    }

    @Test
    void solicitarInsumo_DebeLanzarResourceNotFound_CuandoOrdenNoExiste() {
        when(ordenRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class,
                () -> ordenTrabajoService.solicitarInsumo(99, solicitudDTO));

        assertEquals("Orden de trabajo no encontrada.", excepcion.getMessage());
        verify(requerimientoRepository, never()).save(any(RequerimientoInsumo.class));
    }

    @Test
    void despacharInsumo_DebeCalcularSubtotalYActualizarOrden() {
        BigDecimal cantidadAEntregar = new BigDecimal("2");
        when(requerimientoRepository.findById(500)).thenReturn(Optional.of(requerimientoPrueba));

        OrdenTrabajoDTO ordenActualizadaDTO = OrdenTrabajoDTO.builder()
                .estado(OrdenTrabajo.ESTADO_EN_REPARACION)
                .costoTotal(new BigDecimal("100.00"))
                .build();

        when(requerimientoRepository.save(any(RequerimientoInsumo.class))).thenReturn(requerimientoPrueba);
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenActualizadaDTO);

        OrdenTrabajoDTO resultado = ordenTrabajoService.despacharInsumo(500, cantidadAEntregar);

        assertNotNull(resultado);
        assertEquals(OrdenTrabajo.ESTADO_EN_REPARACION, resultado.getEstado());
        assertTrue(resultado.getCostoTotal().compareTo(new BigDecimal("100.00")) == 0,
                "El costo total debio subir a 100.00");
        verify(requerimientoRepository, times(1)).save(any(RequerimientoInsumo.class));
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }

    @Test
    void despacharInsumo_DebeLanzarResourceNotFound_CuandoRequerimientoNoExiste() {
        when(requerimientoRepository.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class,
                () -> ordenTrabajoService.despacharInsumo(999, new BigDecimal("2")));

        assertEquals("Requerimiento no encontrado.", excepcion.getMessage());
        verify(ordenRepository, never()).save(any(OrdenTrabajo.class));
    }

    @Test
    void despacharInsumo_DebeLanzarBadRequest_CuandoOTNoEstaEnRevision() {
        ordenPrueba.setEstado(OrdenTrabajo.ESTADO_FINALIZADO);
        when(requerimientoRepository.findById(500)).thenReturn(Optional.of(requerimientoPrueba));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenTrabajoService.despacharInsumo(500, new BigDecimal("2")));

        assertTrue(ex.getMessage().contains(OrdenTrabajo.ESTADO_EN_REVISION));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void listarOrdenes_SinFiltros_DebeRetornarTodas() {
        when(ordenRepository.findAll()).thenReturn(List.of(ordenPrueba));
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        List<OrdenTrabajoDTO> resultado = ordenTrabajoService.listarOrdenes(null, null);

        assertEquals(1, resultado.size());
        verify(ordenRepository, times(1)).findAll();
    }

    @Test
    void listarOrdenes_ConFiltroEstado_DebeUsarFindByEstado() {
        when(ordenRepository.findByEstado(OrdenTrabajo.ESTADO_EN_REVISION)).thenReturn(List.of(ordenPrueba));
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(ordenDTOPrueba);

        List<OrdenTrabajoDTO> resultado = ordenTrabajoService.listarOrdenes(OrdenTrabajo.ESTADO_EN_REVISION, null);

        assertEquals(1, resultado.size());
        verify(ordenRepository, times(1)).findByEstado(OrdenTrabajo.ESTADO_EN_REVISION);
    }

    @Test
    void finalizarOrden_DebeCambiarEstadoAFinalizado() {
        ordenPrueba.setEstado(OrdenTrabajo.ESTADO_EN_REPARACION);
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));

        OrdenTrabajoDTO dtoFinal = OrdenTrabajoDTO.builder()
                .estado(OrdenTrabajo.ESTADO_FINALIZADO)
                .build();
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(dtoFinal);

        OrdenTrabajoDTO resultado = ordenTrabajoService.finalizarOrden(1);

        assertEquals(OrdenTrabajo.ESTADO_FINALIZADO, resultado.getEstado());
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }

    @Test
    void finalizarOrden_DebeLanzarBadRequest_CuandoNoEstaEnReparacion() {
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenTrabajoService.finalizarOrden(1));

        assertTrue(ex.getMessage().contains(OrdenTrabajo.ESTADO_EN_REPARACION));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void cancelarOrden_DebeCambiarEstadoACancelado() {
        when(ordenRepository.findById(1)).thenReturn(Optional.of(ordenPrueba));

        OrdenTrabajoDTO dtoCancelado = OrdenTrabajoDTO.builder()
                .estado(OrdenTrabajo.ESTADO_CANCELADO)
                .build();
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenReturn(ordenPrueba);
        when(ordenMapper.toDTO(any(OrdenTrabajo.class))).thenReturn(dtoCancelado);

        OrdenTrabajoDTO resultado = ordenTrabajoService.cancelarOrden(1);

        assertEquals(OrdenTrabajo.ESTADO_CANCELADO, resultado.getEstado());
        verify(ordenRepository, times(1)).save(any(OrdenTrabajo.class));
    }
}
