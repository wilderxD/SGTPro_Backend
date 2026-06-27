package com.example.sgtpro.SGTPRO.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrabajoOtDTO {
    private Integer idTrabajo;
    private String descripcion;
    private Boolean completado;
    private String observaciones;
}
