package com.krishna.MobileBackendProjectPhase1.JwtFilter;

import com.krishna.MobileBackendProjectPhase1.service.CustomUserDetailsService;
import com.krishna.MobileBackendProjectPhase1.util.JWTutil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTutil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No Authorization header
        // Continue the request normally.
        if (header == null || header.isBlank()) {

            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header exists but is not Bearer JWT
        if (!header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Remove "Bearer " from the header
            String token = header.substring(7);

            if (token.isBlank()) {
                sendUnauthorizedResponse(response, "Invalid or expired token");
                return;
            }

            // Extract email/username from JWT
            String username = jwtUtil.findUsername(token);

            // Only authenticate if no authentication
            // already exists
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate username and expiration
                if (jwtUtil.validate(userDetails, username, token)) {

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            // Continue request
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            // Invalid / expired / malformed JWT
            SecurityContextHolder.clearContext();
            sendUnauthorizedResponse(response, "Invalid or expired token");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(
                """
                {
                  "success": false,
                  "message": "%s",
                  "data": null
                }
                """.formatted(message)
        );
    }
}