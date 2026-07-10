package com.flowmari.industrialgearboxsfselector.api;

import com.flowmari.industrialgearboxsfselector.config.GearboxConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GearboxSelectionController.class)
@Import({GearboxConfiguration.class, ApiExceptionHandler.class})
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsProblemDetailForBeanValidationErrors() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "inputRpm": 1500,
                                  "outputRpm": 0,
                                  "requiredTorqueNm": -300,
                                  "loadType": "MODERATE",
                                  "operatingHoursPerDay": 25,
                                  "startsPerHour": 20,
                                  "shockLevel": "MEDIUM",
                                  "ambientTemperatureC": 35
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid gearbox selection request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*]", hasItem(containsString("outputRpm"))))
                .andExpect(jsonPath("$.errors[*]", hasItem(containsString("requiredTorqueNm"))))
                .andExpect(jsonPath("$.errors[*]", hasItem(containsString("operatingHoursPerDay"))));
    }

    @Test
    void returnsProblemDetailForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/gearbox/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motorPowerKw": 2.2,
                                  "loadType": "UNKNOWN_LOAD_TYPE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Malformed request body"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request body could not be read. Check JSON syntax and enum values."));
    }
}
