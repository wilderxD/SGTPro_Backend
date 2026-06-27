package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ordenes_trabajo")
@EntityListeners(AuditingEntityListener.class)
public class OrdenTrabajo {

    public static final String ESTADO_EN_REVISION = "EN_REVISION";
    public static final String ESTADO_EN_REPARACION = "EN_REPARACION";
    public static final String ESTADO_FINALIZADO = "FINALIZADO";
    public static final String ESTADO_ENTREGADO = "ENTREGADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ot")
    private Integer idOt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jefe_taller")
    private Usuario jefeTaller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mecanico")
    private Usuario mecanico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placa", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "fecha_internamiento")
    private LocalDateTime fechaInternamiento;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "diagnostico_mecanico", columnDefinition = "TEXT")
    private String diagnosticoMecanico;

    @Column(name = "fallas_reparadas", columnDefinition = "TEXT")
    private String fallasReparadas;

    @Column(name = "kilometraje")
    private Integer kilometraje;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "costo_total", precision = 10, scale = 2)
    private BigDecimal costoTotal = BigDecimal.ZERO;

    @Column(length = 50)
    private String estado = ESTADO_EN_REVISION;

    @ToString.Exclude
    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequerimientoInsumo> requerimientos;

    @ToString.Exclude
    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrabajoOt> trabajos;
}
