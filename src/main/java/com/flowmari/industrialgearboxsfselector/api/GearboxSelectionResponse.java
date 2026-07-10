package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.FactorBreakdown;
import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic industrial gearbox screening result.")
public record GearboxSelectionResponse(
        @Schema(description = "Calculated reduction ratio.", example = "30.0")
        double reductionRatio,

        @Schema(description = "Calculated generic service factor.", example = "1.67")
        double serviceFactor,

        @Schema(description = "Required design torque after service factor correction.", example = "501.0")
        double designTorqueNm,

        @Schema(description = "Generic screening status.", example = "SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE")
        String selectionStatus,

        @Schema(description = "Breakdown of factors used in the generic screening calculation.")
        FactorBreakdown factorBreakdown,

        @Schema(description = "Human-readable reasons explaining how the result was calculated.")
        List<String> selectionReasons,

        @Schema(description = "Engineering risk notes that should be reviewed against manufacturer documentation.")
        List<String> riskNotes,

        @Schema(description = "Summary diagnosis message.")
        String diagnosis
) {
    static GearboxSelectionResponse from(GearboxSelectionResult result) {
        return new GearboxSelectionResponse(
                result.reductionRatio(),
                result.serviceFactor(),
                result.designTorqueNm(),
                result.selectionStatus(),
                result.factorBreakdown(),
                result.selectionReasons(),
                result.riskNotes(),
                result.diagnosis()
        );
    }
}
