package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehiculos")
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "proximo_mantenimiento_km")
    private Integer proximoMantenimientoKm;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
