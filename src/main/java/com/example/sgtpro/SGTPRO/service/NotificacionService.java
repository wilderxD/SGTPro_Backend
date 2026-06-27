package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.entity.Notificacion;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.repository.NotificacionRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                                UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void crearParaUsuario(String mensaje, String tipo, String ruta, Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) return;
        notificacionRepository.save(new Notificacion(mensaje, tipo, ruta, usuario));
    }

    @Transactional
    public void crearParaRol(String mensaje, String tipo, String ruta, String nombreRol) {
        List<Usuario> usuarios = usuarioRepository.findByRolNombre(nombreRol);
        for (Usuario usuario : usuarios) {
            notificacionRepository.save(new Notificacion(mensaje, tipo, ruta, usuario));
        }
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarPorUsuario(Integer idUsuario) {
        return notificacionRepository.findByDestinatarioIdUsuarioOrderByCreatedAtDesc(idUsuario);
    }

    @Transactional
    public void marcarComoLeida(Integer idNotificacion, Integer idUsuario) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion).orElse(null);
        if (notificacion != null && notificacion.getDestinatario().getIdUsuario().equals(idUsuario)) {
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
        }
    }

    @Transactional
    public void marcarTodasComoLeidas(Integer idUsuario) {
        List<Notificacion> noLeidas = notificacionRepository
                .findByDestinatarioIdUsuarioAndLeidaFalseOrderByCreatedAtDesc(idUsuario);
        for (Notificacion n : noLeidas) {
            n.setLeida(true);
        }
        notificacionRepository.saveAll(noLeidas);
    }
}
