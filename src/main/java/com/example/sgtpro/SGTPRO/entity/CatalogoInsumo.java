package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "catalogo_insumos")
public class CatalogoInsumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Integer idInsumo;
    
    @Column(name = "codigoInterno", unique = true, length = 50)
    private String codigoInterno;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;
    
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    public CatalogoInsumo() {
    }

    public CatalogoInsumo(Integer idInsumo, String codigoInterno, String nombre, String unidadMedida, BigDecimal costoUnitario) {
        this.idInsumo = idInsumo;
        this.codigoInterno = codigoInterno;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.costoUnitario = costoUnitario;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Integer idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
    
}
