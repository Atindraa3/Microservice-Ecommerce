package com.micoservice.AuthService.security;

import com.micoservice.AuthService.model.AuthProvider;
import com.micoservice.AuthService.model.User;
import com.micoservice.AuthService.repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.admin-emails:}")
    private String adminEmails;

    public OAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String sub = oauth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email);
            newUser.setProvider(AuthProvider.GOOGLE);
            newUser.setProviderId(sub);
            newUser.setRoles(determineRoles(email));
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user);
        String redirectUrl = frontendUrl + "/oauth2/redirect?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
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
