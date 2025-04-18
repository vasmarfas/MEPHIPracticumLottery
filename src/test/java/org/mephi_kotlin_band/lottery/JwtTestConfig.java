package org.mephi_kotlin_band.lottery;

import org.mephi_kotlin_band.lottery.features.user.service.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class JwtTestConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Для тестов отключаем проверку JWT токенов
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .httpBasic(withDefaults());
        
        return http.build();
    }
    
    @Bean
    @Primary
    public JwtService jwtServiceMock() {
        JwtService mockService = Mockito.mock(JwtService.class);
        // Возвращаем заранее заданные значения для любых вызовов
        Mockito.when(mockService.generateToken(Mockito.any())).thenReturn("test-token");
        Mockito.when(mockService.extractUsername(Mockito.anyString())).thenReturn("test-user");
        Mockito.when(mockService.isTokenValid(Mockito.anyString(), Mockito.any())).thenReturn(true);
        return mockService;
    }
} 