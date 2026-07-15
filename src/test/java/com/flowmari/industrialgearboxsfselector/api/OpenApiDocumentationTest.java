package com.flowmari.industrialgearboxsfselector.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesOpenApiSpecificationAndPowerFeasibilityContract() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Industrial Gearbox Sizing & Service Factor API"))
                .andExpect(jsonPath("$.info.version").value("0.1.3-alpha"))
                .andExpect(jsonPath("$.paths", hasKey("/api/gearbox/selection")))
                .andExpect(jsonPath("$.paths", hasKey("/health")))
                .andExpect(jsonPath("$.components.schemas.GearboxSelectionResponse.properties", hasKey("calculationModelVersion")))
                .andExpect(jsonPath("$.components.schemas.GearboxSelectionResponse.properties", hasKey("requiredOutputPowerKw")))
                .andExpect(jsonPath("$.components.schemas.GearboxSelectionResponse.properties", hasKey("minimumRequiredOverallEfficiency")))
                .andExpect(jsonPath("$.components.schemas.GearboxSelectionResponse.properties", hasKey("powerFeasibilityStatus")));
    }
}
