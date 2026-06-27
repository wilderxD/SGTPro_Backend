package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByDestinatarioIdUsuarioOrderByCreatedAtDesc(Integer idUsuario);

    List<Notificacion> findByDestinatarioIdUsuarioAndLeidaFalseOrderByCreatedAtDesc(Integer idUsuario);
}
