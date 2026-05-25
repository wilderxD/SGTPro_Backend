package com.example.sgtpro.SGTPRO.dto;

import java.math.BigDecimal;

public class CatalogoInsumoDTO {

    private Integer idInsumo;
    private String codigoInterno;
    private String nombre;
    private String unidadMedida;
    private BigDecimal costoUnitario;

    public CatalogoInsumoDTO() {
    }
        
    private CatalogoInsumoDTO(Integer idInsumo, String codigoInterno, String nombre, String unidadMedida, BigDecimal costoUnitario) {
        this.idInsumo = idInsumo;
        this.codigoInterno = codigoInterno;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.costoUnitario = costoUnitario;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }
    
    public static CatalogoInsumoDTOBuilder builder(){
        return new CatalogoInsumoDTOBuilder();
    }
    
    public static class CatalogoInsumoDTOBuilder{
        private Integer idInsumo;
        private String codigoInterno;
        private String nombre;
        private String unidadMedida;
        private BigDecimal costoUnitario;
        
        public CatalogoInsumoDTOBuilder idInsumo(Integer idInsumo){
            this.idInsumo = idInsumo;
            return this;
        }
        
        public CatalogoInsumoDTOBuilder codigoInterno(String codigoInterno){
            this.codigoInterno = codigoInterno;
            return this;
        }
        
        public CatalogoInsumoDTOBuilder nombre(String nombre){
            this.nombre = nombre;
            return this;
        }
        
        public CatalogoInsumoDTOBuilder unidadMedida(String unidadMedida){
            this.unidadMedida = unidadMedida;
            return this;
        }
        
        public CatalogoInsumoDTOBuilder costoUnitario(BigDecimal costoUnitario){
            this.costoUnitario = costoUnitario;
            return this;
        }
        
        public CatalogoInsumoDTO build(){
            return new CatalogoInsumoDTO(idInsumo, codigoInterno, nombre, unidadMedida, costoUnitario);
        }
        
    }
    
    
}
