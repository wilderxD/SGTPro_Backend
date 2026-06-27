package com.example.sgtpro.SGTPRO.repository;

import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer>{

    List<OrdenTrabajo> findByEstado(String estado);

    List<OrdenTrabajo> findByVehiculoPlaca(String placa);

    List<OrdenTrabajo> findByEstadoAndVehiculoPlaca(String estado, String placa);

    @Query("SELECT o FROM OrdenTrabajo o WHERE "
            + "(:estado IS NULL OR o.estado = :estado) "
            + "AND (:placa IS NULL OR o.vehiculo.placa = :placa) "
            + "AND (:idMecanico IS NULL OR o.mecanico.idUsuario = :idMecanico) "
            + "AND (:fechaDesde IS NULL OR o.fechaInternamiento >= :fechaDesde) "
            + "AND (:fechaHasta IS NULL OR o.fechaInternamiento <= :fechaHasta)")
    Page<OrdenTrabajo> findFiltered(
            @Param("estado") String estado,
            @Param("placa") String placa,
            @Param("idMecanico") Integer idMecanico,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            Pageable pageable);

    @Query("SELECT o.vehiculo.placa, MAX(o.kilometraje) FROM OrdenTrabajo o "
            + "WHERE o.kilometraje IS NOT NULL AND o.estado IN ('FINALIZADO', 'ENTREGADO') "
            + "GROUP BY o.vehiculo.placa")
    List<Object[]> findMaxKmPorVehiculo();

    @Query("SELECT o.estado, COUNT(o), COALESCE(SUM(o.costoTotal), 0) FROM OrdenTrabajo o GROUP BY o.estado")
    List<Object[]> findStatsGroupedByEstado();

    List<OrdenTrabajo> findTop5ByOrderByFechaInternamientoDesc();

}
