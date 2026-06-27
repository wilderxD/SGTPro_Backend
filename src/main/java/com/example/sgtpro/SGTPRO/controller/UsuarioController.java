package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.service.IUsuarioService;
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

@RestController()
@RequestMapping("/api/usuarios")
@Tag(name = "Maestro de Usuarios", description = "Endpoints para la gestion de los usuarios del sistema")
public class UsuarioController {
    
    private final IUsuarioService usuarioService;
    private final int pageSize;

    public UsuarioController(IUsuarioService usuarioService, @Value("${app.page-size}") int pageSize) {
        this.usuarioService = usuarioService;
        this.pageSize = pageSize;
    }
    
    @Operation(summary = "Listar mecánicos activos", description = "Retorna la lista de usuarios con rol MECANICO. Accesible para todos los roles autenticados.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mecanicos")
    public ResponseEntity<List<UsuarioDTO>> listarMecanicos(){
        return ResponseEntity.ok(usuarioService.listarUsuariosPorRol("ROLE_MECANICO"));
    }

    @Operation(summary = "Listar Usuarios", description = "Retorna una lista con todos los usuarios que se encuentran en la base de datos.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(){
        List<UsuarioDTO> usuarios =  usuarioService.listarUsuarios();
        
        return ResponseEntity.ok(usuarios);
    }
    
    @Operation(summary = "Listar Paginado de usuarios", description = "Retorna los usuarios registrados paginados.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @GetMapping("/paginado")
    public ResponseEntity<Page<UsuarioDTO>> listarPaginado(
            @RequestParam int page,
            @RequestParam(required = false) String search){
        Pageable miOrden = PageRequest.of(page, pageSize);
        
        Page<UsuarioDTO> respuesta = usuarioService.listarUsuariosPaginado(search, miOrden);
        return ResponseEntity.ok(respuesta);        
    }
    
    @Operation(summary = "Registrar un nuevo Usuario", description = "Valida y registra un nuevo usuario en la base de datis, requiere un DTO de usuario.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardarUsuario(usuarioDTO));
    }
    
    @Operation(summary = "Buscar un usuario", description = "Retorna el usuario buscado por ID, requiere un dato de tipo Integer id para la busqueda.")
    @PreAuthorize("hasAnyAuthority('ROLE_JEFE_TALLER', 'ROLE_JEFE_DIRECTO')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUnUsuario(@PathVariable Integer id){
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }    
    
    @Operation(summary = "Actualizar un Usuario", description = "Valida y actualiza los datos de un Usuario, requiere id y DTO.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@Valid @PathVariable Integer id, @RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    @Operation(summary = "Elimina un Usuario", description = "Valida y elimina un Usuario, requiere un Integer id.")
    @PreAuthorize("hasAuthority('ROLE_JEFE_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id){
        usuarioService.eliminarPorID(id);
        return ResponseEntity.noContent().build();
    }
}
