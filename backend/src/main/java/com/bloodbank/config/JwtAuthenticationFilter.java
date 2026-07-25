package com.bloodbank.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwtToken;
            final String userEmail;

            // FIX #1: CORRECT LOGIC - if null OR does NOT start with Bearer, skip
            // Old code was: if (authHeader == null || authHeader.startsWith("Bearer "))
            // That would SKIP authentication if token was present!
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract token (substring removes "Bearer " prefix)
            jwtToken = authHeader.substring(7);

            // FIX #2: Wrap JWT parsing in try-catch to handle invalid tokens
            try {
                userEmail = jwtUtils.getUserNameFromJwtToken(jwtToken);
            } catch (JwtException ex) {
                log.warn("Failed to extract username from JWT: {}", ex.getMessage());
                filterChain.doFilter(request, response);
                return;
            }

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    if (jwtUtils.validateJwtToken(jwtToken)) {
                        log.info("JWT validated successfully for user: {}", userEmail);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    } else {
                        log.warn("JWT validation failed for user: {}", userEmail);
                    }

                } catch (UsernameNotFoundException ex) {
                    log.warn("User not found for token email: {}", ex.getMessage());
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("Unexpected error in JWT authentication filter", ex);
            filterChain.doFilter(request, response);
        }
    }
}
