package com.example.sgtpro.SGTPRO.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia para los datos de un vehiculo de la flota")
public class VehiculoDTO {

    @NotBlank(message = "La placa no puede estar vacía")
    @Schema(description = "Placa del vehiculo", example = "BAZ-911")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Schema(description = "Marca camión", example = "Volkswagen")
    private String marca;

    @Schema(description = "Modelo o linea del vehiculo", example = "Robust 17.230")
    private String modelo;

    @NotNull(message = "El kilometraje es obligatorio")
    @Schema(description = "Kilometraje actual leido del odometro", example = "150000")
    private Integer kilometrajeActual;

    @Schema(description = "Kilometraje para el próximo mantenimiento general", example = "160000")
    private Integer proximoMantenimientoKm;
}
