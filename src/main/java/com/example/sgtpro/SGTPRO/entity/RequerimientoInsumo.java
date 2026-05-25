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
import lombok.Data;

@Entity
@Table(name = "requerimientos_insumo")       
public class RequerimientoInsumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requerimiento")
    private Integer idRequerimiento;
    
    //Relacion hacia la orden de trabajo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ot", nullable = false)
    private OrdenTrabajo ordenTrabajo;
    
    //Realacion hacia el catalogo de insumos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo", nullable = false)
    private CatalogoInsumo insumo;
    
    @Column(name = "cantidad_solicitada", nullable = false, precision = 8, scale = 2)
    private BigDecimal cantidadSolicitada;
    
    @Column(name = "cantidad_entregada", precision = 8, scale = 2)
    private BigDecimal cantidadEntregada = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    public RequerimientoInsumo() {
    }

    public RequerimientoInsumo(Integer idRequerimiento, OrdenTrabajo ordenTrabajo, CatalogoInsumo insumo, BigDecimal cantidadSolicitada) {
        this.idRequerimiento = idRequerimiento;
        this.ordenTrabajo = ordenTrabajo;
        this.insumo = insumo;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public void setIdRequerimiento(Integer idRequerimiento) {
        this.idRequerimiento = idRequerimiento;
    }

    public OrdenTrabajo getOrdenTrabajo() {
        return ordenTrabajo;
    }

    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
        this.ordenTrabajo = ordenTrabajo;
    }

    public CatalogoInsumo getInsumo() {
        return insumo;
    }

    public void setInsumo(CatalogoInsumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(BigDecimal cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public BigDecimal getCantidadEntregada() {
        return cantidadEntregada;
    }

    public void setCantidadEntregada(BigDecimal cantidadEntregada) {
        this.cantidadEntregada = cantidadEntregada;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
}
