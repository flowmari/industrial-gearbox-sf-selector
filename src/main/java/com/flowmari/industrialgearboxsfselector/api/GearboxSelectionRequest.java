package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionInput;
import com.flowmari.industrialgearboxsfselector.domain.LoadType;
import com.flowmari.industrialgearboxsfselector.domain.ShockLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record GearboxSelectionRequest(
        @NotNull @Positive Double motorPowerKw,
        @NotNull @Positive Double inputRpm,
        @NotNull @Positive Double outputRpm,
        @NotNull @Positive Double requiredTorqueNm,
        @NotNull LoadType loadType,
        @NotNull @Positive @DecimalMax("24.0") Double operatingHoursPerDay,
        @NotNull @PositiveOrZero Integer startsPerHour,
        @NotNull ShockLevel shockLevel
) {
    GearboxSelectionInput toDomain() {
        return new GearboxSelectionInput(
                motorPowerKw,
                inputRpm,
                outputRpm,
                requiredTorqueNm,
                loadType,
                operatingHoursPerDay,
                startsPerHour,
                shockLevel
        );
    }
}
