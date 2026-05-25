package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String>{
    public Optional<Vehiculo> findByPlaca(String placa);
    public void deleteByPlaca(String placa);
}
