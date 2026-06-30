package com.flowmari.industrialgearboxsfselector.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceFactorCalculatorTest {

    private final ServiceFactorCalculator calculator = new ServiceFactorCalculator();

    @Test
    void calculatesMultiplicativeServiceFactorDesignTorqueAndSelectionReasons() {
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

        assertThat(result.reductionRatio()).isEqualTo(30.0);
        assertThat(result.serviceFactor()).isEqualTo(1.67);
        assertThat(result.designTorqueNm()).isEqualTo(501.0);
        assertThat(result.selectionStatus()).isEqualTo("SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE");
        assertThat(result.factorBreakdown().loadFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().dutyCycleFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().startStopFactor()).isEqualTo(1.10);
        assertThat(result.factorBreakdown().shockFactor()).isEqualTo(1.15);
        assertThat(result.factorBreakdown().ambientTemperatureFactor()).isEqualTo(1.00);
        assertThat(result.selectionReasons()).contains(
                "Reduction ratio was calculated from input rpm and output rpm: 30.0.",
                "The resulting generic service factor is 1.67, so the reducer should be rated for at least 501.0 Nm."
        );
        assertThat(result.riskNotes()).isEmpty();
        assertThat(result.diagnosis()).contains("manufacturer documentation");
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
        assertThat(result.diagnosis()).contains("Additional engineering review");
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
}
