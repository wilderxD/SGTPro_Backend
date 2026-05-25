package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    
    @Id
    @Column(name = "placa", length = 7)
    private String placa;
    
    @Column(name = "marca")
    private String marca;
    
    @Column(name = "modelo")
    private String modelo;
    
    @Column(name = "kilometraje_actual", nullable = false)
    private Integer kilometrajeActual;

    public Vehiculo() {
    }

    public Vehiculo(String placa, String marca, String modelo, Integer kilometrajeActual) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.kilometrajeActual = kilometrajeActual;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getKilometrajeActual() {
        return kilometrajeActual;
    }

    public void setKilometrajeActual(Integer kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }
    
}
