package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer>{ 
   
}
