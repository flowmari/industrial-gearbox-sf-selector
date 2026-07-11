package com.flowmari.industrialgearboxsfselector.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
        info = @Info(
                title = "Industrial Gearbox Sizing & Service Factor API",
                version = "0.1.2-alpha",
                description = """
                        Generic engineering screening API for industrial gearbox service factor calculation,
                        reduction ratio calculation, design torque calculation, factor breakdowns,
                        selection reasons, and risk notes.

                        This API intentionally excludes manufacturer-specific catalog data and model-number recommendations.
                        Final reducer selection must always be verified against official manufacturer documentation.
                        """
        )
)
public class OpenApiConfiguration {
}
