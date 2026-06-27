package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ICatalogoInsumoService {

    public List<CatalogoInsumoDTO> listarCatalogoDeInsumos();

    public Page<CatalogoInsumoDTO> listarCatalogoInsumosPaginado(String search, Pageable pageable);

    public CatalogoInsumoDTO crearInsumo(CatalogoInsumoDTO dto);

    public CatalogoInsumoDTO buscarPorId(Integer id);

    public CatalogoInsumoDTO actualizarInsumo(Integer id, CatalogoInsumoDTO insumoEditado);

    public void eliminarInsumo(Integer id);
    
}
