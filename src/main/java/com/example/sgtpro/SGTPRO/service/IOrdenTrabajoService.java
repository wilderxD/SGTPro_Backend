package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrdenTrabajoService {

    OrdenTrabajoDTO crearOrden(OrdenTrabajoDTO dto);

    OrdenTrabajoDTO solicitarInsumo(Integer idOt, RequerimientoInsumoDTO solicitud);

    OrdenTrabajoDTO despacharInsumo(Integer idRequerimiento, BigDecimal cantidadEntregada);

    List<OrdenTrabajoDTO> listarOrdenes(String estado, String placa);

    Page<OrdenTrabajoDTO> listarOrdenesPaginado(
            String estado, String placa, Integer idMecanico,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            Pageable pageable);

    OrdenTrabajoDTO buscarPorId(Integer idOt);

    OrdenTrabajoDTO finalizarOrden(Integer idOt);

    OrdenTrabajoDTO cancelarOrden(Integer idOt);

    OrdenTrabajoDTO entregarOrden(Integer idOt);
    
}
