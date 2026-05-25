package com.example.sgtpro.SGTPRO.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Objeto de transferencia para los datos de un vehiculo de la flota")
public class VehiculoDTO {

    @NotBlank(message = "La placa no puede estar vacía")
    @Schema(description = "Placa del vehiculo", example = "BAZ-911")
    private String placa;
        
    @NotBlank(message = "La marca es obligatoria")
    @Schema(description = "Marca camión", example = "Volkswagen")
    private String marca;
    
    @Schema(description = "Modelo o linea del vehiculo", example = "Robust 17.230")
    private String modelo;
    
    @NotNull(message = "El kilometraje es obligatorio")
    @Schema(description = "Kilometraje actual leido del odometro", example = "150000")
    private Integer kilometrajeActual;

    public VehiculoDTO() {
    }
    
    private VehiculoDTO(String placa, String marca, String modelo, Integer kilometrajeActual) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.kilometrajeActual = kilometrajeActual;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getKilometrajeActual() {
        return kilometrajeActual;
    }
    
    public static VehiculoDTOBuilder builder(){
        return new VehiculoDTOBuilder();
    }
    
    public static class VehiculoDTOBuilder{
        
        private String placa;
        private String marca;
        private String modelo;
        private Integer kilometrajeActual;
        
        public VehiculoDTOBuilder placa(String placa){
            this.placa = placa;
            return this;
        }
        
        public VehiculoDTOBuilder marca(String marca){
            this.marca = marca;
            return this;
        }
        
        public VehiculoDTOBuilder modelo(String modelo){
            this.modelo = modelo;
            return this;
        }       
        
        public VehiculoDTOBuilder kilometrajeActual(Integer kilometrajeActual){
            this.kilometrajeActual = kilometrajeActual;
            return this;
        }
        
        public VehiculoDTO build(){
            return new VehiculoDTO(placa, marca, modelo, kilometrajeActual);
        }
        
    }
    
    
}
