package com.micoservice.AuthService.repo;

import com.micoservice.AuthService.model.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
}
