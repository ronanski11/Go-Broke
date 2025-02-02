package com.ina23c.gobroke.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ina23c.gobroke.model.Role;
import com.ina23c.gobroke.model.User;
import com.ina23c.gobroke.repository.UserRepository;
import com.ina23c.gobroke.security.model.AuthenticationRequest;
import com.ina23c.gobroke.security.model.AuthenticationResponse;
import com.ina23c.gobroke.security.model.RegisterRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public ResponseEntity<?> register(RegisterRequest request) {
		var user = User.builder().username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.USER).profilePicId("").build();
		if (!userRepository.findByUsernameOptional(request.getUsername()).isEmpty())
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is taken.");
		User savedUser = userRepository.save(user);
		var jwtToken = jwtService.generateToken(user, savedUser.getId());
		return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		var user = userRepository.findByUsernameOptional(request.getUsername()).orElseThrow();
		var jwtToken = jwtService.generateToken(user, user.getId());
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public String getUsername() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getUsername();
	}
	
	public boolean isAdmin() {
		return userRepository.findByUsername(getUsername()).getRole().equals(Role.ADMIN);
	}

	public void authenticatePassword(String username, String currentPassword) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, currentPassword));
	}

}
