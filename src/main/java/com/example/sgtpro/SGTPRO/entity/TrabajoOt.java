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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trabajo_ot")
public class TrabajoOt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajo")
    private Integer idTrabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ot", nullable = false)
    private OrdenTrabajo ordenTrabajo;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean completado = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
