package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.dto.TrabajoOtDTO;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import com.example.sgtpro.SGTPRO.entity.TrabajoOt;
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
                .idJefeTaller(entity.getJefeTaller() != null ? entity.getJefeTaller().getIdUsuario() : null)
                .nombreJefeTaller(entity.getJefeTaller() != null ? entity.getJefeTaller().getNombreCompleto() : null)
                .idMecanico(entity.getMecanico() != null ? entity.getMecanico().getIdUsuario() : null)
                .nombreMecanico(entity.getMecanico() != null ? entity.getMecanico().getNombreCompleto() : null)
                .placaVehiculo(entity.getVehiculo() != null ? entity.getVehiculo().getPlaca() : null)
                .fechaInternamiento(entity.getFechaInternamiento())
                .fechaSalida(entity.getFechaSalida())
                .diagnosticoMecanico(entity.getDiagnosticoMecanico())
                .fallasReparadas(entity.getFallasReparadas())
                .kilometraje(entity.getKilometraje())
                .costoTotal(entity.getCostoTotal())
                .estado(entity.getEstado())
                .requerimientos(entity.getRequerimientos() != null ? entity.getRequerimientos()
                        .stream()
                        .map(this::toRequerimientoDTO)
                        .collect(Collectors.toList()) :
                        Collections.emptyList())
                .trabajos(entity.getTrabajos() != null ? entity.getTrabajos()
                        .stream()
                        .map(this::toTrabajoDTO)
                        .collect(Collectors.toList()) :
                        Collections.emptyList())
                        .build();
    }
    
    private TrabajoOtDTO toTrabajoDTO(TrabajoOt t) {
        if (t == null) return null;
        return TrabajoOtDTO.builder()
                .idTrabajo(t.getIdTrabajo())
                .descripcion(t.getDescripcion())
                .completado(t.getCompletado())
                .observaciones(t.getObservaciones())
                .build();
    }

    //mapeamos el detalle
    private RequerimientoInsumoDTO toRequerimientoDTO(RequerimientoInsumo req){
        if(req == null){
            return null;
        }
        
        return RequerimientoInsumoDTO.builder()
                .idRequerimiento(req.getIdRequerimiento())
                .idInsumo(req.getInsumo() != null ? req.getInsumo().getIdInsumo() : null)
                .codigoInsumo(req.getInsumo() != null ? req.getInsumo().getCodigoInterno() : null)
                .nombreInsumo(req.getInsumo() != null ? req.getInsumo().getNombre() : null)
                .idSolicitadoPor(req.getSolicitadoPor() != null ? req.getSolicitadoPor().getIdUsuario() : null)
                .nombreSolicitante(req.getSolicitadoPor() != null ? req.getSolicitadoPor().getNombreCompleto() : null)
                .cantidadSolicitada(req.getCantidadSolicitada())
                .cantidadEntregada(req.getCantidadEntregada())
                .subtotal(req.getSubtotal())
                .build();
    }
    
}
