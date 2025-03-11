package com.tus.individual.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import com.tus.individual.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter);
    }

    // ✅ Test Role Hierarchy
    @Test
    void testRoleHierarchy() {
        RoleHierarchy roleHierarchy = SecurityConfig.roleHierarchy();

        // Test role inheritance
        Collection<? extends GrantedAuthority> managerAuthorities =
                roleHierarchy.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));

        assertTrue(managerAuthorities.contains(new SimpleGrantedAuthority("ROLE_COACH")));
        assertTrue(managerAuthorities.contains(new SimpleGrantedAuthority("ROLE_ANALYST")));
    }


    // ✅ Test Method Security Expression Handler
    @Test
    void testMethodSecurityExpressionHandler() {
        RoleHierarchy roleHierarchy = SecurityConfig.roleHierarchy();
        MethodSecurityExpressionHandler handler = SecurityConfig.methodSecurityExpressionHandler(roleHierarchy);

        assertNotNull(handler);
    }

    // ✅ Test Password Encoder
    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertNotNull(encoder);
        String rawPassword = "TestPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    // ✅ Test Security Filter Chain
    @Test
    void testSecurityFilterChain() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_SELF);  // ✅ Fluent API
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);  // ✅ Correct type

        when(httpSecurity.build()).thenReturn(mockFilterChain);  // ✅ Return valid filter chain

        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        assertNotNull(filterChain);  // ✅ No more null pointer error
    }

}
