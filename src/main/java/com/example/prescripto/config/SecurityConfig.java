package com.example.prescripto.config;


import com.example.prescripto.Filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

       private final JwtFilter jwtFilter;
       private final AuthenticationProvider authenticationProvider;

       public SecurityConfig(JwtFilter jwtFilter, AuthenticationProvider authenticationProvider) {
                this.jwtFilter = jwtFilter;
                this.authenticationProvider = authenticationProvider;
       }

       @Bean
       public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config= new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:5174",
                    "http://localhost:5173",
                    "https://medsync-lwb0.onrender.com",
                    "https://undistortedly-unslakeable-elliot.ngrok-free.dev"));
            config.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE","OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true);


           UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
       }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/admin/login",
                                "/api/user/register",
                                "/uploads/**",
                                "/api/user/login",
                                "/api/doctor/list",
                                "/api/doctor/login" ,"/api/test"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }



}
