package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoInsumoRepository extends JpaRepository<CatalogoInsumo, Integer>{
    Optional<CatalogoInsumo> findByCodigoInterno(String codigoInterno);

    @Query("SELECT i FROM CatalogoInsumo i WHERE "
            + "(:search IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(i.codigoInterno) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CatalogoInsumo> findFiltered(@Param("search") String search, Pageable pageable);

    List<CatalogoInsumo> findByStockLessThanEqual(BigDecimal stock);
}
