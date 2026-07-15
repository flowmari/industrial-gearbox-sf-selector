package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.config.GearboxConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GearboxSelectionController.class)
@Import(GearboxConfiguration.class)
class GearboxSelectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsGearboxSelectionReasoningAndPowerFeasibilityFromPostApi() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculationModelVersion").value("generic-screening-v1.1"))
                .andExpect(jsonPath("$.reductionRatio").value(30.0))
                .andExpect(jsonPath("$.serviceFactor").value(1.67))
                .andExpect(jsonPath("$.designTorqueNm").value(501.0))
                .andExpect(jsonPath("$.requiredOutputPowerKw").value(1.57))
                .andExpect(jsonPath("$.minimumRequiredOverallEfficiency").value(0.714))
                .andExpect(jsonPath("$.powerFeasibilityStatus").value("VERIFY_ACTUAL_EFFICIENCY_AND_MOTOR_DUTY"))
                .andExpect(jsonPath("$.selectionStatus").value("SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE"))
                .andExpect(jsonPath("$.factorBreakdown.loadFactor").value(1.15))
                .andExpect(jsonPath("$.factorBreakdown.dutyCycleFactor").value(1.15))
                .andExpect(jsonPath("$.factorBreakdown.startStopFactor").value(1.10))
                .andExpect(jsonPath("$.factorBreakdown.shockFactor").value(1.15))
                .andExpect(jsonPath("$.factorBreakdown.ambientTemperatureFactor").value(1.00))
                .andExpect(jsonPath("$.selectionReasons[0]").value(containsString("Reduction ratio")))
                .andExpect(jsonPath("$.selectionReasons[6]").value(containsString("501.0 Nm")))
                .andExpect(jsonPath("$.selectionReasons[7]").value(containsString("1.57 kW")))
                .andExpect(jsonPath("$.riskNotes.length()").value(0))
                .andExpect(jsonPath("$.engineeringReviewChecklist.length()").value(4))
                .andExpect(jsonPath("$.engineeringReviewChecklist[0]").value(containsString("manufacturer documentation")))
                .andExpect(jsonPath("$.engineeringReviewChecklist[1]").value(containsString("mounting position")))
                .andExpect(jsonPath("$.engineeringReviewChecklist[3]").value(containsString("drivetrain efficiency")))
                .andExpect(jsonPath("$.diagnosis").value(containsString("minimum overall efficiency ratio")));
    }

    @Test
    void returnsEngineeringReviewWhenRequiredOutputPowerExceedsMotorPower() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 1.5,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredOutputPowerKw").value(1.57))
                .andExpect(jsonPath("$.minimumRequiredOverallEfficiency").value(1.0472))
                .andExpect(jsonPath("$.powerFeasibilityStatus").value("REQUIRED_OUTPUT_POWER_MEETS_OR_EXCEEDS_MOTOR_POWER"))
                .andExpect(jsonPath("$.selectionStatus").value("SCREENING_REQUIRES_ENGINEERING_REVIEW"))
                .andExpect(jsonPath("$.riskNotes[0]").value(containsString("no allowance for drivetrain losses")));
    }

    @Test
    void returnsRiskNotesForHighRiskConditions() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "loadType": "HEAVY",
                                  "operatingHoursPerDay": 20,
                                  "startsPerHour": 45,
                                  "shockLevel": "HIGH",
                                  "ambientTemperatureC": 55
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectionStatus").value("SCREENING_REQUIRES_ENGINEERING_REVIEW"))
                .andExpect(jsonPath("$.serviceFactor").value(3.16))
                .andExpect(jsonPath("$.designTorqueNm").value(948.0))
                .andExpect(jsonPath("$.riskNotes.length()").value(4))
                .andExpect(jsonPath("$.riskNotes[0]").value(containsString("Ambient temperature")))
                .andExpect(jsonPath("$.engineeringReviewChecklist.length()").value(8))
                .andExpect(jsonPath("$.engineeringReviewChecklist[6]").value(containsString("coupling compatibility")));
    }

    @Test
    void rejectsNegativeRequiredTorque() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": -300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsZeroOutputRpm() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 0,
                                  "requiredTorqueNm": 300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsOperatingHoursAboveTwentyFour() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 25,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingLoadType() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingAmbientTemperature() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 50,
                                  "requiredTorqueNm": 300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 12,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
