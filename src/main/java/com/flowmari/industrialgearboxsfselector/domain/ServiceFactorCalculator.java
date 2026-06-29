package com.flowmari.industrialgearboxsfselector.domain;

public class ServiceFactorCalculator {

    public GearboxSelectionResult calculate(GearboxSelectionInput input) {
        validate(input);

        double reductionRatio = roundToOneDecimal(input.inputRpm() / input.outputRpm());

        double serviceFactor = roundToOneDecimal(1.0
                + loadTypeFactor(input.loadType())
                + operatingHoursFactor(input.operatingHoursPerDay())
                + startsPerHourFactor(input.startsPerHour())
                + shockLevelFactor(input.shockLevel()));

        double designTorqueNm = roundToOneDecimal(input.requiredTorqueNm() * serviceFactor);

        String diagnosis = buildDiagnosis(input, designTorqueNm, serviceFactor);

        return new GearboxSelectionResult(
                reductionRatio,
                serviceFactor,
                designTorqueNm,
                diagnosis
        );
    }

    private void validate(GearboxSelectionInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null.");
        }
        if (input.motorPowerKw() <= 0) {
            throw new IllegalArgumentException("Motor power must be greater than zero.");
        }
        if (input.inputRpm() <= 0) {
            throw new IllegalArgumentException("Input rpm must be greater than zero.");
        }
        if (input.outputRpm() <= 0) {
            throw new IllegalArgumentException("Output rpm must be greater than zero.");
        }
        if (input.requiredTorqueNm() <= 0) {
            throw new IllegalArgumentException("Required torque must be greater than zero.");
        }
        if (input.operatingHoursPerDay() <= 0 || input.operatingHoursPerDay() > 24) {
            throw new IllegalArgumentException("Operating hours per day must be between 0 and 24.");
        }
        if (input.startsPerHour() < 0) {
            throw new IllegalArgumentException("Starts per hour must not be negative.");
        }
        if (input.loadType() == null) {
            throw new IllegalArgumentException("Load type must not be null.");
        }
        if (input.shockLevel() == null) {
            throw new IllegalArgumentException("Shock level must not be null.");
        }
    }

    private double loadTypeFactor(LoadType loadType) {
        return switch (loadType) {
            case LIGHT -> 0.0;
            case MODERATE -> 0.2;
            case HEAVY -> 0.4;
        };
    }

    private double operatingHoursFactor(double hours) {
        if (hours <= 8) {
            return 0.0;
        }
        if (hours <= 16) {
            return 0.2;
        }
        return 0.4;
    }

    private double startsPerHourFactor(int startsPerHour) {
        if (startsPerHour <= 10) {
            return 0.0;
        }
        if (startsPerHour <= 30) {
            return 0.1;
        }
        return 0.2;
    }

    private double shockLevelFactor(ShockLevel shockLevel) {
        return switch (shockLevel) {
            case LOW -> 0.0;
            case MEDIUM -> 0.2;
            case HIGH -> 0.4;
        };
    }

    private String buildDiagnosis(
            GearboxSelectionInput input,
            double designTorqueNm,
            double serviceFactor
    ) {
        String message = "The required design torque is %.1f Nm. Select a reducer rated for at least %.1f Nm. The calculated service factor is %.1f based on load type, operating hours, starts per hour, and shock level."
                .formatted(designTorqueNm, designTorqueNm, serviceFactor);

        if (input.loadType() == LoadType.HEAVY || input.shockLevel() == ShockLevel.HIGH) {
            message += " Heavy load or high shock conditions require careful reducer sizing.";
        }

        if (input.startsPerHour() > 30) {
            message += " Frequent start-stop operation may require an additional safety margin.";
        }

        return message;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
