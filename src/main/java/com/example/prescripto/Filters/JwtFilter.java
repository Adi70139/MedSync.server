package com.example.prescripto.Filters;

import com.example.prescripto.Utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// add logger imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.debug("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null) {
                log.debug("No Authorization header present for request {} {}", request.getMethod(), request.getRequestURI());
            }

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                log.debug("Authorization header found and starts with Bearer");
                String token = authHeader.substring(7);
                log.trace("Extracted token (masked): {}", token == null ? null : (token.length() > 10 ? token.substring(0, 10) + "..." : token));

                String username = jwtUtil.extractUsername(token);
                log.debug("Extracted username from token: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("No existing authentication in SecurityContext, loading user details for {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        log.debug("JWT token validated for user {}. Setting authentication.", username);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        log.debug("JWT token validation failed for user {}", username);
                    }
                }
            }

            log.trace("Proceeding with filter chain for request {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            log.trace("Returned from filter chain for request {} {}", request.getMethod(), request.getRequestURI());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.warn("JWT token expired for request {} {}", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"JWT token expired\"}");
        }
    }

}