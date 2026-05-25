package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ot")
    private Integer idOt;
    
    @Column(name = "id_jefe_taller", nullable = false)
    private Integer idJefeTaller;
    
    @Column(name = "id_mecanico", nullable = false)
    private Integer idMecanico;
    
    @Column(name = "fecha_internamiento")
    private LocalDateTime fechaInternamiento;
    
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;
    
    @Column(name = "diagnostico_mecanico", columnDefinition = "TEXT")
    private String diagnosticoMecanico;
    
    @Column(name = "fallas_reparadas", columnDefinition = "TEXT")
    private String fallasReportadas;
    
    @Column(name = "costo_total", precision = 10, scale = 2)
    private BigDecimal costoTotal = BigDecimal.ZERO;
    
    @Column(length = 50)
    private String estado = "EN_REVISION";
    
    //Relacion Bidireccional; cascade= CascadeType.ALL permite guardar la OT y sus insumos al mismo tiempo
    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequerimientoInsumo> requerimientos;

    public OrdenTrabajo() {
    }

    public OrdenTrabajo(Integer idOt, Integer idJefeTaller, Integer idMecanico, LocalDateTime fechaInternamiento, LocalDateTime fechaSalida, String diagnosticoMecanico, String fallasReportadas, List<RequerimientoInsumo> requerimientos) {
        this.idOt = idOt;
        this.idJefeTaller = idJefeTaller;
        this.idMecanico = idMecanico;
        this.fechaInternamiento = fechaInternamiento;
        this.fechaSalida = fechaSalida;
        this.diagnosticoMecanico = diagnosticoMecanico;
        this.fallasReportadas = fallasReportadas;
        this.requerimientos = requerimientos;
    }

    public Integer getIdOt() {
        return idOt;
    }

    public void setIdOt(Integer idOt) {
        this.idOt = idOt;
    }

    public Integer getIdJefeTaller() {
        return idJefeTaller;
    }

    public void setIdJefeTaller(Integer idJefeTaller) {
        this.idJefeTaller = idJefeTaller;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(Integer idMecanico) {
        this.idMecanico = idMecanico;
    }

    public LocalDateTime getFechaInternamiento() {
        return fechaInternamiento;
    }

    public void setFechaInternamiento(LocalDateTime fechaInternamiento) {
        this.fechaInternamiento = fechaInternamiento;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getDiagnosticoMecanico() {
        return diagnosticoMecanico;
    }

    public void setDiagnosticoMecanico(String diagnosticoMecanico) {
        this.diagnosticoMecanico = diagnosticoMecanico;
    }

    public String getFallasReportadas() {
        return fallasReportadas;
    }

    public void setFallasReportadas(String fallasReportadas) {
        this.fallasReportadas = fallasReportadas;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<RequerimientoInsumo> getRequerimientos() {
        return requerimientos;
    }

    public void setRequerimientos(List<RequerimientoInsumo> requerimientos) {
        this.requerimientos = requerimientos;
    }
    
    
}
