package com.ferry.framework.security.config;

import com.ferry.framework.security.filter.TokenAuthenticationFilter;
import com.ferry.framework.security.util.JwtTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(FerrySecurityProperties.class)
public class SecurityConfig {

    @Bean
    public JwtTokenService jwtTokenService(FerrySecurityProperties properties) {
        return new JwtTokenService(properties.getJwtSecret());
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(JwtTokenService jwtTokenService) {
        return new TokenAuthenticationFilter(jwtTokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   FerrySecurityProperties properties,
                                                   TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> {
                for (String pattern : properties.getPermitAll()) {
                    auth.requestMatchers(pattern).permitAll();
                }
                if (properties.isEnabled()) {
                    auth.anyRequest().authenticated();
                } else {
                    auth.anyRequest().permitAll();
                }
            });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
