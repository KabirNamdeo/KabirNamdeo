package com.kabir.student.exception;

public class StudentNotFoundException extends RuntimeException {
	public StudentNotFoundException(Long id) {
		super("Student with id %d not found or inactive".formatted(id));
	}
}

