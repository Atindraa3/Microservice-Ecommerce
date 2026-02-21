package com.micoservice.AuthService.controller;

import com.micoservice.AuthService.dto.AdminAuditLogResponse;
import com.micoservice.AuthService.dto.RoleUpdateRequest;
import com.micoservice.AuthService.dto.UserSummary;
import com.micoservice.AuthService.model.AdminAuditLog;
import com.micoservice.AuthService.model.User;
import com.micoservice.AuthService.repo.AdminAuditLogRepository;
import com.micoservice.AuthService.repo.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AdminAuditLogRepository auditLogRepository;

    public AdminController(UserRepository userRepository, AdminAuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/users")
    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @PutMapping("/users/{id}/roles")
    @ResponseStatus(HttpStatus.OK)
    public UserSummary updateRoles(@PathVariable Long id,
                                   @RequestBody RoleUpdateRequest request,
                                   @AuthenticationPrincipal User admin) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<String> roles = normalizeRoles(request.getRoles());
        user.setRoles(roles);
        User saved = userRepository.save(user);
        auditLogRepository.save(new AdminAuditLog(
                admin != null ? admin.getEmail() : "unknown",
                "UPDATE_ROLES",
                saved.getId(),
                saved.getEmail(),
                String.join(",", saved.getRoles())
        ));
        return toSummary(saved);
    }

    @GetMapping("/audit")
    public List<AdminAuditLogResponse> auditLog() {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(log -> new AdminAuditLogResponse(
                        log.getId(),
                        log.getActorEmail(),
                        log.getAction(),
                        log.getTargetUserId(),
                        log.getTargetEmail(),
                        log.getRoles(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getRoles(), user.getProvider());
    }

    private Set<String> normalizeRoles(Set<String> roles) {
        Set<String> normalized = new HashSet<>();
        if (roles != null) {
            for (String role : roles) {
                if (role == null || role.isBlank()) {
                    continue;
                }
                String trimmed = role.trim().toUpperCase();
                if (!trimmed.startsWith("ROLE_")) {
                    trimmed = "ROLE_" + trimmed;
                }
                if (trimmed.equals("ROLE_USER") || trimmed.equals("ROLE_ADMIN")) {
                    normalized.add(trimmed);
                }
            }
        }
        if (normalized.isEmpty()) {
            normalized.add("ROLE_USER");
        }
        return normalized;
    }
}
