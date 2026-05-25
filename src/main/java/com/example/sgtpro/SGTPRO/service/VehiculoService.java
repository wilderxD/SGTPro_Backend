package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.VehiculoMapper;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculoService implements IVehiculoService{
    
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoMapper vehiculoMapper;
    
    public VehiculoService(VehiculoRepository vehiculoRepository, VehiculoMapper vehiculoMapper){
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoMapper = vehiculoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoDTO> obtenerTodos() {
        List<VehiculoDTO> vehiculosDTO = vehiculoRepository.findAll()
                .stream()
                .map(vehiculoMapper::toDTO)
                .toList();
        return vehiculosDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDTO> ObtenerTodosPaginado(Pageable pageable) {
        Page<Vehiculo> vehiculos = vehiculoRepository.findAll(pageable);
        
        return vehiculos.map(vehiculoMapper::toDTO);
    }

    @Override
    @Transactional
    public VehiculoDTO guardar(VehiculoDTO vehiculoDTO) {
        if(vehiculoRepository.existsById(vehiculoDTO.getPlaca())){
            throw new BadRequestException("Ya existe un vehiculo registrado con la placa: " + vehiculoDTO.getPlaca());
        }
        
        Vehiculo vehiculo = vehiculoMapper.toEntity(vehiculoDTO);
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);
        
        return vehiculoMapper.toDTO(vehiculoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO obtenerPorPlaca(String placa) {
        return vehiculoMapper.toDTO(vehiculoRepository.findByPlaca(placa).orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con placa: " + placa)));        //agregar exception personalizado
    }

    @Override
    @Transactional
    public VehiculoDTO actualizar(String placa, VehiculoDTO vEditado) {
        VehiculoDTO vehiculoExistente = obtenerPorPlaca(placa);
        
       VehiculoDTO vehiculoActualizado = VehiculoDTO.builder()
               .placa(placa)
               .marca(vEditado.getMarca() != null ? vEditado.getMarca() : vehiculoExistente.getMarca())
               .modelo(vEditado.getModelo() != null ? vEditado.getModelo() : vehiculoExistente.getModelo())
               .kilometrajeActual(vEditado.getKilometrajeActual() != null ? vEditado.getKilometrajeActual() : vehiculoExistente.getKilometrajeActual())
               .build();
        
        return vehiculoMapper.toDTO(vehiculoRepository.save(vehiculoMapper.toEntity(vehiculoActualizado)));
    }

    @Override
    @Transactional
    public void eliminarPorPlaca(String placa) {
        if(!vehiculoRepository.existsById(placa)){
            throw new RuntimeException("No se puede eliminar: El vehículo con placa " + placa + " no existe.");
        }
        vehiculoRepository.deleteByPlaca(placa);
    }
    
}
