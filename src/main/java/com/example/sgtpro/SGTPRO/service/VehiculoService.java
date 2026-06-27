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
        return vehiculoRepository.findAll()
                .stream()
                .map(vehiculoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDTO> obtenerTodosPaginado(String search, Pageable pageable) {
        return vehiculoRepository.findFiltered(search, pageable)
                .map(vehiculoMapper::toDTO);
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
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con placa: " + placa));
        return vehiculoMapper.toDTO(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoDTO actualizar(String placa, VehiculoDTO vEditado) {
        Vehiculo vehiculoExistente = vehiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con placa: " + placa));

        if (vEditado.getMarca() != null) vehiculoExistente.setMarca(vEditado.getMarca());
        if (vEditado.getModelo() != null) vehiculoExistente.setModelo(vEditado.getModelo());
        if (vEditado.getKilometrajeActual() != null) vehiculoExistente.setKilometrajeActual(vEditado.getKilometrajeActual());
        if (vEditado.getProximoMantenimientoKm() != null) vehiculoExistente.setProximoMantenimientoKm(vEditado.getProximoMantenimientoKm());

        return vehiculoMapper.toDTO(vehiculoRepository.save(vehiculoExistente));
    }

    @Override
    @Transactional
    public void eliminarPorPlaca(String placa) {
        if(!vehiculoRepository.existsById(placa)){
            throw new ResourceNotFoundException("No se puede eliminar: El vehículo con placa " + placa + " no existe.");
        }
        vehiculoRepository.deleteByPlaca(placa);
    }
}
