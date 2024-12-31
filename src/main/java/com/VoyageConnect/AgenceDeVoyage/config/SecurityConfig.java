package com.VoyageConnect.AgenceDeVoyage.config;

import com.VoyageConnect.AgenceDeVoyage.service.CustomUserDetailsService;
import com.VoyageConnect.AgenceDeVoyage.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	// Configure password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Authentication manager configuration
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
		return authenticationManagerBuilder.build();
	}

	// Configure CORS settings
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:5173") // Allow frontend origin
				.allowedMethods("GET", "POST", "PUT", "DELETE") // Allow necessary HTTP methods
				.allowedHeaders("*") // Allow all headers
				.allowCredentials(true); // Allow credentials (cookies, headers)
	}

	// Security filter configuration
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable() // Disable CSRF for debugging; re-enable in production
				.authorizeHttpRequests().requestMatchers("/auth/register", "/login").permitAll() // Allow login and
																									// register publicly
				.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/client/**").hasRole("CLIENT")
				.anyRequest().authenticated().and().formLogin().loginProcessingUrl("/login")
				.successHandler((request, response, authentication) -> {
	                response.setContentType("application/json");
	                // Get the user's role from the authentication object
	                String userRole = authentication.getAuthorities().stream()
	                        .map(grantedAuthority -> grantedAuthority.getAuthority())
	                        .filter(role -> role.startsWith("ROLE_"))
	                        .findFirst()
	                        .orElse("ROLE_UNKNOWN");

	                // Retrieve the JSESSIONID cookie
	                String jsessionId = request.getSession().getId();

	                // Return a JSON response with the message, role, and JSESSIONID
	                String jsonResponse = "{\"message\": \"Login Successful\", \"role\": \"" + userRole + 
	                                      "\", \"jsessionId\": \"" + jsessionId + "\"}";
	                response.getWriter().write(jsonResponse);
	                response.getWriter().flush();
	            }).failureHandler((request, response, exception) -> {
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter()
							.write("{\"error\": \"Login Failed\", \"message\": \"" + exception.getMessage() + "\"}");
					response.getWriter().flush();
				})

				.and().logout().logoutUrl("/logout").invalidateHttpSession(true)
			    .deleteCookies("JSESSIONID").logoutSuccessUrl("/login?logout=true").permitAll();

		return http.build();
	}

}
