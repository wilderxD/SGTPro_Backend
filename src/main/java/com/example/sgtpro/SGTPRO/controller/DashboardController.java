package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.mapper.OrdenTrabajoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints de estadísticas para el dashboard")
public class DashboardController {

    private final OrdenTrabajoRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoInsumoRepository insumoRepository;
    private final OrdenTrabajoMapper ordenMapper;

    public DashboardController(OrdenTrabajoRepository ordenRepository,
                               VehiculoRepository vehiculoRepository,
                               UsuarioRepository usuarioRepository,
                               CatalogoInsumoRepository insumoRepository,
                               OrdenTrabajoMapper ordenMapper) {
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.insumoRepository = insumoRepository;
        this.ordenMapper = ordenMapper;
    }

    @Operation(summary = "Estadísticas del dashboard", description = "Retorta estadísticas resumidas para el dashboard principal.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        List<Object[]> stats = ordenRepository.findStatsGroupedByEstado();

        long revision = 0, reparacion = 0, finalizadas = 0, canceladas = 0, entregadas = 0;
        BigDecimal costoTotal = BigDecimal.ZERO;
        long totalOt = 0;

        for (Object[] row : stats) {
            String estado = (String) row[0];
            long count = (long) row[1];
            BigDecimal sumCosto = (BigDecimal) row[2];

            totalOt += count;
            costoTotal = costoTotal.add(sumCosto != null ? sumCosto : BigDecimal.ZERO);

            switch (estado) {
                case OrdenTrabajo.ESTADO_EN_REVISION -> revision = count;
                case OrdenTrabajo.ESTADO_EN_REPARACION -> reparacion = count;
                case OrdenTrabajo.ESTADO_FINALIZADO -> finalizadas = count;
                case OrdenTrabajo.ESTADO_CANCELADO -> canceladas = count;
                case OrdenTrabajo.ESTADO_ENTREGADO -> entregadas = count;
            }
        }

        long vehicleCount = vehiculoRepository.count();
        long userCount = usuarioRepository.count();
        long insumoCount = insumoRepository.count();

        long lowStock = insumoRepository.findByStockLessThanEqual(BigDecimal.valueOf(5)).size();

        return ResponseEntity.ok(new DashboardStats(
                totalOt, revision, reparacion, finalizadas, canceladas, entregadas,
                costoTotal, vehicleCount, userCount, insumoCount, lowStock));
    }

    @Operation(summary = "Órdenes recientes", description = "Retorta las 5 órdenes de trabajo más recientes.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recientes")
    public ResponseEntity<List<OrdenTrabajoDTO>> getRecientes() {
        return ResponseEntity.ok(
                ordenRepository.findTop5ByOrderByFechaInternamientoDesc()
                        .stream()
                        .map(ordenMapper::toDTO)
                        .toList());
    }

    public static class DashboardStats {
        private long totalOt;
        private long enRevision;
        private long enReparacion;
        private long finalizadas;
        private long canceladas;
        private long entregadas;
        private BigDecimal costoTotal;
        private long totalVehiculos;
        private long totalUsuarios;
        private long totalInsumos;
        private long lowStock;

        public DashboardStats() {}

        public DashboardStats(long totalOt, long enRevision, long enReparacion, long finalizadas,
                              long canceladas, long entregadas, BigDecimal costoTotal,
                              long totalVehiculos, long totalUsuarios, long totalInsumos, long lowStock) {
            this.totalOt = totalOt;
            this.enRevision = enRevision;
            this.enReparacion = enReparacion;
            this.finalizadas = finalizadas;
            this.canceladas = canceladas;
            this.entregadas = entregadas;
            this.costoTotal = costoTotal;
            this.totalVehiculos = totalVehiculos;
            this.totalUsuarios = totalUsuarios;
            this.totalInsumos = totalInsumos;
            this.lowStock = lowStock;
        }

        public long getTotalOt() { return totalOt; }
        public long getEnRevision() { return enRevision; }
        public long getEnReparacion() { return enReparacion; }
        public long getFinalizadas() { return finalizadas; }
        public long getCanceladas() { return canceladas; }
        public long getEntregadas() { return entregadas; }
        public BigDecimal getCostoTotal() { return costoTotal; }
        public long getTotalVehiculos() { return totalVehiculos; }
        public long getTotalUsuarios() { return totalUsuarios; }
        public long getTotalInsumos() { return totalInsumos; }
        public long getLowStock() { return lowStock; }
    }
}
