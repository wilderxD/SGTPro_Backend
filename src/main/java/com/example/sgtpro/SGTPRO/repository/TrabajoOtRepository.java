package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.TrabajoOt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrabajoOtRepository extends JpaRepository<TrabajoOt, Integer> {
    List<TrabajoOt> findByOrdenTrabajo_IdOtOrderByIdTrabajoAsc(Integer idOt);
}
