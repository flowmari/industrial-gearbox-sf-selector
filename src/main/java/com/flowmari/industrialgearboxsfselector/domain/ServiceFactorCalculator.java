package com.flowmari.industrialgearboxsfselector.domain;

import java.util.ArrayList;
import java.util.List;

public class ServiceFactorCalculator {

    private static final String SCREENING_OK_STATUS = "SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE";
    private static final String ENGINEERING_REVIEW_STATUS = "SCREENING_REQUIRES_ENGINEERING_REVIEW";

    public GearboxSelectionResult calculate(GearboxSelectionInput input) {
        validate(input);

        double reductionRatio = roundToOneDecimal(input.inputRpm() / input.outputRpm());

        FactorBreakdown factorBreakdown = new FactorBreakdown(
                loadFactor(input.loadType()),
                dutyCycleFactor(input.operatingHoursPerDay()),
                startStopFactor(input.startsPerHour()),
                shockFactor(input.shockLevel()),
                ambientTemperatureFactor(input.ambientTemperatureC())
        );

        double serviceFactor = roundToTwoDecimals(
                factorBreakdown.loadFactor()
                        * factorBreakdown.dutyCycleFactor()
                        * factorBreakdown.startStopFactor()
                        * factorBreakdown.shockFactor()
                        * factorBreakdown.ambientTemperatureFactor()
        );

        double designTorqueNm = roundToOneDecimal(input.requiredTorqueNm() * serviceFactor);

        List<String> riskNotes = buildRiskNotes(input);
        List<String> engineeringReviewChecklist = buildEngineeringReviewChecklist(input);
        String selectionStatus = riskNotes.isEmpty() ? SCREENING_OK_STATUS : ENGINEERING_REVIEW_STATUS;
        List<String> selectionReasons = buildSelectionReasons(input, reductionRatio, serviceFactor, designTorqueNm, factorBreakdown);
        String diagnosis = buildDiagnosis(serviceFactor, designTorqueNm, riskNotes);

        return new GearboxSelectionResult(
                reductionRatio,
                serviceFactor,
                designTorqueNm,
                selectionStatus,
                factorBreakdown,
                selectionReasons,
                riskNotes,
                engineeringReviewChecklist,
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
        if (input.ambientTemperatureC() < -20 || input.ambientTemperatureC() > 80) {
            throw new IllegalArgumentException("Ambient temperature must be between -20 and 80 degrees Celsius.");
        }
        if (input.loadType() == null) {
            throw new IllegalArgumentException("Load type must not be null.");
        }
        if (input.shockLevel() == null) {
            throw new IllegalArgumentException("Shock level must not be null.");
        }
    }

    private double loadFactor(LoadType loadType) {
        return switch (loadType) {
            case LIGHT -> 1.00;
            case MODERATE -> 1.15;
            case HEAVY -> 1.30;
        };
    }

    private double dutyCycleFactor(double hours) {
        if (hours <= 8) {
            return 1.00;
        }
        if (hours <= 16) {
            return 1.15;
        }
        return 1.30;
    }

    private double startStopFactor(int startsPerHour) {
        if (startsPerHour <= 10) {
            return 1.00;
        }
        if (startsPerHour <= 30) {
            return 1.10;
        }
        return 1.20;
    }

    private double shockFactor(ShockLevel shockLevel) {
        return switch (shockLevel) {
            case LOW -> 1.00;
            case MEDIUM -> 1.15;
            case HIGH -> 1.30;
        };
    }

    private double ambientTemperatureFactor(double ambientTemperatureC) {
        if (ambientTemperatureC <= 40) {
            return 1.00;
        }
        if (ambientTemperatureC <= 50) {
            return 1.10;
        }
        return 1.20;
    }

    private List<String> buildSelectionReasons(
            GearboxSelectionInput input,
            double reductionRatio,
            double serviceFactor,
            double designTorqueNm,
            FactorBreakdown factorBreakdown
    ) {
        return List.of(
                "Reduction ratio was calculated from input rpm and output rpm: %.1f.".formatted(reductionRatio),
                "Load factor %.2f was applied for %s load.".formatted(factorBreakdown.loadFactor(), input.loadType()),
                "Duty-cycle factor %.2f was applied for %.1f operating hours per day.".formatted(factorBreakdown.dutyCycleFactor(), input.operatingHoursPerDay()),
                "Start-stop factor %.2f was applied for %d starts per hour.".formatted(factorBreakdown.startStopFactor(), input.startsPerHour()),
                "Shock factor %.2f was applied for %s shock level.".formatted(factorBreakdown.shockFactor(), input.shockLevel()),
                "Ambient-temperature factor %.2f was applied for %.1f °C.".formatted(factorBreakdown.ambientTemperatureFactor(), input.ambientTemperatureC()),
                "The resulting generic service factor is %.2f, so the reducer should be rated for at least %.1f Nm.".formatted(serviceFactor, designTorqueNm)
        );
    }

    private List<String> buildRiskNotes(GearboxSelectionInput input) {
        List<String> riskNotes = new ArrayList<>();

        if (input.ambientTemperatureC() > 50) {
            riskNotes.add("Ambient temperature is above 50 °C; verify thermal limits, lubrication, and derating with manufacturer documentation.");
        } else if (input.ambientTemperatureC() > 40) {
            riskNotes.add("Ambient temperature is above 40 °C; check manufacturer thermal rating and lubricant recommendations.");
        }

        if (input.operatingHoursPerDay() > 16) {
            riskNotes.add("Long daily operating hours may require thermal and bearing-life checks.");
        }

        if (input.startsPerHour() > 30) {
            riskNotes.add("Frequent start-stop operation may require an additional safety margin.");
        }

        if (input.loadType() == LoadType.HEAVY || input.shockLevel() == ShockLevel.HIGH) {
            riskNotes.add("Heavy load or high shock conditions may require an additional safety margin and manufacturer review.");
        }

        return riskNotes;
    }

    private List<String> buildEngineeringReviewChecklist(GearboxSelectionInput input) {
        List<String> checklist = new ArrayList<>();

        checklist.add("Verify the final reducer rating, service factor, and application conditions against official manufacturer documentation.");
        checklist.add("Confirm mounting position, shaft orientation, and installation constraints before final selection.");
        checklist.add("Confirm coupling, motor, and driven-machine interfaces separately before final selection.");

        if (input.ambientTemperatureC() > 40) {
            checklist.add("Review ambient-temperature derating, thermal rating, and lubricant recommendation for the installation environment.");
        }

        if (input.operatingHoursPerDay() > 16) {
            checklist.add("Review thermal capacity, lubricant recommendations, and bearing-life assumptions for long daily operating hours.");
        }

        if (input.startsPerHour() > 30) {
            checklist.add("Review start-stop duty, motor starting behavior, and coupling compatibility for frequent cycling.");
        }

        if (input.loadType() == LoadType.HEAVY || input.shockLevel() == ShockLevel.HIGH) {
            checklist.add("Review shock loading, shaft loads, and application-specific safety margin separately.");
        }

        return checklist;
    }

    private String buildDiagnosis(double serviceFactor, double designTorqueNm, List<String> riskNotes) {
        String diagnosis = "Generic screening result: service factor %.2f gives a design torque of %.1f Nm. Select a reducer rated for at least this design torque, then verify the final selection against manufacturer documentation."
                .formatted(serviceFactor, designTorqueNm);

        if (!riskNotes.isEmpty()) {
            diagnosis += " Additional engineering review is recommended because risk notes were detected.";
        }

        return diagnosis;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
