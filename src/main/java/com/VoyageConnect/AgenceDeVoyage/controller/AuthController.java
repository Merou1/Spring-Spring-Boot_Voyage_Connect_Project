package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.entity.User;

import com.VoyageConnect.AgenceDeVoyage.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication; // Use this instead of Tomcat's Authentication
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestParam String username, @RequestParam String fullName,
			@RequestParam String password, @RequestParam String role) {
		userService.registerUser(username, fullName, password, role);
		Map<String, String> response = new HashMap<>();
		response.put("message", "User registered successfully!");

		// Return a JSON response with status 200
		return ResponseEntity.ok(response);
	}// UserController.java

	@GetMapping("/user-details")
	public ResponseEntity<?> getUserDetails(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			Optional<User> user = userService.findByUsername(username);

			if (user.isPresent()) {
				return ResponseEntity.ok(user.get());
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	// Login will be handled by Spring Security (no need to explicitly define it
	// here)
}
