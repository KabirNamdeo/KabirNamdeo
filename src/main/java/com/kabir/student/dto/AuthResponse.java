package com.kabir.student.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
	private final String token;
	private final String tokenType;
	private final Instant expiresAt;
}

