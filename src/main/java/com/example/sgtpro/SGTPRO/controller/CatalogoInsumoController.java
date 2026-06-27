package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import com.example.sgtpro.SGTPRO.service.ICatalogoInsumoService;
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
@RequestMapping("/api/catalogoInsumos")
@Tag(name = "Maestro Catalogo de Insumos", description = "Endpoints para la gestion del catalogo de insumos.")
public class CatalogoInsumoController {
    
    private final ICatalogoInsumoService insumoService;
    private final int pageSize;

    public CatalogoInsumoController(ICatalogoInsumoService insumoService, @Value("${app.page-size}") int pageSize) {
        this.insumoService = insumoService;
        this.pageSize = pageSize;
    }
    
   @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Listar Insumos", description = "Retorna una lista de todos los insumos registrados en la base de datos.")
    @GetMapping
    public ResponseEntity<List<CatalogoInsumoDTO>> listarCatalogoInsumos(){
        List<CatalogoInsumoDTO> insumosDTO = insumoService.listarCatalogoDeInsumos();
        return ResponseEntity.ok(insumosDTO);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Listar Insumos", description = "Retorna todos los Insumos paginados, requiere un entero con el numero de pagina.")
    @GetMapping("/paginado")
    public ResponseEntity<Page<CatalogoInsumoDTO>> listarCatalogoInsumosPaginado(
            @RequestParam int page,
            @RequestParam(required = false) String search){
        Pageable miOrden = PageRequest.of(page, pageSize);
        
        Page<CatalogoInsumoDTO> respuesta = insumoService.listarCatalogoInsumosPaginado(search, miOrden);
        return ResponseEntity.ok(respuesta);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_LOGISTICA', 'ROLE_JEFE_TALLER', 'ROLE_MECANICO')")
    @Operation(summary = "Registrar un nuevo Insumo", description = "Valida y registra un nuevo Insumo en la base de taos, requiere un DTO de CatalogoInsumo.")
    @PostMapping
    public ResponseEntity<CatalogoInsumoDTO> guardarInsumo(@Valid @RequestBody CatalogoInsumoDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.crearInsumo(dto));
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO', 'ROLE_MECANICO', 'ROLE_LOGISTICA')")
    @Operation(summary = "Buscar un Insumo", description = "Retorna un Insumo buscado por el id, requiere un dato de tipo Integer id para la busqueda.")
    @GetMapping("/{id}")
    public ResponseEntity<CatalogoInsumoDTO> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(insumoService.buscarPorId(id));
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_LOGISTICA', 'ROLE_JEFE_TALLER', 'ROLE_MECANICO')")
    @Operation(summary = "Actualizar un Insumo", description = "Valida y actualiza los datos de un insumo, requiere id y DTO de CatalogoInsumo.")
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoInsumoDTO> actualizarInsumo(@Valid @PathVariable Integer id, @RequestBody CatalogoInsumoDTO insumoEditado){
        CatalogoInsumoDTO insumoActualizado = insumoService.actualizarInsumo(id, insumoEditado);
        
        return ResponseEntity.ok(insumoActualizado);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_LOGISTICA', 'ROLE_JEFE_TALLER')")
    @Operation(summary = "Elimina un Insumo", description = "Valida y elimina un Insumo, requiere un Integer id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInsumo(@PathVariable Integer id){
        insumoService.eliminarInsumo(id);
        
        return ResponseEntity.noContent().build();
    }
    
}
