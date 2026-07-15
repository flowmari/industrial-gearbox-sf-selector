package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.domain.GearboxSelectionInput;
import com.flowmari.industrialgearboxsfselector.domain.LoadType;
import com.flowmari.industrialgearboxsfselector.domain.ShockLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Input conditions for generic industrial gearbox screening.")
public record GearboxSelectionRequest(
        @Schema(description = "Motor rated mechanical output power at the gearbox input, in kilowatts.", example = "2.2")
        @NotNull @Positive Double motorPowerKw,

        @Schema(description = "Input speed in rpm.", example = "1500")
        @NotNull @Positive Double inputRpm,

        @Schema(description = "Required output speed in rpm.", example = "50")
        @NotNull @Positive Double outputRpm,

        @Schema(description = "Required output torque in Nm before service factor correction.", example = "300")
        @NotNull @Positive Double requiredTorqueNm,

        @Schema(description = "Generic load category.", example = "MODERATE")
        @NotNull LoadType loadType,

        @Schema(description = "Operating hours per day. Accepted range: greater than 0 and up to 24.", example = "12")
        @NotNull @Positive @DecimalMax("24.0") Double operatingHoursPerDay,

        @Schema(description = "Number of starts per hour.", example = "20")
        @NotNull @PositiveOrZero Integer startsPerHour,

        @Schema(description = "Generic shock level.", example = "MEDIUM")
        @NotNull ShockLevel shockLevel,

        @Schema(description = "Ambient temperature in Celsius. Accepted range: -20 to 80.", example = "35")
        @NotNull @DecimalMin("-20.0") @DecimalMax("80.0") Double ambientTemperatureC
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
                shockLevel,
                ambientTemperatureC
        );
    }
}
