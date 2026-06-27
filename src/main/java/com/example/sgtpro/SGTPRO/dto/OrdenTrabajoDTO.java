package com.example.sgtpro.SGTPRO.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoDTO {

    private Integer idOt;

    private Integer idJefeTaller;

    private String nombreJefeTaller;

    private Integer idMecanico;

    private String nombreMecanico;

    @NotNull(message = "La placa del vehiculo es obligatoria")
    @JsonProperty("placa")
    private String placaVehiculo;

    private LocalDateTime fechaInternamiento;
    private LocalDateTime fechaSalida;
    private String diagnosticoMecanico;
    private String fallasReparadas;
    private Integer kilometraje;
    private BigDecimal costoTotal;
    private String estado;

    private List<RequerimientoInsumoDTO> requerimientos;
    private List<TrabajoOtDTO> trabajos;
}
