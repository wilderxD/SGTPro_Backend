package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.entity.Notificacion;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para obtener notificaciones del sistema")
public class NotificationController {

    private final NotificacionService notificacionService;

    public NotificationController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(summary = "Obtener notificaciones", description = "Retorta notificaciones del usuario autenticado.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<NotificationItem>> getNotifications() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<Notificacion> notificaciones = notificacionService.listarPorUsuario(usuario.getIdUsuario());
        List<NotificationItem> items = notificaciones.stream()
                .map(n -> new NotificationItem(
                        n.getIdNotificacion(), n.getTipo(), n.getMensaje(),
                        n.getRuta(), n.getLeida(), n.getCreatedAt().toString()))
                .toList();

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación como leída para el usuario autenticado.")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Integer id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        notificacionService.marcarComoLeida(id, usuario.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones del usuario como leídas.")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        notificacionService.marcarTodasComoLeidas(usuario.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    public static class NotificationItem {
        private Integer id;
        private String type;
        private String message;
        private String route;
        private boolean read;
        private String createdAt;

        public NotificationItem() {}

        public NotificationItem(Integer id, String type, String message, String route, boolean read, String createdAt) {
            this.id = id;
            this.type = type;
            this.message = message;
            this.route = route;
            this.read = read;
            this.createdAt = createdAt;
        }

        public Integer getId() { return id; }
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getRoute() { return route; }
        public boolean isRead() { return read; }
        public String getCreatedAt() { return createdAt; }
    }
}
