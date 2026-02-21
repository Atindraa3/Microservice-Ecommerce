package com.micoservice.AuthService.dto;

import com.micoservice.AuthService.model.AuthProvider;
import java.util.Set;

public class UserSummary {

    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private AuthProvider provider;

    public UserSummary() {
    }

    public UserSummary(Long id, String name, String email, Set<String> roles, AuthProvider provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.provider = provider;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}
