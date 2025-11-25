package com.kabir.student.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kabir.student.dto.AuthResponse;
import com.kabir.student.dto.LoginRequest;
import com.kabir.student.dto.SignupRequest;
import com.kabir.student.model.entity.UserAccount;
import com.kabir.student.repository.UserRepository;
import com.kabir.student.security.JwtTokenProvider;
import com.kabir.student.security.JwtTokenProvider.TokenPair;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;

	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered");
		}
		UserAccount saved = userRepository.save(UserAccount.builder()
				.fullName(request.getFullName())
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.build());

		UserDetails principal = User.builder()
				.username(saved.getEmail())
				.password(saved.getPasswordHash())
				.roles(saved.getRole().name())
				.build();

		TokenPair tokenPair = jwtTokenProvider.generateToken(principal);
		return AuthResponse.builder()
				.token(tokenPair.token())
				.tokenType("Bearer")
				.expiresAt(tokenPair.expiresAt())
				.build();
	}

	public AuthResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		TokenPair tokenPair = jwtTokenProvider.generateToken(principal);
		return AuthResponse.builder()
				.token(tokenPair.token())
				.tokenType("Bearer")
				.expiresAt(tokenPair.expiresAt())
				.build();
	}
}

