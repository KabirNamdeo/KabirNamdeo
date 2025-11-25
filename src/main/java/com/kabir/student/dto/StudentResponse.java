package com.kabir.student.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentResponse {
	private final Long id;
	private final String fullName;
	private final String email;
	private final String phone;
	private final String branch;
	private final Integer yop;
	private final Boolean active;
	private final Instant createdAt;
	private final Instant updatedAt;
}

