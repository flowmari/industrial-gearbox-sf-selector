package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.FactorBreakdown;
import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionResult;

import java.util.List;

public record GearboxSelectionResponse(
        double reductionRatio,
        double serviceFactor,
        double designTorqueNm,
        String selectionStatus,
        FactorBreakdown factorBreakdown,
        List<String> selectionReasons,
        List<String> riskNotes,
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
