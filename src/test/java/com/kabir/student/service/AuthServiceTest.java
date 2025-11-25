package com.kabir.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kabir.student.dto.AuthResponse;
import com.kabir.student.dto.LoginRequest;
import com.kabir.student.dto.SignupRequest;
import com.kabir.student.model.entity.UserAccount;
import com.kabir.student.repository.UserRepository;
import com.kabir.student.security.JwtTokenProvider;
import com.kabir.student.security.JwtTokenProvider.TokenPair;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthService authService;

	private SignupRequest signupRequest;

	@BeforeEach
	void setUp() {
		signupRequest = new SignupRequest();
		signupRequest.setFullName("Test User");
		signupRequest.setEmail("test@example.com");
		signupRequest.setPassword("Password!1");
	}

	@Test
	void signup_creates_user_and_returns_token() {
		when(userRepository.existsByEmailIgnoreCase(signupRequest.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashed");
		when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
			UserAccount user = invocation.getArgument(0);
			user.setId(10L);
			return user;
		});
		when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn(new TokenPair("token", java.time.Instant.now().plusSeconds(3600)));

		AuthResponse response = authService.signup(signupRequest);

		assertThat(response.getToken()).isEqualTo("token");
	}

	@Test
	void signup_rejects_duplicate_email() {
		when(userRepository.existsByEmailIgnoreCase(signupRequest.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> authService.signup(signupRequest))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void login_returns_token_on_success() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("Password!1");

		UserDetails principal = org.springframework.security.core.userdetails.User.builder()
				.username(loginRequest.getEmail())
				.password("hashed")
				.roles("USER")
				.build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(principal)).thenReturn(new TokenPair("token", java.time.Instant.now().plusSeconds(3600)));

		AuthResponse response = authService.login(loginRequest);

		assertThat(response.getToken()).isEqualTo("token");
	}
}

