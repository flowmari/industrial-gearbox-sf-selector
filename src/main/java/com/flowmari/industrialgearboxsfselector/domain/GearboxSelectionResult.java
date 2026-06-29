package com.flowmari.industrialgearboxsfselector.domain;

public record GearboxSelectionResult(
        double reductionRatio,
        double serviceFactor,
        double designTorqueNm,
        String diagnosis
) {
}
