package com.tus.individual.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR,
    CUSTOMER_SERVICE,
    SUPPORT_ENGINEER,
    NETWORK_MANAGEMENT_ENGINEER;

    @Override
    public String getAuthority() {
        return name(); // ✅ Returns "ADMINISTRATOR", "CUSTOMER_SERVICE", etc.
    }
}
