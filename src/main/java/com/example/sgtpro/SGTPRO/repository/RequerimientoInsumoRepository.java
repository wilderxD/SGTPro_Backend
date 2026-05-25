package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequerimientoInsumoRepository extends JpaRepository<RequerimientoInsumo, Integer>{
    
}
