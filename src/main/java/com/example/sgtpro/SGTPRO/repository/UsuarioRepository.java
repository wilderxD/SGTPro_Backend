package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND "
            + "(:search IS NULL OR LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Usuario> findFiltered(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :nombreRol AND u.activo = true")
    List<Usuario> findByRolNombre(@Param("nombreRol") String nombreRol);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true")
    List<Usuario> findAllActivos();
}
