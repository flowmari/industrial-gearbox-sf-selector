package com.flowmari.industrialgearboxsfselector.domain;

import java.util.List;

public record GearboxSelectionResult(
        String calculationModelVersion,
        double reductionRatio,
        double serviceFactor,
        double designTorqueNm,
        double requiredOutputPowerKw,
        double minimumRequiredOverallEfficiency,
        PowerFeasibilityStatus powerFeasibilityStatus,
        String selectionStatus,
        FactorBreakdown factorBreakdown,
        List<String> selectionReasons,
        List<String> riskNotes,
        List<String> engineeringReviewChecklist,
        String diagnosis
) {
    public GearboxSelectionResult {
        selectionReasons = List.copyOf(selectionReasons);
        riskNotes = List.copyOf(riskNotes);
        engineeringReviewChecklist = List.copyOf(engineeringReviewChecklist);
    }
}
