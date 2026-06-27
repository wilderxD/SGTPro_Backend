package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.AsignarOtRequest;
import com.example.sgtpro.SGTPRO.dto.CategoriaReporteDTO;
import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.ReporteRequest;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.dto.TrabajoOtDTO;
import com.example.sgtpro.SGTPRO.entity.CategoriaReporte;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import com.example.sgtpro.SGTPRO.entity.TrabajoOt;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.OrdenTrabajoMapper;
import com.example.sgtpro.SGTPRO.repository.CategoriaReporteRepository;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.RequerimientoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.TrabajoOtRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import com.example.sgtpro.SGTPRO.service.NotificacionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrdenTrabajoService implements IOrdenTrabajoService{

    private final OrdenTrabajoRepository ordenRepository;
    private final CatalogoInsumoRepository insumoRepository;
    private final RequerimientoInsumoRepository requerimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoMapper ordenMapper;
    private final CategoriaReporteRepository categoriaRepository;
    private final TrabajoOtRepository trabajoRepository;
    private final NotificacionService notificacionService;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenRepository,
            CatalogoInsumoRepository insumoRepository,
            RequerimientoInsumoRepository requerimientoRepository,
            UsuarioRepository usuarioRepository, VehiculoRepository vehiculoRepository,
            OrdenTrabajoMapper ordenMapper,
            CategoriaReporteRepository categoriaRepository,
            TrabajoOtRepository trabajoRepository,
            NotificacionService notificacionService) {
        this.ordenRepository = ordenRepository;
        this.insumoRepository = insumoRepository;
        this.requerimientoRepository = requerimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenMapper = ordenMapper;
        this.categoriaRepository = categoriaRepository;
        this.trabajoRepository = trabajoRepository;
        this.notificacionService = notificacionService;
    }

    // ─── CATEGORÍAS ───────────────────────────────────────────────────────────┐

    @Transactional(readOnly = true)
    public List<CategoriaReporteDTO> listarCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(c -> CategoriaReporteDTO.builder()
                        .idCategoria(c.getIdCategoria())
                        .nombre(c.getNombre())
                        .descripcion(c.getDescripcion())
                        .build())
                .toList();
    }

    // ─── REPORTE (JEFE_DIRECTO) ───────────────────────────────────────────────┘

    @Transactional
    public OrdenTrabajoDTO crearReporte(ReporteRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(request.getPlaca())
                .orElseThrow(() -> new BadRequestException("El vehículo con placa " + request.getPlaca() + " no existe."));

        List<CategoriaReporte> categorias = categoriaRepository.findAllById(request.getIdCategorias());
        if (categorias.isEmpty()) {
            throw new BadRequestException("No se encontraron las categorías seleccionadas.");
        }

        OrdenTrabajo ot = new OrdenTrabajo();
        ot.setVehiculo(vehiculo);
        ot.setFechaInternamiento(LocalDateTime.now());
        ot.setEstado(OrdenTrabajo.ESTADO_EN_REVISION);
        ot.setCostoTotal(BigDecimal.ZERO);
        ot.setDiagnosticoMecanico(request.getObservaciones());

        List<TrabajoOt> trabajos = categorias.stream().map(cat -> {
            TrabajoOt t = new TrabajoOt();
            t.setOrdenTrabajo(ot);
            t.setDescripcion(cat.getNombre());
            t.setCompletado(false);
            return t;
        }).toList();
        ot.setTrabajos(trabajos);

        OrdenTrabajoDTO dto = ordenMapper.toDTO(ordenRepository.save(ot));
        notificacionService.crearParaRol(
                "Nueva OT #" + dto.getIdOt() + " reportada para " + request.getPlaca(),
                "info", "/ordenes/" + dto.getIdOt(),
                "ROLE_JEFE_TALLER");
        return dto;
    }

    // ─── ASIGNAR (JEFE_TALLER) ────────────────────────────────────────────────┘

    @Transactional
    public OrdenTrabajoDTO asignarOt(Integer idOt, AsignarOtRequest request) {
        OrdenTrabajo ot = ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada."));

        if (ot.getJefeTaller() != null) {
            throw new BadRequestException("La OT ya tiene un jefe de taller asignado.");
        }

        Usuario jefeTaller = usuarioRepository.findById(request.getIdJefeTaller())
                .orElseThrow(() -> new BadRequestException("El jefe de taller no existe."));
        Usuario mecanico = usuarioRepository.findById(request.getIdMecanico())
                .orElseThrow(() -> new BadRequestException("El mecánico no existe."));

        ot.setJefeTaller(jefeTaller);
        ot.setMecanico(mecanico);
        ot.setDiagnosticoMecanico(request.getDiagnosticoMecanico());
        if (request.getKilometraje() != null && request.getKilometraje() > 0) {
            ot.setKilometraje(request.getKilometraje());
            Vehiculo v = ot.getVehiculo();
            v.setKilometrajeActual(request.getKilometraje());
            vehiculoRepository.save(v);
        }
        ot.setEstado(OrdenTrabajo.ESTADO_EN_REPARACION);

        OrdenTrabajoDTO dto = ordenMapper.toDTO(ordenRepository.save(ot));
        notificacionService.crearParaUsuario(
                "Se te ha asignado la OT #" + dto.getIdOt() + " - " + dto.getPlacaVehiculo(),
                "info", "/ordenes/" + dto.getIdOt(),
                mecanico.getIdUsuario());
        return dto;
    }

    // ─── COMPLETAR TRABAJO (MECANICO) ─────────────────────────────────────────┘

    @Transactional
    public TrabajoOtDTO completarTrabajo(Integer idTrabajo, String observaciones) {
        TrabajoOt trabajo = trabajoRepository.findById(idTrabajo)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado."));

        OrdenTrabajo ot = trabajo.getOrdenTrabajo();
        if (!OrdenTrabajo.ESTADO_EN_REPARACION.equals(ot.getEstado())) {
            throw new BadRequestException("Solo se pueden completar trabajos en OT en estado " + OrdenTrabajo.ESTADO_EN_REPARACION);
        }

        trabajo.setCompletado(true);
        trabajo.setObservaciones(observaciones);
        trabajoRepository.save(trabajo);

        notificacionService.crearParaRol(
                "Trabajo completado en OT #" + ot.getIdOt() + ": " + trabajo.getDescripcion(),
                "success", "/ordenes/" + ot.getIdOt(),
                "ROLE_JEFE_TALLER");
        notificacionService.crearParaRol(
                "Trabajo completado en OT #" + ot.getIdOt() + ": " + trabajo.getDescripcion(),
                "success", "/ordenes/" + ot.getIdOt(),
                "ROLE_JEFE_DIRECTO");

        return TrabajoOtDTO.builder()
                .idTrabajo(trabajo.getIdTrabajo())
                .descripcion(trabajo.getDescripcion())
                .completado(true)
                .observaciones(observaciones)
                .build();
    }

    // ─── CREAR OT (JEFE_TALLER) ───────────────────────────────────────────────┘

    @Transactional
    @Override
    public OrdenTrabajoDTO crearOrden(OrdenTrabajoDTO dto) {
        Usuario jefeTaller = usuarioRepository.findById(dto.getIdJefeTaller())
                .orElseThrow(() -> new BadRequestException("El jefe de taller especificado no existe."));
        Usuario mecanico = usuarioRepository.findById(dto.getIdMecanico())
                .orElseThrow(() -> new BadRequestException("El mecanico especificado no existe."));
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(dto.getPlacaVehiculo())
                .orElseThrow(() -> new BadRequestException("El vehiculo con placa " + dto.getPlacaVehiculo() + " no existe."));

        OrdenTrabajo ot = new OrdenTrabajo();
        ot.setJefeTaller(jefeTaller);
        ot.setMecanico(mecanico);
        ot.setVehiculo(vehiculo);
        ot.setFechaInternamiento(LocalDateTime.now());
        ot.setEstado(OrdenTrabajo.ESTADO_EN_REVISION);
        ot.setCostoTotal(BigDecimal.ZERO);
        Integer km = dto.getKilometraje();
        if (km != null) {
            ot.setKilometraje(km);
            if (km > 0) {
                vehiculo.setKilometrajeActual(km);
                vehiculoRepository.save(vehiculo);
            }
        }

        if (dto.getTrabajos() != null && !dto.getTrabajos().isEmpty()) {
            List<TrabajoOt> trabajos = dto.getTrabajos().stream().map(t -> {
                TrabajoOt trabajo = new TrabajoOt();
                trabajo.setOrdenTrabajo(ot);
                trabajo.setDescripcion(t.getDescripcion());
                trabajo.setCompletado(false);
                return trabajo;
            }).toList();
            ot.setTrabajos(trabajos);
        }

        OrdenTrabajoDTO otDto = ordenMapper.toDTO(ordenRepository.save(ot));
        notificacionService.crearParaUsuario(
                "Se te ha asignado la OT #" + otDto.getIdOt() + " - " + otDto.getPlacaVehiculo(),
                "info", "/ordenes/" + otDto.getIdOt(),
                mecanico.getIdUsuario());
        notificacionService.crearParaRol(
                "Nueva OT #" + otDto.getIdOt() + " creada para " + otDto.getPlacaVehiculo(),
                "info", "/ordenes/" + otDto.getIdOt(),
                "ROLE_JEFE_DIRECTO");
        return otDto;
    }

    // ─── SOLICITAR INSUMO ─────────────────────────────────────────────────────┘

    @Transactional
    @Override
    public OrdenTrabajoDTO solicitarInsumo(Integer idOt, RequerimientoInsumoDTO solicitud) {
        OrdenTrabajo ot = ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada."));

        if (!OrdenTrabajo.ESTADO_EN_REVISION.equals(ot.getEstado())
                && !OrdenTrabajo.ESTADO_EN_REPARACION.equals(ot.getEstado())) {
            throw new BadRequestException("No se pueden solicitar insumos para una OT en estado " + ot.getEstado());
        }

        CatalogoInsumo insumo = insumoRepository.findById(solicitud.getIdInsumo())
                .orElseThrow(() -> new ResourceNotFoundException("El insumo solicitado no existe en el catálogo"));

        Usuario solicitante = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        RequerimientoInsumo requerimiento = new RequerimientoInsumo();
        requerimiento.setOrdenTrabajo(ot);
        requerimiento.setInsumo(insumo);
        requerimiento.setSolicitadoPor(solicitante);
        requerimiento.setCantidadSolicitada(solicitud.getCantidadSolicitada());
        requerimiento.setCantidadEntregada(BigDecimal.ZERO);
        requerimiento.setSubtotal(BigDecimal.ZERO);

        requerimientoRepository.save(requerimiento);

        notificacionService.crearParaRol(
                "Solicitud de insumo: " + insumo.getNombre() + " x" + solicitud.getCantidadSolicitada()
                        + " para OT #" + idOt,
                "warning", "/ordenes/" + idOt,
                "ROLE_LOGISTICA");

        return ordenMapper.toDTO(ordenRepository.findById(idOt).get());
    }

    // ─── DESPACHAR INSUMO ─────────────────────────────────────────────────────┘

    @Override
    @Transactional
    public OrdenTrabajoDTO despacharInsumo(Integer idRequerimiento, BigDecimal cantidadEntregada) {
        RequerimientoInsumo req = requerimientoRepository.findById(idRequerimiento)
                .orElseThrow(() -> new ResourceNotFoundException("Requerimiento no encontrado."));

        OrdenTrabajo ot = req.getOrdenTrabajo();
        CatalogoInsumo insumo = req.getInsumo();

        if (!OrdenTrabajo.ESTADO_EN_REVISION.equals(ot.getEstado())
                && !OrdenTrabajo.ESTADO_EN_REPARACION.equals(ot.getEstado())) {
            throw new BadRequestException("No se puede despachar insumos: la OT debe estar en estado "
                    + OrdenTrabajo.ESTADO_EN_REVISION + " o " + OrdenTrabajo.ESTADO_EN_REPARACION);
        }

        if (insumo.getStock().compareTo(cantidadEntregada) < 0) {
            throw new BadRequestException("Stock insuficiente para '" + insumo.getNombre()
                    + "'. Disponible: " + insumo.getStock() + ", solicitado: " + cantidadEntregada);
        }

        req.setCantidadEntregada(cantidadEntregada);

        BigDecimal subtotal = cantidadEntregada.multiply(insumo.getCostoUnitario());
        req.setSubtotal(subtotal);

        insumo.setStock(insumo.getStock().subtract(cantidadEntregada));
        insumoRepository.save(insumo);

        BigDecimal nuevoCostoTotal = ot.getCostoTotal().add(subtotal);
        ot.setCostoTotal(nuevoCostoTotal);
        if (OrdenTrabajo.ESTADO_EN_REVISION.equals(ot.getEstado())) {
            ot.setEstado(OrdenTrabajo.ESTADO_EN_REPARACION);
        }

        requerimientoRepository.save(req);
        ordenRepository.save(ot);

        return ordenMapper.toDTO(ot);
    }

    // ─── LISTAR ───────────────────────────────────────────────────────────────┘

    @Override
    @Transactional(readOnly = true)
    public List<OrdenTrabajoDTO> listarOrdenes(String estado, String placa) {
        List<OrdenTrabajo> ordenes;

        if (estado != null && placa != null) {
            ordenes = ordenRepository.findByEstadoAndVehiculoPlaca(estado, placa);
        } else if (estado != null) {
            ordenes = ordenRepository.findByEstado(estado);
        } else if (placa != null) {
            ordenes = ordenRepository.findByVehiculoPlaca(placa);
        } else {
            ordenes = ordenRepository.findAll();
        }

        return ordenes.stream()
                .map(ordenMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenTrabajoDTO> listarOrdenesPaginado(
            String estado, String placa, Integer idMecanico,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            Pageable pageable) {
        return ordenRepository.findFiltered(estado, placa, idMecanico,
                        fechaDesde, fechaHasta, pageable)
                .map(ordenMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenTrabajoDTO buscarPorId(Integer idOt) {
        return ordenMapper.toDTO(ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id: " + idOt)));
    }

    // ─── TRANSICIONES ─────────────────────────────────────────────────────────┘

    @Override
    @Transactional
    public OrdenTrabajoDTO finalizarOrden(Integer idOt) {
        OrdenTrabajo ot = ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id: " + idOt));

        if (!OrdenTrabajo.ESTADO_EN_REPARACION.equals(ot.getEstado())) {
            throw new BadRequestException("Solo se pueden finalizar OT en estado " + OrdenTrabajo.ESTADO_EN_REPARACION);
        }

        ot.setEstado(OrdenTrabajo.ESTADO_FINALIZADO);
        ot.setFechaSalida(LocalDateTime.now());
        return ordenMapper.toDTO(ordenRepository.save(ot));
    }

    @Override
    @Transactional
    public OrdenTrabajoDTO cancelarOrden(Integer idOt) {
        OrdenTrabajo ot = ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id: " + idOt));

        if (OrdenTrabajo.ESTADO_FINALIZADO.equals(ot.getEstado()) || OrdenTrabajo.ESTADO_ENTREGADO.equals(ot.getEstado())) {
            throw new BadRequestException("No se puede cancelar una OT que ya ha sido finalizada o entregada.");
        }

        ot.setEstado(OrdenTrabajo.ESTADO_CANCELADO);
        return ordenMapper.toDTO(ordenRepository.save(ot));
    }

    @Override
    @Transactional
    public OrdenTrabajoDTO entregarOrden(Integer idOt) {
        OrdenTrabajo ot = ordenRepository.findById(idOt)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada con id: " + idOt));

        if (!OrdenTrabajo.ESTADO_FINALIZADO.equals(ot.getEstado())) {
            throw new BadRequestException("Solo se pueden entregar OT en estado " + OrdenTrabajo.ESTADO_FINALIZADO);
        }

        if (ot.getFechaSalida() == null) {
            ot.setFechaSalida(LocalDateTime.now());
        }

        ot.setEstado(OrdenTrabajo.ESTADO_ENTREGADO);
        return ordenMapper.toDTO(ordenRepository.save(ot));
    }
}
