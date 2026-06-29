package com.flowmari.industrialgearboxsfselector.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceFactorCalculatorTest {

    private final ServiceFactorCalculator calculator = new ServiceFactorCalculator();

    @Test
    void calculatesServiceFactorDesignTorqueAndDiagnosis() {
        GearboxSelectionInput input = new GearboxSelectionInput(
                2.2,
                1500,
                50,
                300,
                LoadType.MODERATE,
                12,
                20,
                ShockLevel.MEDIUM
        );

        GearboxSelectionResult result = calculator.calculate(input);

        assertThat(result.reductionRatio()).isEqualTo(30.0);
        assertThat(result.serviceFactor()).isEqualTo(1.7);
        assertThat(result.designTorqueNm()).isEqualTo(510.0);
        assertThat(result.diagnosis()).contains("510.0 Nm");
        assertThat(result.diagnosis()).contains("service factor is 1.7");
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
                ShockLevel.MEDIUM
        );

        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Output rpm");
    }
}
