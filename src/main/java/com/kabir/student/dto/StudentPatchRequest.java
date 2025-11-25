package com.kabir.student.dto;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentPatchRequest {
	@Length(max = 120)
	private String fullName;

	@Email(message = "Email must be valid")
	@Length(max = 160)
	private String email;

	@Pattern(regexp = "^[+0-9\\- ]{7,25}$", message = "Phone must contain 7-25 digits and valid symbols")
	private String phone;

	@Length(max = 80)
	private String branch;

	@Min(value = 1990, message = "Year of passing must be after 1990")
	@Max(value = 2100, message = "Year of passing must be before 2100")
	private Integer yop;

	private Boolean active;
}

