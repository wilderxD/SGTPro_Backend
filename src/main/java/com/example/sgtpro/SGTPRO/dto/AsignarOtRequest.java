package com.example.sgtpro.SGTPRO.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarOtRequest {

    @NotNull(message = "El id del jefe de taller es obligatorio")
    private Integer idJefeTaller;

    @NotNull(message = "El id del mecánico es obligatorio")
    private Integer idMecanico;

    private String diagnosticoMecanico;
    private Integer kilometraje;
}
