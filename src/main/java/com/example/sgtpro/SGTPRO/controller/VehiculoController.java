package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.service.IVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Maestro de Vehiculos", description = "Endpoints para la gestión de la flota de camiones")
public class VehiculoController {
    private final IVehiculoService vehiculoService;
    private final int pageSize;

    public VehiculoController(IVehiculoService vehiculoService, @Value("${app.page-size}") int pageSize) {
        this.vehiculoService = vehiculoService;
        this.pageSize = pageSize;
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Listar flota", description = "Retorna todos los vehículos registrados sin paginación.")
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listarVehiculos(){
        
        List<VehiculoDTO> vehiculosDTO = vehiculoService.obtenerTodos();
        
        return ResponseEntity.ok(vehiculosDTO);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Listar flota", description = "Retorna todos los vehiculos paginados, requiere un entero con el numero de pagina.")
    @GetMapping("/paginado")
    public ResponseEntity<Page<VehiculoDTO>> listarPaginado(
            @RequestParam int page,
            @RequestParam(required = false) String search){
        
        Pageable miOrden = PageRequest.of(page, pageSize);
        
        Page<VehiculoDTO> respuestas = vehiculoService.obtenerTodosPaginado(search, miOrden);
        return ResponseEntity.ok(respuestas);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @Operation(summary = "Registrar nuevo camion", description = "Valida y registra un nuevo vehículo en la base de datos, requiere un DTO de vehiculo.")
    @PostMapping
    public ResponseEntity<VehiculoDTO> guardarVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO){
        
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.guardar(vehiculoDTO));        
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Buscar un camion", description = "Retorna el camion por placa, requiere un String (BAZ-911).")
    @GetMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> obtenerUnVehiculo(@PathVariable String placa){
        return ResponseEntity.ok(vehiculoService.obtenerPorPlaca(placa));
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @Operation(summary = "Actualiza un vehiculo", description = "Valida y actualiza los datos de un vehículo, requiere placa (BAZ-911) y DTO.")
    @PutMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> actualizarVehiculo(@RequestBody VehiculoDTO vehiculoEditado, @PathVariable String placa){
        VehiculoDTO vehiculoActualizado = vehiculoService.actualizar(placa, vehiculoEditado);
        
        return ResponseEntity.ok(vehiculoActualizado);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @Operation(summary = "Elimina un vehiculo", description = "Valida y elimina un vehículo, requiere placa (BAZ-911).")
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable String placa){
        vehiculoService.eliminarPorPlaca(placa);
        return ResponseEntity.noContent().build();
    }
    
}
