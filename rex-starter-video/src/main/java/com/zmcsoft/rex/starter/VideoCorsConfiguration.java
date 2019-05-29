package com.zmcsoft.rex.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class VideoCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration videoCorsConfiguration =new CorsConfiguration();
        videoCorsConfiguration.addAllowedHeader("*");
        videoCorsConfiguration.addAllowedMethod("*");
        videoCorsConfiguration.addAllowedOrigin("*");
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", videoCorsConfiguration);
        return new CorsFilter(corsConfigurationSource);
    }
}
