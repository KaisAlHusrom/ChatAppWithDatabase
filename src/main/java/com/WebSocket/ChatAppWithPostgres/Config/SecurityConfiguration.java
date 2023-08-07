package com.WebSocket.ChatAppWithPostgres.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // You possibly will need to use this: csrf()
        //                .disable()
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(httpRequest ->
                                httpRequest.requestMatchers("/api/v1/auth/**", "/ws/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                        )
                .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout
                        .logoutUrl("/api/v1/auth/logout")
//                        .addLogoutHandler(null)
                        .logoutSuccessHandler(
                            (request, response, authentication)
                            -> SecurityContextHolder.clearContext())
                        );


        return http.build();
    }
}
