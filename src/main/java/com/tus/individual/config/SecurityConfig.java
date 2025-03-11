package com.tus.individual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tus.individual.filter.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ Allow static content (HTML, JS, CSS)
                .requestMatchers(
                		"/",
                		"/admin/**",
                		"/components/**",
                		"/css/**",
                		"/analyst/**",
                		"/images/**",
                		"/js/**",
                		"/manager/**",
                		"/coach/**",
                		"/index.html",
                		"/main.js",
                		"/styles.css"
                ).permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/actions").permitAll()
                .requestMatchers("/api/matches").permitAll()
                .requestMatchers("/api/players").permitAll()
                .requestMatchers("/api/teams").permitAll()
                .requestMatchers("/api/teams/goals").permitAll()
                .requestMatchers("/api/teams/points").permitAll()
                .requestMatchers("/api/season/**").permitAll()
                .requestMatchers("/api/players/team").permitAll()
                
                .anyRequest().authenticated()
                
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}