package com.flowmari.industrialgearboxsfselector.domain;

public record GearboxSelectionInput(
        double motorPowerKw,
        double inputRpm,
        double outputRpm,
        double requiredTorqueNm,
        LoadType loadType,
        double operatingHoursPerDay,
        int startsPerHour,
        ShockLevel shockLevel
) {
}
