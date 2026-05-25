package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoInsumoRepository extends JpaRepository<CatalogoInsumo, Integer>{
    Optional<CatalogoInsumo> findByCodigoInterno(String codigoInterno);
}
