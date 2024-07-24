package com.xaral.tubesail.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize ->
                        authorize.anyRequest().permitAll()  // Разрешить все запросы без аутентификации
                )
                .csrf(csrf -> csrf.disable()); ;  // Отключить CSRF для упрощения (но лучше настроить правильно)
        return http.build();
    }
}
