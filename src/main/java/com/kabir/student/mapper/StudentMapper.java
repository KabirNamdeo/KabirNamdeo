package com.kabir.student.mapper;

import com.kabir.student.dto.StudentRequest;
import com.kabir.student.dto.StudentResponse;
import com.kabir.student.model.entity.Student;

public final class StudentMapper {

	private StudentMapper() {
	}

	public static Student toEntity(StudentRequest request) {
		return Student.builder()
				.fullName(request.getFullName())
				.email(request.getEmail())
				.phone(request.getPhone())
				.branch(request.getBranch())
				.yop(request.getYop())
				.active(Boolean.TRUE)
				.build();
	}

	public static StudentResponse toResponse(Student student) {
		return StudentResponse.builder()
				.id(student.getId())
				.fullName(student.getFullName())
				.email(student.getEmail())
				.phone(student.getPhone())
				.branch(student.getBranch())
				.yop(student.getYop())
				.active(student.getActive())
				.createdAt(student.getCreatedAt())
				.updatedAt(student.getUpdatedAt())
				.build();
	}
}

