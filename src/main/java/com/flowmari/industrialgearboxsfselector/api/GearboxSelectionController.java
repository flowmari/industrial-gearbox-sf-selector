package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;
import com.flowmari.industrialgearboxsfselector.domain.ServiceFactorCalculator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Gearbox Screening",
        description = "Generic service factor screening and reducer sizing diagnostics."
)
@RestController
@RequestMapping("/api/gearbox")
public class GearboxSelectionController {

    private final ServiceFactorCalculator calculator;

    public GearboxSelectionController(ServiceFactorCalculator calculator) {
        this.calculator = calculator;
    }

    @Operation(
            summary = "Calculate generic gearbox screening result",
            description = """
                    Calculates reduction ratio, generic service factor, design torque,
                    factor breakdown, selection reasons, risk notes, and screening status
                    from industrial gearbox operating conditions.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Generic gearbox screening result returned."),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation error.")
    })
    @PostMapping("/selection")
    public GearboxSelectionResponse calculate(@Valid @RequestBody GearboxSelectionRequest request) {
        GearboxSelectionResult result = calculator.calculate(request.toDomain());
        return GearboxSelectionResponse.from(result);
    }
}
