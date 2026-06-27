package com.example.sgtpro.SGTPRO.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Integer idRol;
    private String nombre;
    private String descripcion;
}
