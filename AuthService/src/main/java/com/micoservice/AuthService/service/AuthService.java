package com.micoservice.AuthService.service;

import com.micoservice.AuthService.dto.AuthResponse;
import com.micoservice.AuthService.dto.LoginRequest;
import com.micoservice.AuthService.dto.RegisterRequest;
import com.micoservice.AuthService.model.AuthProvider;
import com.micoservice.AuthService.model.User;
import com.micoservice.AuthService.repo.UserRepository;
import com.micoservice.AuthService.security.JwtService;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.admin-emails:}")
    private String adminEmails;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        user.setRoles(determineRoles(request.getEmail()));
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return new AuthResponse(token, jwtService.getJwtExpirationMs(), saved.getName(), saved.getEmail(), saved.getRoles());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, jwtService.getJwtExpirationMs(), user.getName(), user.getEmail(), user.getRoles());
    }

    private Set<String> determineRoles(String email) {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        if (adminEmails != null && !adminEmails.isBlank()) {
            for (String adminEmail : adminEmails.split(",")) {
                if (email.equalsIgnoreCase(adminEmail.trim())) {
                    roles.add("ROLE_ADMIN");
                }
            }
        }
        return roles;
    }
}
