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
public class CatalogoInsumoDTO {

    private Integer idInsumo;
    private String codigoInterno;
    private String nombre;
    private String unidadMedida;
    private BigDecimal costoUnitario;
    private BigDecimal stock;
}
