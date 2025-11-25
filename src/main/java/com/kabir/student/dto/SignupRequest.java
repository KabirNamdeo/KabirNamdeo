package com.kabir.student.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

	@NotBlank(message = "Full name is required")
	@Length(max = 120)
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email
	@Length(max = 160)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 64, message = "Password must be 8-64 characters")
	private String password;
}

