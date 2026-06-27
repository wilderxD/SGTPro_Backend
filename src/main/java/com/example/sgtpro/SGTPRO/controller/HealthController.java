package com.example.sgtpro.SGTPRO.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Endpoint de salud del sistema")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Operation(summary = "Health check", description = "Verifica el estado del servicio y la conexión a la BD.")
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean dbOk = false;
        try (var conn = dataSource.getConnection()) {
            dbOk = conn.isValid(2);
        } catch (Exception e) {
            dbOk = false;
        }

        var status = dbOk ? "UP" : "DEGRADED";
        int httpStatus = dbOk ? 200 : 503;

        return ResponseEntity.status(httpStatus).body(Map.of(
                "status", status,
                "timestamp", LocalDateTime.now().toString(),
                "database", dbOk ? "connected" : "disconnected"
        ));
    }
}
