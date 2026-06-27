package com.example.sgtpro.SGTPRO.controller;

import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.entity.OrdenTrabajo;
import com.example.sgtpro.SGTPRO.entity.Vehiculo;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import com.example.sgtpro.SGTPRO.repository.OrdenTrabajoRepository;
import com.example.sgtpro.SGTPRO.repository.VehiculoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Exportar", description = "Endpoints para exportar datos en formato CSV")
public class ExportController {

    private final OrdenTrabajoRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;
    private final CatalogoInsumoRepository insumoRepository;

    public ExportController(OrdenTrabajoRepository ordenRepository,
                            VehiculoRepository vehiculoRepository,
                            CatalogoInsumoRepository insumoRepository) {
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.insumoRepository = insumoRepository;
    }

    @Operation(summary = "Exportar órdenes de trabajo a CSV")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ordenes")
    public ResponseEntity<byte[]> exportOrdenes() {
        List<OrdenTrabajo> all = ordenRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder("ID OT,Placa,Km,Estado,Fecha Ingreso,Fecha Salida,Diagnóstico,Costo Total\n");
        for (OrdenTrabajo o : all) {
            sb.append(o.getIdOt()).append(",")
                    .append(escapeCsv(o.getVehiculo() != null ? o.getVehiculo().getPlaca() : "")).append(",")
                    .append(o.getKilometraje() != null ? o.getKilometraje() : "").append(",")
                    .append(o.getEstado()).append(",")
                    .append(o.getFechaInternamiento() != null ? o.getFechaInternamiento().format(fmt) : "").append(",")
                    .append(o.getFechaSalida() != null ? o.getFechaSalida().format(fmt) : "").append(",")
                    .append(escapeCsv(o.getDiagnosticoMecanico())).append(",")
                    .append(o.getCostoTotal()).append("\n");
        }

        return csvResponse(sb.toString(), "ordenes-trabajo.csv");
    }

    @Operation(summary = "Exportar vehículos a CSV")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehiculos")
    public ResponseEntity<byte[]> exportVehiculos() {
        List<Vehiculo> all = vehiculoRepository.findAll();

        StringBuilder sb = new StringBuilder("Placa,Marca,Modelo,Kilometraje Actual\n");
        for (Vehiculo v : all) {
            sb.append(v.getPlaca()).append(",")
                    .append(escapeCsv(v.getMarca())).append(",")
                    .append(escapeCsv(v.getModelo())).append(",")
                    .append(v.getKilometrajeActual()).append("\n");
        }

        return csvResponse(sb.toString(), "vehiculos.csv");
    }

    @Operation(summary = "Exportar catálogo de insumos a CSV")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/insumos")
    public ResponseEntity<byte[]> exportInsumos() {
        List<CatalogoInsumo> all = insumoRepository.findAll();

        StringBuilder sb = new StringBuilder("Código,Nombre,Unidad,Costo Unit.,Stock\n");
        for (CatalogoInsumo i : all) {
            sb.append(escapeCsv(i.getCodigoInterno())).append(",")
                    .append(escapeCsv(i.getNombre())).append(",")
                    .append(escapeCsv(i.getUnidadMedida())).append(",")
                    .append(i.getCostoUnitario()).append(",")
                    .append(i.getStock()).append("\n");
        }

        return csvResponse(sb.toString(), "catalogo-insumos.csv");
    }

    private ResponseEntity<byte[]> csvResponse(String csv, String filename) {
        byte[] bytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
