package ru.kalampus.RequestService.configuration;

import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.context.annotation.Bean;

public class WebFluxProperty {
    @Bean
    WebFluxProperties webFluxProperties() {
        return new WebFluxProperties();
    }
}
