package com.micoservice.AuthService.controller;

import com.micoservice.AuthService.dto.AuthResponse;
import com.micoservice.AuthService.dto.IntrospectRequest;
import com.micoservice.AuthService.dto.IntrospectResponse;
import com.micoservice.AuthService.dto.LoginRequest;
import com.micoservice.AuthService.dto.RegisterRequest;
import com.micoservice.AuthService.model.User;
import com.micoservice.AuthService.repo.UserRepository;
import com.micoservice.AuthService.security.JwtService;
import com.micoservice.AuthService.service.AuthService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserRepository userRepository, JwtService jwtService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles());
        response.put("provider", user.getProvider());
        return response;
    }

    @PostMapping("/introspect")
    public IntrospectResponse introspect(@RequestBody(required = false) IntrospectRequest request,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = resolveToken(request, authorization);
        if (!StringUtils.hasText(token)) {
            return new IntrospectResponse(false, null, null, null, null);
        }

        try {
            String username = jwtService.extractUsername(token);
            if (!jwtService.isTokenValid(token, username)) {
                return new IntrospectResponse(false, null, null, null, null);
            }
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isEmpty()) {
                return new IntrospectResponse(false, null, null, null, null);
            }
            Long expiresAt = jwtService.extractExpiration(token).getTime();
            return new IntrospectResponse(true, username, user.get().getRoles(), expiresAt, user.get().getId());
        } catch (Exception ex) {
            return new IntrospectResponse(false, null, null, null, null);
        }
    }

    private String resolveToken(IntrospectRequest request, String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        if (request != null && StringUtils.hasText(request.getToken())) {
            return request.getToken();
        }
        return null;
    }
}
