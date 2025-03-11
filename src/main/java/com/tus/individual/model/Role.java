package com.tus.individual.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR,
    ANALYST,
    COACH,
    MANAGER;

    @Override
    public String getAuthority() {
        return name(); // ✅ Returns "ADMINISTRATOR", "CUSTOMER_SERVICE", etc.
    }
}
