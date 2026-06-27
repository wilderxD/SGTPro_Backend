package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.CategoriaReporte;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaReporteRepository extends JpaRepository<CategoriaReporte, Integer> {
    List<CategoriaReporte> findAllByOrderByNombreAsc();
}
