package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.OrdenTrabajoDTO;
import com.example.sgtpro.SGTPRO.dto.RequerimientoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.RequerimientoInsumo;
import com.example.sgtpro.SGTPRO.mapper.OrdenTrabajoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.RequerimientoInsumoRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrdenTrabajoService implements IOrdenTrabajoService{
    
    private final OrdenTrabajoRepository ordenRepository;
    private final CatalogoInsumoRepository insumoRepository;
    private final RequerimientoInsumoRepository requerimientoRepository;
    private final OrdenTrabajoMapper ordenMapper;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenRepository, CatalogoInsumoRepository insumoRepository, RequerimientoInsumoRepository requerimientoRepository, OrdenTrabajoMapper ordenMapper) {
        this.ordenRepository = ordenRepository;
        this.insumoRepository = insumoRepository;
        this.requerimientoRepository = requerimientoRepository;
        this.ordenMapper = ordenMapper;
    }
      
    //creamos una nueva orden de trabajo emitida solo por el jefe del taller
    @Transactional
    @Override
    public OrdenTrabajoDTO crearOrden(OrdenTrabajoDTO dto) {
        OrdenTrabajo ot = new OrdenTrabajo();        
        ot.setIdJefeTaller(dto.getIdJefeTaller());
        ot.setIdMecanico(dto.getIdMecanico());
        ot.setFechaInternamiento(LocalDateTime.now());
        ot.setEstado("EN_REVISION");
        ot.setCostoTotal(BigDecimal.ZERO);
        
        return ordenMapper.toDTO(ordenRepository.save(ot));
    }

    //El mecanico pide un repuesto o insumo(aun no suma costo)
    @Transactional
    @Override
    public OrdenTrabajoDTO solicitarInsumo(Integer idOt, RequerimientoInsumoDTO solicitud) {
        //Early Returns: validamos que los items a buscar existan
        OrdenTrabajo ot = ordenRepository.findById(idOt).orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada.!"));
        CatalogoInsumo insumo = insumoRepository.findById(solicitud.getIdInsumo()).orElseThrow(() -> new RuntimeException("El insumo solicitado no existe en el catálogo"));
        
        RequerimientoInsumo requerimiento = new RequerimientoInsumo();
        requerimiento.setOrdenTrabajo(ot);
        requerimiento.setInsumo(insumo);
        requerimiento.setCantidadSolicitada(solicitud.getCantidadSolicitada());
        requerimiento.setCantidadEntregada(BigDecimal.ZERO);
        requerimiento.setSubtotal(BigDecimal.ZERO); 
        
        requerimientoRepository.save(requerimiento);
        
        //Refrescamos la Orden de trabajo para devolverla con el nuevo repuesto en la lista
        return ordenMapper.toDTO(ordenRepository.findById(idOt).get());        
    }

    //Logistica despacha el insumo
    @Override
    public OrdenTrabajoDTO despacharInsumo(Integer idRequerimiento, BigDecimal cantidadEntregada) {
        RequerimientoInsumo req = requerimientoRepository.findById(idRequerimiento).orElseThrow(() -> new RuntimeException("Requerimientono encontrado.!"));
        
        OrdenTrabajo ot = req.getOrdenTrabajo();
        CatalogoInsumo insumo = req.getInsumo();
        
        //actualizamos lo entregado
        req.setCantidadEntregada(cantidadEntregada);
        
        //calculamos el subtotal
        BigDecimal subtotal = cantidadEntregada.multiply(insumo.getCostoUnitario());
        req.setSubtotal(subtotal);
        
        //sumamos este subtotal al costo de la orden de trabajo
        BigDecimal nuevoCostoTotal = ot.getCostoTotal().add(subtotal);
        ot.setCostoTotal(nuevoCostoTotal);
        ot.setEstado("EN_REPARACION");
        
        requerimientoRepository.save(req);
        ordenRepository.save(ot);
        
        return ordenMapper.toDTO(ot);        
    }
    
}
