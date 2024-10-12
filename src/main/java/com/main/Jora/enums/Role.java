package com.main.Jora.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_LEADER, ROLE_MODERATOR, ROLE_PARTICIPANT;
    @Override
    public String getAuthority() {
        return name();
    }
}
