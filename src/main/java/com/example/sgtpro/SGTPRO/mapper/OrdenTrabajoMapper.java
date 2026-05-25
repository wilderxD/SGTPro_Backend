package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrdenTrabajoMapper {
    
    public OrdenTrabajoDTO toDTO(OrdenTrabajo entity){
        if(entity == null){
            return null;
        }
        
        return OrdenTrabajoDTO.builder()
                .idOt(entity.getIdOt())
                .idJefeTaller(entity.getIdJefeTaller())
                .idMecanico(entity.getIdMecanico())
                .fechaInternamiento(entity.getFechaInternamiento())
                .fechaSalida(entity.getFechaSalida())
                .diagnosticoMecanico(entity.getDiagnosticoMecanico())
                .fallasReparadas(entity.getFallasReportadas())
                .costoTotal(entity.getCostoTotal())
                .estado(entity.getEstado())
                .requerimientos(entity.getRequerimientos() != null ? entity.getRequerimientos()
                        .stream()
                        .map(this::toRequerimientoDTO)
                        .collect(Collectors.toList()) :
                        Collections.emptyList())
                        .build();
    }
    
    //mapeamos el detalle
    private RequerimientoInsumoDTO toRequerimientoDTO(RequerimientoInsumo req){
        if(req == null){
            return null;
        }
        
        return RequerimientoInsumoDTO.builder()
                .idRequerimeinto(req.getIdRequerimiento())
                //extraemos los datos utiles de la entidad insumo relacionada
                .idInsumo(req.getInsumo() != null ? req.getInsumo().getIdInsumo() : null)
                .codigoInsumo(req.getInsumo() != null ? req.getInsumo().getCodigoInterno() : null)
                .nombreInsumo(req.getInsumo() != null ? req.getInsumo().getNombre() : null)
                //cantidades y costos
                .cantidadSolicitada(req.getCantidadSolicitada())
                .cantidadEntregada(req.getCantidadEntregada())
                .subtotal(req.getSubtotal())
                .build();
    }
    
}
