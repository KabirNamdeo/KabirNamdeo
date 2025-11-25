package com.kabir.student.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

	@NotBlank(message = "Full name is required")
	@Length(max = 120)
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Length(max = 160)
	private String email;

	@NotBlank(message = "Phone is required")
	@Pattern(regexp = "^[+0-9\\- ]{7,25}$", message = "Phone must contain 7-25 digits and valid symbols")
	private String phone;

	@NotBlank(message = "Branch is required")
	@Length(max = 80)
	private String branch;

	@NotNull(message = "Year of passing is required")
	@Min(value = 1990, message = "Year of passing must be after 1990")
	@Max(value = 2100, message = "Year of passing must be before 2100")
	private Integer yop;
}

