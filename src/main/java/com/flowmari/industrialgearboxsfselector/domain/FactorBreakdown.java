package com.flowmari.industrialgearboxsfselector.domain;

public record FactorBreakdown(
        double loadFactor,
        double dutyCycleFactor,
        double startStopFactor,
        double shockFactor,
        double ambientTemperatureFactor
) {
}
