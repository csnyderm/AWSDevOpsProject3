package com.skillstorm.skillvestgateway.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors().and()
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic().disable()
                .oauth2Login()
                .and()
                .build();
    }
}
