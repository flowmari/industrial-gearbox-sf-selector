package com.flowmari.industrialgearboxsfselector.config;

import com.flowmari.industrialgearboxsfselector.domain.ServiceFactorCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GearboxConfiguration {

    @Bean
    ServiceFactorCalculator serviceFactorCalculator() {
        return new ServiceFactorCalculator();
    }
}
