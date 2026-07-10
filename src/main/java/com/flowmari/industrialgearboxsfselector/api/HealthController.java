package com.flowmari.industrialgearboxsfselector.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(
        name = "Health",
        description = "Simple deployment and health check endpoints."
)
@RestController
public class HealthController {

    @Operation(summary = "Root deployment check")
    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "status", "ok",
                "service", "industrial-gearbox-sf-selector",
                "description", "Industrial gearbox service factor and sizing API"
        );
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "service", "industrial-gearbox-sf-selector"
        );
    }
}
