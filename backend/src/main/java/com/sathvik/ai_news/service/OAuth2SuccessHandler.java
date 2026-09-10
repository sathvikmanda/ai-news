package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.User;
import com.sathvik.ai_news.repository.UserRepository;
import com.sathvik.ai_news.security.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() ->
                        userRepository.findByEmail(email)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Authenticated Google user not found in database"
                                        )
                                )
                );

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        response.sendRedirect(
                "http://localhost:4200/auth/callback?token=" + token
        );
    }
}