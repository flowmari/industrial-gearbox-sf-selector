package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;
import com.flowmari.industrialgearboxsfselector.domain.ServiceFactorCalculator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gearbox")
public class GearboxSelectionController {

    private final ServiceFactorCalculator calculator;

    public GearboxSelectionController(ServiceFactorCalculator calculator) {
        this.calculator = calculator;
    }

    @PostMapping("/selection")
    GearboxSelectionResponse calculate(@Valid @RequestBody GearboxSelectionRequest request) {
        GearboxSelectionResult result = calculator.calculate(request.toDomain());
        return GearboxSelectionResponse.from(result);
    }
}
