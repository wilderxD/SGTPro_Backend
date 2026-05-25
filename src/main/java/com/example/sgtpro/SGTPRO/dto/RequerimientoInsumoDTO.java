package com.example.sgtpro.SGTPRO.dto;

import java.math.BigDecimal;


public class RequerimientoInsumoDTO {
    
    private Integer idRequerimiento;
        
    private Integer idInsumo;
    private String codigoInsumo;
    private String nombreInsumo;
    
    private BigDecimal cantidadSolicitada;
    private BigDecimal cantidadEntregada;
    private BigDecimal subTotal;

    public RequerimientoInsumoDTO() {
    }

    private RequerimientoInsumoDTO(Integer idRequerimiento, Integer idInsumo, String codigoInsumo, String nombreInsumo, BigDecimal cantidadSolicitada, BigDecimal cantidadEntregada, BigDecimal subTotal) {
        this.idRequerimiento = idRequerimiento;
        this.idInsumo = idInsumo;
        this.codigoInsumo = codigoInsumo;
        this.nombreInsumo = nombreInsumo;
        this.cantidadSolicitada = cantidadSolicitada;
        this.cantidadEntregada = cantidadEntregada;
        this.subTotal = subTotal;
    }

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public BigDecimal getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public BigDecimal getCantidadEntregada() {
        return cantidadEntregada;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }
    
    public static RequerimientoInsumoDTOBuilder builder(){
        return new RequerimientoInsumoDTOBuilder();
    }
    
    public static class RequerimientoInsumoDTOBuilder{
        
        private Integer idRequerimiento;
        private Integer idInsumo;
        private String codigoInsumo;
        private String nombreInsumo;
        private BigDecimal cantidadSolicitada;
        private BigDecimal cantidadEntregada;
        private BigDecimal subtotal;
        
        public RequerimientoInsumoDTOBuilder idRequerimeinto(Integer idRequerimiento){
            this.idRequerimiento = idRequerimiento;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder idInsumo(Integer idInsumo){
            this.idInsumo = idInsumo;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder codigoInsumo(String codigoInsumo){
            this.codigoInsumo = codigoInsumo;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder nombreInsumo(String nombreInsumo){
            this.nombreInsumo = nombreInsumo;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder cantidadSolicitada(BigDecimal cantidadSolicitada){
            this.cantidadSolicitada = cantidadSolicitada;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder cantidadEntregada(BigDecimal cantidadEntregada){
            this.cantidadEntregada = cantidadEntregada;
            return this;
        }
        
        public RequerimientoInsumoDTOBuilder subtotal(BigDecimal subtotal){
            this.subtotal = subtotal;
            return this;
        }
        
        public RequerimientoInsumoDTO build(){
            return new RequerimientoInsumoDTO(idRequerimiento, idInsumo, codigoInsumo, nombreInsumo, cantidadSolicitada, cantidadEntregada, subtotal);
        }
    }
    
}
