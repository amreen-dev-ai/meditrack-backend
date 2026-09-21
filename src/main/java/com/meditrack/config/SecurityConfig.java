package com.meditrack.config;

import com.meditrack.security.CustomUserDetailsService;
import com.meditrack.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // BCrypt: a one-way hashing algorithm. Passwords are NEVER stored as plain text --
    // this is what converts "mypassword123" into an unrecoverable hash before saving.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This tells Spring: "allow requests from this specific origin (our React dev server),
    // with these HTTP methods, and allow the Authorization header to be sent."
    // Without this, the browser blocks cross-origin requests from localhost:5173
    // to localhost:8080 before they even reach the backend -- that's what caused
    // the 403 you just saw.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection is for browser cookie-based sessions -- since we're
                // stateless (JWT in headers, not cookies), we disable it.
                .csrf(csrf -> csrf.disable())

                // Tells Spring Security to actually USE the CorsConfigurationSource bean above.
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth
                        // These two endpoints must be reachable WITHOUT a token --
                        // otherwise nobody could ever log in in the first place.
                        .requestMatchers("/api/auth/**").permitAll()
                        // Everything else requires a valid JWT.
                        .anyRequest().authenticated()
                )

                // STATELESS: we don't use HttpSession at all. Every request must carry
                // its own proof of identity (the JWT) -- this is what makes REST APIs scale.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                // Insert our custom JWT filter BEFORE Spring's default username/password filter --
                // this is what makes our filter actually run on every request.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}