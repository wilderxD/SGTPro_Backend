package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer>{
    
}
