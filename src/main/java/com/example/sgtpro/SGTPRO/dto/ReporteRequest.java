package com.example.sgtpro.SGTPRO.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {

    @NotBlank(message = "La placa del vehículo es obligatoria")
    private String placa;

    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private List<Integer> idCategorias;

    private String observaciones;
}
