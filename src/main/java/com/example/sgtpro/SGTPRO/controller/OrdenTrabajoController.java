package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.service.OrdenTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin("*")
@Tag(name = "Maestro de Ordenes de trabajo", description = "Endpoints para la gestión de las Ordenes de Trabajo")
public class OrdenTrabajoController {
    
    private final OrdenTrabajoService ordenService;
    
    public OrdenTrabajoController(OrdenTrabajoService ordenService){
        this.ordenService = ordenService;
    }
    
    @Operation(summary = "Crear una nueva Orden de Trabajo", description = "Registra una nueva orden de trabajo en el sistema asignando al mecánico y al jefe de taller. Inicializa el costo en 0 y el estado en 'EN_REVISION'.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> crearOrden(@RequestBody OrdenTrabajoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearOrden(dto));
    }
    
    @Operation(summary = "Solicitar insumo para una OT", description = "Permite al mecánico solicitar un repuesto del catálogo para una orden de trabajo específica. Solo crea el requerimiento, aún no afecta el costo total de la orden.")
    @PreAuthorize("hasAnyAuthority('ROLE_MECANICO', 'ROLE_JEFE_TALLER')")
    @PostMapping("/{idOt}/requerimientos")
    public ResponseEntity<OrdenTrabajoDTO> solicitarInsumo(@PathVariable Integer idOt, @RequestBody RequerimientoInsumoDTO solicitud){
        return ResponseEntity.ok(ordenService.solicitarInsumo(idOt, solicitud));
    }
    
    @Operation(summary = "Despachar insumo de almacén (Uso Exclusivo de Logística)", description = "Permite al área de logística confirmar la entrega física de un insumo solicitado. Este endpoint calcula el subtotal del repuesto y lo suma automáticamente al costo total de la Orden de Trabajo, cambiando su estado a 'EN_REPARACION'.")
    @PreAuthorize("hasAuthority('ROLE_LOGISTICA')")
    @PatchMapping("/requerimientos/{idRequerimiento}/despachar")
    public ResponseEntity<OrdenTrabajoDTO> DespacharInsumo(@PathVariable Integer idRequerimiento, @RequestParam BigDecimal cantidadEntregada){
        return ResponseEntity.ok(ordenService.despacharInsumo(idRequerimiento, cantidadEntregada));
    }
}
