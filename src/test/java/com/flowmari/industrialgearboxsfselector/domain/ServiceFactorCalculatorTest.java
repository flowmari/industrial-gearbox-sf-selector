package com.flowmari.industrialgearboxsfselector.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceFactorCalculatorTest {

    private final ServiceFactorCalculator calculator = new ServiceFactorCalculator();

    @Test
    void calculatesMultiplicativeServiceFactorDesignTorqueAndPowerFeasibility() {
        GearboxSelectionInput input = new GearboxSelectionInput(
                2.2,
                1500,
                50,
                300,
                LoadType.MODERATE,
                12,
                20,
                ShockLevel.MEDIUM,
                35
        );

        GearboxSelectionResult result = calculator.calculate(input);

        assertThat(result.calculationModelVersion()).isEqualTo("generic-screening-v1.1");
        assertThat(result.reductionRatio()).isEqualTo(30.0);
        assertThat(result.serviceFactor()).isEqualTo(1.67);
        assertThat(result.designTorqueNm()).isEqualTo(501.0);
        assertThat(result.requiredOutputPowerKw()).isEqualTo(1.57);
        assertThat(result.minimumRequiredOverallEfficiency()).isEqualTo(0.714);
        assertThat(result.powerFeasibilityStatus()).isEqualTo(PowerFeasibilityStatus.VERIFY_ACTUAL_EFFICIENCY_AND_MOTOR_DUTY);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE");
        assertThat(result.factorBreakdown().loadFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().dutyCycleFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().startStopFactor()).isEqualTo(1.10);
        assertThat(result.factorBreakdown().shockFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.00);
        assertThat(result.selectionReasons()).contains(
                "Reduction ratio was calculated from input rpm and output rpm: 30.0.",
                "The resulting generic service factor is 1.67, so the reducer should be rated for at least 501.0 Nm.",
                "Required mechanical output power was calculated from required torque and output speed: 1.57 kW."
        );
        assertThat(result.riskNotes()).isEmpty();
        assertThat(result.engineeringReviewChecklist()).contains(
                "Verify the final reducer rating, service factor, and application conditions against official manufacturer documentation.",
                "Confirm mounting position, shaft orientation, and installation constraints before final selection.",
                "Confirm coupling, motor, and driven-machine interfaces separately before final selection.",
                "Verify actual overall drivetrain efficiency and motor duty against manufacturer data before final selection."
        );
        assertThat(result.diagnosis()).contains("minimum overall efficiency ratio of 0.7140");
    }

    @Test
    void addsRiskNotesWhenOperatingConditionsRequireEngineeringReview() {
        GearboxSelectionInput input = new GearboxSelectionInput(
                2.2,
                1500,
                50,
                300,
                LoadType.HEAVY,
                20,
                45,
                ShockLevel.HIGH,
                55
        );

        GearboxSelectionResult result = calculator.calculate(input);

        assertThat(result.serviceFactor()).isEqualTo(3.16);
        assertThat(result.designTorqueNm()).isEqualTo(948.0);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_REQUIRES_ENGINEERING_REVIEW");
        assertThat(result.riskNotes()).hasSize(4);
        assertThat(result.riskNotes()).anyMatch(note -> note.contains("Ambient temperature"));
        assertThat(result.riskNotes()).anyMatch(note -> note.contains("start-stop"));
        assertThat(result.engineeringReviewChecklist()).hasSize(8);
        assertThat(result.engineeringReviewChecklist()).anyMatch(note -> note.contains("mounting position"));
        assertThat(result.engineeringReviewChecklist()).anyMatch(note -> note.contains("bearing-life assumptions"));
        assertThat(result.engineeringReviewChecklist()).anyMatch(note -> note.contains("coupling compatibility"));
        assertThat(result.engineeringReviewChecklist()).anyMatch(note -> note.contains("shock loading"));
        assertThat(result.diagnosis()).contains("Additional engineering review");
    }

    @Test
    void keepsPowerFeasibilityBelowOneHundredPercentInVerificationStatus() {
        double motorPowerKw = 2.2;
        double justBelowOneHundredPercentTorqueNm = torqueForPowerKw(motorPowerKw * 0.9999, 50);

        GearboxSelectionResult result = calculator.calculate(powerBoundaryInput(
                motorPowerKw,
                justBelowOneHundredPercentTorqueNm,
                50
        ));

        assertThat(result.powerFeasibilityStatus())
                .isEqualTo(PowerFeasibilityStatus.VERIFY_ACTUAL_EFFICIENCY_AND_MOTOR_DUTY);
        assertThat(result.minimumRequiredOverallEfficiency()).isEqualTo(0.9999);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE");
        assertThat(result.riskNotes()).isEmpty();
    }

    @Test
    void requiresEngineeringReviewAtExactlyOneHundredPercentEfficiency() {
        double motorPowerKw = 2.2;
        double exactlyOneHundredPercentTorqueNm = torqueForPowerKw(motorPowerKw, 50);

        GearboxSelectionResult result = calculator.calculate(powerBoundaryInput(
                motorPowerKw,
                exactlyOneHundredPercentTorqueNm,
                50
        ));

        assertThat(result.powerFeasibilityStatus())
                .isEqualTo(PowerFeasibilityStatus.REQUIRED_OUTPUT_POWER_MEETS_OR_EXCEEDS_MOTOR_POWER);
        assertThat(result.minimumRequiredOverallEfficiency()).isEqualTo(1.0);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_REQUIRES_ENGINEERING_REVIEW");
        assertThat(result.riskNotes()).singleElement().asString().contains("no allowance for drivetrain losses");
    }

    @Test
    void requiresEngineeringReviewAboveOneHundredPercentEfficiency() {
        double motorPowerKw = 2.2;
        double aboveOneHundredPercentTorqueNm = torqueForPowerKw(motorPowerKw * 1.05, 50);

        GearboxSelectionResult result = calculator.calculate(powerBoundaryInput(
                motorPowerKw,
                aboveOneHundredPercentTorqueNm,
                50
        ));

        assertThat(result.requiredOutputPowerKw()).isEqualTo(2.31);
        assertThat(result.minimumRequiredOverallEfficiency()).isEqualTo(1.05);
        assertThat(result.powerFeasibilityStatus())
                .isEqualTo(PowerFeasibilityStatus.REQUIRED_OUTPUT_POWER_MEETS_OR_EXCEEDS_MOTOR_POWER);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_REQUIRES_ENGINEERING_REVIEW");
    }

    @Test
    void appliesDutyCycleBoundaryAtEightHours() {
        GearboxSelectionResult atEightHours = calculator.calculate(baselineInput(8.0, 0, 35.0));
        GearboxSelectionResult justAboveEightHours = calculator.calculate(baselineInput(8.1, 0, 35.0));

        assertThat(atEightHours.factorBreakdown().dutyCycleFactor()).isEqualTo(1.00);
        assertThat(atEightHours.serviceFactor()).isEqualTo(1.00);

        assertThat(justAboveEightHours.factorBreakdown().dutyCycleFactor()).isEqualTo(1.15);
        assertThat(justAboveEightHours.serviceFactor()).isEqualTo(1.15);
    }

    @Test
    void appliesDutyCycleBoundaryAtSixteenHours() {
        GearboxSelectionResult atSixteenHours = calculator.calculate(baselineInput(16.0, 0, 35.0));
        GearboxSelectionResult justAboveSixteenHours = calculator.calculate(baselineInput(16.1, 0, 35.0));

        assertThat(atSixteenHours.factorBreakdown().dutyCycleFactor()).isEqualTo(1.15);
        assertThat(atSixteenHours.serviceFactor()).isEqualTo(1.15);

        assertThat(justAboveSixteenHours.factorBreakdown().dutyCycleFactor()).isEqualTo(1.30);
        assertThat(justAboveSixteenHours.serviceFactor()).isEqualTo(1.30);
        assertThat(justAboveSixteenHours.riskNotes()).anyMatch(note -> note.contains("Long daily operating hours"));
    }

    @Test
    void appliesStartStopBoundaryAtTenAndThirtyStartsPerHour() {
        GearboxSelectionResult atTenStarts = calculator.calculate(baselineInput(8.0, 10, 35.0));
        GearboxSelectionResult atElevenStarts = calculator.calculate(baselineInput(8.0, 11, 35.0));
        GearboxSelectionResult atThirtyStarts = calculator.calculate(baselineInput(8.0, 30, 35.0));
        GearboxSelectionResult atThirtyOneStarts = calculator.calculate(baselineInput(8.0, 31, 35.0));

        assertThat(atTenStarts.factorBreakdown().startStopFactor()).isEqualTo(1.00);
        assertThat(atElevenStarts.factorBreakdown().startStopFactor()).isEqualTo(1.10);
        assertThat(atThirtyStarts.factorBreakdown().startStopFactor()).isEqualTo(1.10);
        assertThat(atThirtyOneStarts.factorBreakdown().startStopFactor()).isEqualTo(1.20);
        assertThat(atThirtyOneStarts.riskNotes()).anyMatch(note -> note.contains("Frequent start-stop operation"));
    }

    @Test
    void appliesAmbientTemperatureBoundaryAtFortyAndFiftyCelsius() {
        GearboxSelectionResult atFortyCelsius = calculator.calculate(baselineInput(8.0, 0, 40.0));
        GearboxSelectionResult justAboveFortyCelsius = calculator.calculate(baselineInput(8.0, 0, 40.1));
        GearboxSelectionResult atFiftyCelsius = calculator.calculate(baselineInput(8.0, 0, 50.0));
        GearboxSelectionResult justAboveFiftyCelsius = calculator.calculate(baselineInput(8.0, 0, 50.1));

        assertThat(atFortyCelsius.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.00);
        assertThat(atFortyCelsius.riskNotes()).isEmpty();

        assertThat(justAboveFortyCelsius.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.10);
        assertThat(justAboveFortyCelsius.riskNotes()).anyMatch(note -> note.contains("above 40 °C"));

        assertThat(atFiftyCelsius.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.10);
        assertThat(atFiftyCelsius.riskNotes()).anyMatch(note -> note.contains("above 40 °C"));

        assertThat(justAboveFiftyCelsius.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.20);
        assertThat(justAboveFiftyCelsius.riskNotes()).anyMatch(note -> note.contains("above 50 °C"));
    }

    @Test
    void rejectsInvalidOutputRpm() {
        GearboxSelectionInput input = new GearboxSelectionInput(
                2.2,
                1500,
                0,
                300,
                LoadType.MODERATE,
                12,
                20,
                ShockLevel.MEDIUM,
                35
        );

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Output rpm");
    }

    @Test
    void rejectsInvalidAmbientTemperature() {
        GearboxSelectionInput input = new GearboxSelectionInput(
                2.2,
                1500,
                50,
                300,
                LoadType.MODERATE,
                12,
                20,
                ShockLevel.MEDIUM,
                90
        );

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ambient temperature");
    }

    private GearboxSelectionInput baselineInput(
            double operatingHoursPerDay,
            int startsPerHour,
            double ambientTemperatureC
    ) {
        return new GearboxSelectionInput(
                2.2,
                1500,
                50,
                100,
                LoadType.LIGHT,
                operatingHoursPerDay,
                startsPerHour,
                ShockLevel.LOW,
                ambientTemperatureC
        );
    }

    private GearboxSelectionInput powerBoundaryInput(
            double motorPowerKw,
            double requiredTorqueNm,
            double outputRpm
    ) {
        return new GearboxSelectionInput(
                motorPowerKw,
                1500,
                outputRpm,
                requiredTorqueNm,
                LoadType.LIGHT,
                8,
                0,
                ShockLevel.LOW,
                35
        );
    }

    private double torqueForPowerKw(double powerKw, double outputRpm) {
        return powerKw * 60_000.0 / (2.0 * Math.PI * outputRpm);
    }
}
