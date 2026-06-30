package com.flowmari.industrialgearboxsfselector.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "status", "ok",
                "service", "industrial-gearbox-sf-selector",
                "description", "Industrial gearbox service factor and sizing API"
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "service", "industrial-gearbox-sf-selector"
        );
    }
}
