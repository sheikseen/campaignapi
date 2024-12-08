package com.shiel.campaignapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests().requestMatchers("/auth/**").hasRole("APPLICATION")
				.requestMatchers("/events/add", "/events/update/*", "/events/delete/*", "/meetings/add",
						"/meetings/update/*", "/meetings/delete/*", "/booking/all", "/users/all",
						"booking/event/{eventId}", "booking/user/{userId}", "booking/delete/{bookingId}",
						"/zoom-meetings/add","/zoom-meetings/delete/*","/zoom-meetings/update/*","/roles/**")
				.hasRole("ADMIN")

				.requestMatchers("/events/all", "/meetings/all", "/meetings/{meetingId}", "/events/{eventId}",
						"/zoom-meetings/all", "/zoom-meetings/{meetingId}")
				.hasAnyRole("APPLICATION", "USER", "ADMIN")

				.requestMatchers(
						"/booking/add", "/booking/{id}	", "/booking/update/*", "/users/update/*", "/users/{userId}","/users/delete/*")
				.hasAnyRole("USER", "ADMIN").and()

				.anonymous(anonymous -> anonymous
						.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_APPLICATION"))))
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:8005"));
		configuration.setAllowedMethods(List.of("GET", "POST"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
