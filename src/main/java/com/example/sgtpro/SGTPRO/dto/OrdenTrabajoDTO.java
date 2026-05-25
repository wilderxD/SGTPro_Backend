package com.example.sgtpro.SGTPRO.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenTrabajoDTO {

    private Integer idOt;
    private Integer idJefeTaller;
    private Integer idMecanico;
    private LocalDateTime fechaInternamiento;
    private LocalDateTime fechaSalida;
    private String diagnosticoMecanico;
    private String fallasReparadas;
    private BigDecimal costoTotal;
    private String estado;
    
    //anidamos los insumos requeridos
    private List<RequerimientoInsumoDTO> requerimientos;

    public OrdenTrabajoDTO() {
    }

    private OrdenTrabajoDTO(Integer idOt, Integer idJefeTaller, Integer idMecanico, LocalDateTime fechaInternamiento, LocalDateTime fechaSalida, String diagnosticoMecanico, String fallasReparadas, BigDecimal costoTotal, String estado, List<RequerimientoInsumoDTO> requerimientos) {
        this.idOt = idOt;
        this.idJefeTaller = idJefeTaller;
        this.idMecanico = idMecanico;
        this.fechaInternamiento = fechaInternamiento;
        this.fechaSalida = fechaSalida;
        this.diagnosticoMecanico = diagnosticoMecanico;
        this.fallasReparadas = fallasReparadas;
        this.costoTotal = costoTotal;
        this.estado = estado;
        this.requerimientos = requerimientos;
    }

    public Integer getIdOt() {
        return idOt;
    }

    public Integer getIdJefeTaller() {
        return idJefeTaller;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public LocalDateTime getFechaInternamiento() {
        return fechaInternamiento;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public String getDiagnosticoMecanico() {
        return diagnosticoMecanico;
    }

    public String getFallasReparadas() {
        return fallasReparadas;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public List<RequerimientoInsumoDTO> getRequerimientos() {
        return requerimientos;
    }
    
    public static OrdenTrabajoDTOBuilder builder(){
        return new OrdenTrabajoDTOBuilder();
    }
    
    public static class OrdenTrabajoDTOBuilder{
        private Integer idOt;
        private Integer idJefeTaller;
        private Integer idMecanico;
        private LocalDateTime fechaInternamiento;
        private LocalDateTime fechaSalida;
        private String diagnosticoMecanico;
        private String fallasReparadas;
        private BigDecimal costoTotal;
        private String estado; 
        private List<RequerimientoInsumoDTO> requerimientos;
        
        public OrdenTrabajoDTOBuilder idOt(Integer idOt){
            this.idOt = idOt;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder idJefeTaller(Integer idJefeTaller){
            this.idJefeTaller = idJefeTaller;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder idMecanico(Integer idMecanico){
            this.idMecanico = idMecanico;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder fechaInternamiento(LocalDateTime fechaInternamiento){
            this.fechaInternamiento = fechaInternamiento;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder fechaSalida (LocalDateTime fechaSalida){
            this.fechaSalida = fechaSalida;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder diagnosticoMecanico(String diagnosticoMecanico){
            this.diagnosticoMecanico = diagnosticoMecanico;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder fallasReparadas(String fallasReparadas){
            this.fallasReparadas = fallasReparadas;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder costoTotal(BigDecimal costoTotal){
            this.costoTotal = costoTotal;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder estado(String estado){
            this.estado = estado;
            return this;
        }
        
        public OrdenTrabajoDTOBuilder requerimientos(List<RequerimientoInsumoDTO> requerimientos){
            this.requerimientos = requerimientos;
            return this;
        }
        
        public OrdenTrabajoDTO build(){
            return new OrdenTrabajoDTO(idOt, idJefeTaller, idMecanico, fechaInternamiento, fechaSalida, diagnosticoMecanico, fallasReparadas, costoTotal, estado, requerimientos);
        }
    }
    
}
