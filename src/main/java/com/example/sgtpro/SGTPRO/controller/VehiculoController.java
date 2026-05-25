package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.mapper.VehiculoMapper;
import com.example.sgtpro.SGTPRO.service.IVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
@Tag(name = "Maestro de Vehiculos", description = "Endpoints para la gestión de la flota de camiones")
public class VehiculoController {
    private final IVehiculoService vehiculoService;

    public VehiculoController(IVehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }
    
    @Operation(summary = "Listar flota", description = "Retorna todos los vehículos registrados sin paginación.")
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listarVehiculos(){
        
        List<VehiculoDTO> vehiculosDTO = vehiculoService.obtenerTodos();
        
        return ResponseEntity.ok(vehiculosDTO);
    }
    
    @Operation(summary = "Listar flota", description = "Retorna todos los vehiculos registrados paginados de 8 vehiculos cada pagina, requiere un dato de tipo entero el cual es el numero de pagina")
    @GetMapping("/paginado")
    public ResponseEntity<Page<VehiculoDTO>> listarPaginado(@RequestParam int page){
        
        Pageable miOrden = PageRequest.of(page, 8);
        
        Page<VehiculoDTO> respuestas = vehiculoService.ObtenerTodosPaginado(miOrden);
        return ResponseEntity.ok(respuestas);
    }
    
    @Operation(summary = "Registrar nuevo camion", description = "Valida y registra un nuevo vehículo en la base de datos, requiere un DTO de vehiculo.")
    @PostMapping
    public ResponseEntity<VehiculoDTO> guardarVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO){
        
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.guardar(vehiculoDTO));        
    }
    
    @Operation(summary = "Buscar un camion", description = "Retorna el camion buscado por placa, requiere un dato de tipo String placa para la busqueda(BAZ-911)")
    @GetMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> obtenerUnVehiculo(@PathVariable String placa){
        return ResponseEntity.ok(vehiculoService.obtenerPorPlaca(placa));
    }
    
    @Operation(summary = "Actualiza un vehiculo", description = "Valida y actualiza los datos de un vehículo en la base de datos, requiere un dato de tipo String  placa para la actualizacion(BAZ-911) y un DTO de Vehiculo.")
    @PutMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> actualizarVehiculo(@Valid @RequestBody VehiculoDTO vehiculoEditado, @PathVariable String placa){
        VehiculoDTO vehiculoActualizado = vehiculoService.actualizar(placa, vehiculoEditado);
        
        return ResponseEntity.ok(vehiculoActualizado);
    }
    
    @Operation(summary = "Elimina un vehiculo", description = "Valida y elimina un vehículo en la base de datos, requiere un dato de tipo String  placa para la eliminación(BAZ-911).")
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable String placa){
        vehiculoService.eliminarPorPlaca(placa);
        return ResponseEntity.noContent().build();
    }
    
}
