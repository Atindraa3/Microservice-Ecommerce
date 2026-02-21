package com.micoservice.AuthService.dto;

import java.util.Set;

public class IntrospectResponse {

    private boolean active;
    private String username;
    private Set<String> roles;
    private Long expiresAt;
    private Long userId;

    public IntrospectResponse() {
    }

    public IntrospectResponse(boolean active, String username, Set<String> roles, Long expiresAt, Long userId) {
        this.active = active;
        this.username = username;
        this.roles = roles;
        this.expiresAt = expiresAt;
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
