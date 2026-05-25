package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import java.math.BigDecimal;

public interface IOrdenTrabajoService {
    public OrdenTrabajoDTO crearOrden(OrdenTrabajoDTO dto);
    public OrdenTrabajoDTO solicitarInsumo(Integer idOt, RequerimientoInsumoDTO solicitud);
    public OrdenTrabajoDTO despacharInsumo(Integer idRequerimiento, BigDecimal cantidadEntregada);
    
}
