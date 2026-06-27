package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String>{
    public Optional<Vehiculo> findByPlaca(String placa);

    public void deleteByPlaca(String placa);

    @Query("SELECT v FROM Vehiculo v WHERE "
            + "(:search IS NULL OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Vehiculo> findFiltered(@Param("search") String search, Pageable pageable);
}
