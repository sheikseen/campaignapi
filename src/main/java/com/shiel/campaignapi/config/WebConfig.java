package com.shiel.campaignapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
	 @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/**") // Allows CORS for all paths
	                        .allowedOrigins("*") // Allows requests from all origins (use specific domains in production)
	                        .allowedMethods("GET", "POST", "PUT", "DELETE") // HTTP methods allowed
	                        .allowedHeaders("*"); // Allows all headers
	            }
	        };
	    }
}
