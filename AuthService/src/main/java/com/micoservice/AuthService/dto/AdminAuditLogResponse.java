package com.micoservice.AuthService.dto;

import java.time.Instant;

public class AdminAuditLogResponse {

    private Long id;
    private String actorEmail;
    private String action;
    private Long targetUserId;
    private String targetEmail;
    private String roles;
    private Instant createdAt;

    public AdminAuditLogResponse() {
    }

    public AdminAuditLogResponse(Long id, String actorEmail, String action, Long targetUserId, String targetEmail, String roles, Instant createdAt) {
        this.id = id;
        this.actorEmail = actorEmail;
        this.action = action;
        this.targetUserId = targetUserId;
        this.targetEmail = targetEmail;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
