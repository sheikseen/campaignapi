package com.shiel.campaignapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
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
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

//@Bean
//public GrantedAuthorityDefaults grantedAuthorityDefaults() {
//    return new GrantedAuthorityDefaults(""); // Disable ROLE_ prefix
//}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests().requestMatchers("/auth/**").permitAll() // Public routes

				.requestMatchers("/events/add").hasRole("ADMIN")

				.requestMatchers("/events/update/*", "/events/delete/*", "/meetings/add", "/meetings/update/*",
						"/meetings/delete/*", "/booking/all", "/users/all", "/users/{id}")
				.hasRole("ADMIN")
				// Accessible to all roles
				.requestMatchers("/events/all", "/meetings/all", "/meetings/{id}", "/events/{id}")
				.hasAnyRole("APPLICATION", "USER", "ADMIN")

				// Admin-only routes

				// User and Admin routes
				.requestMatchers("/booking/add", "/booking/{id}	", "/booking/update/*", "/users/update/*","/users/{me}")
				.hasAnyRole("USER", "ADMIN")

				.anyRequest().authenticated() // All other routes require authentication
				.and()
				.anonymous(anonymous -> anonymous
						.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_APPLICATION"))))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// .authenticationProvider(authenticationProvider)
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
