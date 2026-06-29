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
    void returnsGearboxSelectionResultFromPostApi() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reductionRatio").value(30.0))
                .andExpect(jsonPath("$.serviceFactor").value(1.7))
                .andExpect(jsonPath("$.designTorqueNm").value(510.0))
                .andExpect(jsonPath("$.selectionStatus").value("SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE"))
                .andExpect(jsonPath("$.diagnosis").value(containsString("510.0 Nm")));
    }
}
