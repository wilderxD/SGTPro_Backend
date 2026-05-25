package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import org.springframework.stereotype.Component;

@Component
public class CatalogoInsumoMapper {
    
    public CatalogoInsumo toEntity(CatalogoInsumoDTO dto){
        CatalogoInsumo insumo = new CatalogoInsumo();
        insumo.setIdInsumo(dto.getIdInsumo());
        insumo.setNombre(dto.getNombre());
        insumo.setCodigoInterno(dto.getCodigoInterno());
        insumo.setUnidadMedida(dto.getUnidadMedida());
        insumo.setCostoUnitario(dto.getCostoUnitario());
        
        return insumo;
    }
    
    public CatalogoInsumoDTO toDTO(CatalogoInsumo insumo){
        return CatalogoInsumoDTO.builder()
                .idInsumo(insumo.getIdInsumo())
                .codigoInterno(insumo.getCodigoInterno())
                .nombre(insumo.getNombre())
                .unidadMedida(insumo.getUnidadMedida())
                .costoUnitario(insumo.getCostoUnitario())
                .build();
    }
}
