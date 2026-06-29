package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;

public record GearboxSelectionResponse(
        double reductionRatio,
        double serviceFactor,
        double designTorqueNm,
        String selectionStatus,
        String diagnosis
) {
    static GearboxSelectionResponse from(GearboxSelectionResult result) {
        return new GearboxSelectionResponse(
                result.reductionRatio(),
                result.serviceFactor(),
                result.designTorqueNm(),
                "SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE",
                result.diagnosis()
        );
    }
}
