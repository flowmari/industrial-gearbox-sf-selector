package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.FactorBreakdown;
import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;
import com.flowmari.industrialgearboxsfselector.domain.PowerFeasibilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic industrial gearbox screening result.")
public record GearboxSelectionResponse(
        @Schema(description = "Version identifier for the generic calculation model.", example = "generic-screening-v1.1")
        String calculationModelVersion,

        @Schema(description = "Calculated reduction ratio.", example = "30.0")
        double reductionRatio,

        @Schema(description = "Calculated generic service factor.", example = "1.67")
        double serviceFactor,

        @Schema(description = "Required design torque after service factor correction.", example = "501.0")
        double designTorqueNm,

        @Schema(description = "Required mechanical output power calculated from required torque and output speed, in kW.", example = "1.57")
        double requiredOutputPowerKw,

        @Schema(description = "Minimum overall efficiency ratio required for the supplied motor power to satisfy the requested output point. The status is evaluated from the unrounded value.", example = "0.7140")
        double minimumRequiredOverallEfficiency,

        @Schema(description = "Bounded power-feasibility status without assuming a manufacturer-specific drivetrain efficiency.", example = "VERIFY_ACTUAL_EFFICIENCY_AND_MOTOR_DUTY")
        PowerFeasibilityStatus powerFeasibilityStatus,

        @Schema(description = "Generic screening status.", example = "SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE")
        String selectionStatus,

        @Schema(description = "Breakdown of factors used in the generic screening calculation.")
        FactorBreakdown factorBreakdown,

        @Schema(description = "Human-readable reasons explaining how the result was calculated.")
        List<String> selectionReasons,

        @Schema(description = "Engineering risk notes that should be reviewed against manufacturer documentation.")
        List<String> riskNotes,

        @Schema(description = "Generic engineering checklist for final review boundaries before reducer selection is finalized.")
        List<String> engineeringReviewChecklist,

        @Schema(description = "Summary diagnosis message.")
        String diagnosis
) {
    static GearboxSelectionResponse from(GearboxSelectionResult result) {
        return new GearboxSelectionResponse(
                result.calculationModelVersion(),
                result.reductionRatio(),
                result.serviceFactor(),
                result.designTorqueNm(),
                result.requiredOutputPowerKw(),
                result.minimumRequiredOverallEfficiency(),
                result.powerFeasibilityStatus(),
                result.selectionStatus(),
                result.factorBreakdown(),
                result.selectionReasons(),
                result.riskNotes(),
                result.engineeringReviewChecklist(),
                result.diagnosis()
        );
    }
}
