package com.meditrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter = this filter's doFilterInternal() runs exactly ONCE
// for every single incoming HTTP request, before it reaches your controllers.
// This is the "step 4/5" piece from the auth flow you already explained:
// intercept the request, pull the token out, validate it.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: look for the "Authorization: Bearer <token>" header
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);   // strip off "Bearer " (7 characters)
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Malformed/expired token -- just let the request continue,
                // it'll get rejected downstream because no auth was ever set.
                logger.warn("Could not extract username from JWT: " + e.getMessage());
            }
        }

        // Step 2: if we found a username AND nothing is authenticated yet on this request...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 3: actually validate the token against this user
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 4: tell Spring Security "this request IS authenticated, as this user"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 5: pass the request along to the next filter / the actual controller
        filterChain.doFilter(request, response);
    }
}
