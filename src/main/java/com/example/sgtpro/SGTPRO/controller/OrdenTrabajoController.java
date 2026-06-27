package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.AsignarOtRequest;
import com.example.sgtpro.SGTPRO.dto.CategoriaReporteDTO;
import com.example.sgtpro.SGTPRO.dto.CompletarTrabajoRequest;
import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.ReporteRequest;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.dto.TrabajoOtDTO;
import com.example.sgtpro.SGTPRO.service.OrdenTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Maestro de Ordenes de trabajo", description = "Endpoints para la gestión de las Ordenes de Trabajo")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenService;
    private final int pageSize;

    public OrdenTrabajoController(OrdenTrabajoService ordenService, @Value("${app.page-size}") int pageSize){
        this.ordenService = ordenService;
        this.pageSize = pageSize;
    }

    // ─── CATEGORÍAS DE REPORTE ───────────────────────────────────────────────┐

    @Operation(summary = "Listar categorías de reporte", description = "Retorna las categorías predefinidas para reportar fallas.")
    @GetMapping("/categorias-reporte")
    public ResponseEntity<List<CategoriaReporteDTO>> listarCategorias() {
        return ResponseEntity.ok(ordenService.listarCategorias());
    }

    // ─── REPORTE (JEFE_DIRECTO) ───────────────────────────────────────────────┘

    @Operation(summary = "Reportar unidad defectuosa", description = "JEFE_DIRECTO reporta una unidad con categorías de falla.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_DIRECTO')")
    @PostMapping("/reporte")
    public ResponseEntity<OrdenTrabajoDTO> reportarUnidad(@Valid @RequestBody ReporteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearReporte(request));
    }

    // ─── ASIGNAR OT (JEFE_TALLER) ─────────────────────────────────────────────┘

    @Operation(summary = "Asignar recursos a OT", description = "JEFE_TALLER asigna jefeTaller, mecánico y diagnóstico a una OT reportada.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PatchMapping("/{idOt}/asignar")
    public ResponseEntity<OrdenTrabajoDTO> asignarOt(@PathVariable Integer idOt, @Valid @RequestBody AsignarOtRequest request) {
        return ResponseEntity.ok(ordenService.asignarOt(idOt, request));
    }

    // ─── COMPLETAR TRABAJO (MECANICO) ─────────────────────────────────────────┘

    @Operation(summary = "Completar trabajo de checklist", description = "MECANICO marca un trabajo como completado y agrega observaciones.")
    @PreAuthorize("hasAuthority('ROLE_MECANICO')")
    @PatchMapping("/trabajos/{idTrabajo}/completar")
    public ResponseEntity<TrabajoOtDTO> completarTrabajo(@PathVariable Integer idTrabajo, @RequestBody CompletarTrabajoRequest request) {
        return ResponseEntity.ok(ordenService.completarTrabajo(idTrabajo, request.getObservaciones()));
    }

    // ─── OT CRUD ──────────────────────────────────────────────────────────────┘

    @Operation(summary = "Listar Ordenes de Trabajo", description = "Retorna todas las OT con filtros opcionales por estado y/o placa del vehiculo.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @GetMapping
    public ResponseEntity<List<OrdenTrabajoDTO>> listarOrdenes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String placa){
        return ResponseEntity.ok(ordenService.listarOrdenes(estado, placa));
    }

    @Operation(summary = "Listar OT paginado", description = "Retorna OT paginadas con filtros opcionales.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @GetMapping("/paginado")
    public ResponseEntity<Page<OrdenTrabajoDTO>> listarOrdenesPaginado(
            @RequestParam int page,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) Integer idMecanico,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta){
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime desde = fechaDesde != null
                ? LocalDate.parse(fechaDesde).atStartOfDay() : null;
        LocalDateTime hasta = fechaHasta != null
                ? LocalDate.parse(fechaHasta).atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(
                ordenService.listarOrdenesPaginado(
                        estado, placa, idMecanico, desde, hasta, pageable));
    }

    @Operation(summary = "Buscar OT por ID", description = "Retorna una orden de trabajo especifica.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @GetMapping("/{idOt}")
    public ResponseEntity<OrdenTrabajoDTO> buscarPorId(@PathVariable Integer idOt){
        return ResponseEntity.ok(ordenService.buscarPorId(idOt));
    }

    @Operation(summary = "Crear una nueva Orden de Trabajo", description = "Registra una nueva orden. Solo JEFE_TALLER.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> crearOrden(@Valid @RequestBody OrdenTrabajoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearOrden(dto));
    }

    @Operation(summary = "Solicitar insumo para una OT", description = "Permite al mecánico solicitar un repuesto del catálogo para una OT.")
    @PreAuthorize("hasAnyAuthority('ROLE_MECANICO', 'ROLE_JEFE_TALLER')")
    @PostMapping("/{idOt}/requerimientos")
    public ResponseEntity<OrdenTrabajoDTO> solicitarInsumo(@PathVariable Integer idOt, @RequestBody RequerimientoInsumoDTO solicitud){
        return ResponseEntity.ok(ordenService.solicitarInsumo(idOt, solicitud));
    }

    @Operation(summary = "Despachar insumo de almacén", description = "Logistica confirma la entrega y cambia la OT a EN_REPARACION.")
    @PreAuthorize("hasAuthority('ROLE_LOGISTICA')")
    @PatchMapping("/requerimientos/{idRequerimiento}/despachar")
    public ResponseEntity<OrdenTrabajoDTO> despacharInsumo(@PathVariable Integer idRequerimiento, @RequestParam BigDecimal cantidadEntregada){
        return ResponseEntity.ok(ordenService.despacharInsumo(idRequerimiento, cantidadEntregada));
    }

    @Operation(summary = "Finalizar OT (mecánico)", description = "El mecánico cierra la OT cambiando de EN_REPARACION a FINALIZADO.")
    @PreAuthorize("hasAuthority('ROLE_MECANICO')")
    @PatchMapping("/{idOt}/finalizar")
    public ResponseEntity<OrdenTrabajoDTO> finalizarOrden(@PathVariable Integer idOt){
        return ResponseEntity.ok(ordenService.finalizarOrden(idOt));
    }

    @Operation(summary = "Cancelar OT", description = "Cancela una OT que no haya sido finalizada o entregada.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PatchMapping("/{idOt}/cancelar")
    public ResponseEntity<OrdenTrabajoDTO> cancelarOrden(@PathVariable Integer idOt){
        return ResponseEntity.ok(ordenService.cancelarOrden(idOt));
    }

    @Operation(summary = "Entregar OT al cliente (jefe taller)", description = "El jefe de taller da la conformidad final: FINALIZADO → ENTREGADO.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PatchMapping("/{idOt}/entregar")
    public ResponseEntity<OrdenTrabajoDTO> entregarOrden(@PathVariable Integer idOt){
        return ResponseEntity.ok(ordenService.entregarOrden(idOt));
    }
}
