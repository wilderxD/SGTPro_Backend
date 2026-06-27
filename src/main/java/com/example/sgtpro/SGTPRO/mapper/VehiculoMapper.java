package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {
    
    public Vehiculo toEntity(VehiculoDTO dto){
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setKilometrajeActual(dto.getKilometrajeActual());
        vehiculo.setProximoMantenimientoKm(dto.getProximoMantenimientoKm());
        
        return vehiculo;
    }
    
    public VehiculoDTO toDTO(Vehiculo vehiculo){
        return VehiculoDTO.builder()
                .placa(vehiculo.getPlaca())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .kilometrajeActual(vehiculo.getKilometrajeActual())
                .proximoMantenimientoKm(vehiculo.getProximoMantenimientoKm())
                .build();
    }
    
}
