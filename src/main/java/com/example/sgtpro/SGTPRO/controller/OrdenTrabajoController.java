package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.service.OrdenTrabajoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "1. Maestro de Vehiculos", description = "Endpoints para la gestión de la flota de camiones")
public class OrdenTrabajoController {
    
    private final OrdenTrabajoService ordenService;
    
    public OrdenTrabajoController(OrdenTrabajoService ordenService){
        this.ordenService = ordenService;
    }
    
    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> crearOrden(@RequestBody OrdenTrabajoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearOrden(dto));
    }
    
    @PostMapping("/{idOt}/requerimientos")
    public ResponseEntity<OrdenTrabajoDTO> solicitarInsumo(@PathVariable Integer idOt, @RequestBody RequerimientoInsumoDTO solicitud){
        return ResponseEntity.ok(ordenService.solicitarInsumo(idOt, solicitud));
    }
    
    @PatchMapping("/requerimientos/{idRequerimiento}/despachar")
    public ResponseEntity<OrdenTrabajoDTO> DespacharInsumo(@PathVariable Integer idRequerimiento, @RequestParam BigDecimal cantidadEntregada){
        return ResponseEntity.ok(ordenService.despacharInsumo(idRequerimiento, cantidadEntregada));
    }
}
