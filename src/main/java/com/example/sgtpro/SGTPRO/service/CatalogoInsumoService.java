package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.CatalogoInsumoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogoInsumoService implements ICatalogoInsumoService{
    
    private final CatalogoInsumoRepository insumoRepository;
    private final CatalogoInsumoMapper insumoMapper;

    public CatalogoInsumoService(CatalogoInsumoRepository insumoRepository, CatalogoInsumoMapper insumoMapper) {
        this.insumoRepository = insumoRepository;
        this.insumoMapper = insumoMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CatalogoInsumoDTO> listarCatalogoDeInsumos() {
        List<CatalogoInsumoDTO> insumosDTO = insumoRepository.findAll().stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
        
        return insumosDTO;                
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogoInsumoDTO> listarCatalogoInsumosPaginado(String search, Pageable pageable) {
        Page<CatalogoInsumo> listaInsumos = insumoRepository.findFiltered(search, pageable);
        
        return listaInsumos.map(insumoMapper::toDTO);
    }

    @Override
    @Transactional
    public CatalogoInsumoDTO crearInsumo(CatalogoInsumoDTO dto) {
        if(insumoRepository.findByCodigoInterno(dto.getCodigoInterno()).isPresent()){
            throw new BadRequestException("El Insumo de codigo: " + dto.getCodigoInterno() + " ya se encuentra registrado en la base de datos.");
        }
        
        CatalogoInsumoDTO insumoDTO = CatalogoInsumoDTO.builder()
                .codigoInterno(dto.getCodigoInterno())
                .nombre(dto.getNombre())
                .unidadMedida(dto.getUnidadMedida())
                .costoUnitario(dto.getCostoUnitario())
                .stock(dto.getStock() != null ? dto.getStock() : BigDecimal.ZERO)
                .build();
        
        CatalogoInsumo insumo = insumoMapper.toEntity(insumoDTO);
        
        return insumoMapper.toDTO(insumoRepository.save(insumo));
    }
    
    @Override
    @Transactional(readOnly = true)
    public CatalogoInsumoDTO buscarPorId(Integer id){
        return insumoMapper.toDTO(insumoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id)));
    }

    @Override
    @Transactional
    public CatalogoInsumoDTO actualizarInsumo(Integer id, CatalogoInsumoDTO insumoEditado) {
        CatalogoInsumoDTO insumoDTO = buscarPorId(id);
        
        CatalogoInsumoDTO insumoActualizado = CatalogoInsumoDTO.builder()
                .idInsumo(id)
                .codigoInterno(insumoEditado.getCodigoInterno() != null ? insumoEditado.getCodigoInterno() : insumoDTO.getCodigoInterno())
                .nombre(insumoEditado.getNombre() != null ? insumoEditado.getNombre() : insumoDTO.getNombre())
                .unidadMedida(insumoEditado.getUnidadMedida() != null ? insumoEditado.getUnidadMedida() : insumoDTO.getUnidadMedida())
                .costoUnitario(insumoEditado.getCostoUnitario() != null ? insumoEditado.getCostoUnitario() : insumoDTO.getCostoUnitario())
                .stock(insumoEditado.getStock() != null ? insumoEditado.getStock() : insumoDTO.getStock())
                .build();
        
        return insumoMapper.toDTO(insumoRepository.save(insumoMapper.toEntity(insumoActualizado)));
                
    }

    @Override
    @Transactional
    public void eliminarInsumo(Integer id) {
        if(!insumoRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: el insumo con id " + id + " no existe.");
        }
        insumoRepository.deleteById(id);
    }
    
}
