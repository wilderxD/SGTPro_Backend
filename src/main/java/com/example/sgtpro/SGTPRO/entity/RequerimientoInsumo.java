package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requerimientos_insumo")
public class RequerimientoInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requerimiento")
    private Integer idRequerimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ot", nullable = false)
    private OrdenTrabajo ordenTrabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo", nullable = false)
    private CatalogoInsumo insumo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por", nullable = false)
    private Usuario solicitadoPor;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 8, scale = 2)
    private BigDecimal cantidadSolicitada;

    @Column(name = "cantidad_entregada", precision = 8, scale = 2)
    private BigDecimal cantidadEntregada = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
}
