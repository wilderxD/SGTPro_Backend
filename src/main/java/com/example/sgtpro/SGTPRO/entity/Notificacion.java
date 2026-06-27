package com.example.sgtpro.SGTPRO.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "ruta", length = 255)
    private String ruta;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destino", nullable = false)
    private Usuario destinatario;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Notificacion(String mensaje, String tipo, String ruta, Usuario destinatario) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.ruta = ruta;
        this.destinatario = destinatario;
        this.leida = false;
        this.createdAt = LocalDateTime.now();
    }
}
