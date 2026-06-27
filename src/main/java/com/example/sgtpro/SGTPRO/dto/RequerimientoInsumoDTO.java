package com.example.sgtpro.SGTPRO.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoInsumoDTO {

    private Integer idRequerimiento;
    private Integer idInsumo;
    private String codigoInsumo;
    private String nombreInsumo;
    private Integer idSolicitadoPor;
    private String nombreSolicitante;
    private BigDecimal cantidadSolicitada;
    private BigDecimal cantidadEntregada;
    private BigDecimal subtotal;
}
