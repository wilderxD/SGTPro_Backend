package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.VehiculoDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IVehiculoService {

    public List<VehiculoDTO> obtenerTodos();

    public Page<VehiculoDTO> obtenerTodosPaginado(String search, Pageable pageable);

    public VehiculoDTO guardar(VehiculoDTO vehiculo);

    public VehiculoDTO obtenerPorPlaca(String placa);

    public VehiculoDTO actualizar(String placa, VehiculoDTO vEditado);

    public void eliminarPorPlaca(String placa);
    
}
