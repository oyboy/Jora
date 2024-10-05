package com.main.Jora.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    LEADER, PARTICIPANT;
    @Override
    public String getAuthority() {
        return name();
    }
}
