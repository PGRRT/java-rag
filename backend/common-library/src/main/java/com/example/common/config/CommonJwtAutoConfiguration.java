package com.example.common.config;

import com.example.common.jwt.filter.JwtAuthenticationFilter;
import com.example.common.jwt.service.CookieService;
import com.example.common.jwt.service.JwtService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(name = "common.jwt.enabled", havingValue = "true")
public class CommonJwtAutoConfiguration {
    @Bean
    public CookieService cookieService() {
        return new CookieService();
    }

    @Bean
    public JwtService jwtService(CookieService cookieService) {
        return new JwtService(cookieService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }


}
