package com.krishna.MobileBackendProjectPhase1.config;
import com.krishna.MobileBackendProjectPhase1.JwtFilter.JWTFilter;
import com.krishna.MobileBackendProjectPhase1.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class securityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // authenticatin manager
    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    // JWT Filter
    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter();
    }

    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // allow rules CORS Configuration Who can access? Which methods? Which headers? Credentials?
        CorsConfiguration configuration =new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000")); //allows only 3000 port frontend request

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); //allows this request methods

        configuration.setAllowedHeaders(List.of("*")); // allows headers like bear token

        //Allows credentials such as cookies/authorization-related browser credentials to be included in cross-origin requests.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);  // applies this cors rules to all incoming API's

        return source;
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                // No HTTP session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization
                .authorizeHttpRequests(auth -> auth
                                // Public APIs
                                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh", "/api/auth/logout", "/api/health").permitAll()
                                // Everything else requires authentication
                                .anyRequest().authenticated()
                )

                // JSON response for 401 and 403
                .exceptionHandling(exception ->
                        exception

                                // 401 Unauthorized
                                .authenticationEntryPoint((request, response,authException) -> {response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                            response.getWriter().write(
                                                    """
                                                    {
                                                      "success": false,
                                                      "message": "Authentication required",
                                                      "data": null
                                                    }
                                                    """);})

                                // 403 Forbidden
                                .accessDeniedHandler(
                                        (request, response,
                                         accessDeniedException) -> {
                                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                            response.getWriter().write(
                                                    """
                                                    {
                                                      "success": false,
                                                      "message": "Access denied",
                                                      "data": null
                                                    }
                                                    """);})
                )

                // Run JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}