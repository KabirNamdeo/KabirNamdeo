package com.kabir.student.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kabir.student.dto.StudentPatchRequest;
import com.kabir.student.dto.StudentRequest;
import com.kabir.student.dto.StudentResponse;
import com.kabir.student.exception.StudentNotFoundException;
import com.kabir.student.model.entity.Student;
import com.kabir.student.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

	@Mock
	private StudentRepository studentRepository;

	@InjectMocks
	private StudentServiceImpl studentService;

	private Student existingStudent;

	@BeforeEach
	void setUp() {
		existingStudent = Student.builder()
				.id(1L)
				.fullName("Kabir Ahmed")
				.email("kabir@example.com")
				.phone("+91-9000000001")
				.branch("CSE")
				.yop(2022)
				.active(true)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();
	}

	@Test
	void createStudent_persists_and_returns_payload() {
		StudentRequest request = StudentRequest.builder()
				.fullName("Aisha Verma")
				.email("aisha@example.com")
				.phone("+91-9000000002")
				.branch("ECE")
				.yop(2023)
				.build();

		when(studentRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
		when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
			Student student = invocation.getArgument(0);
			student.setId(5L);
			student.setCreatedAt(Instant.now());
			student.setUpdatedAt(Instant.now());
			return student;
		});

		StudentResponse response = studentService.createStudent(request);

		assertThat(response.getId()).isEqualTo(5L);
		assertThat(response.getFullName()).isEqualTo("Aisha Verma");
		verify(studentRepository).save(any(Student.class));
	}

	@Test
	void getStudent_throws_when_not_found() {
		when(studentRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> studentService.getStudent(99L))
				.isInstanceOf(StudentNotFoundException.class);
	}

	@Test
	void updateStudent_validates_email_uniqueness() {
		StudentRequest request = StudentRequest.builder()
				.fullName("Kabir Ahmed")
				.email("kabir@example.com")
				.phone(existingStudent.getPhone())
				.branch(existingStudent.getBranch())
				.yop(existingStudent.getYop())
				.build();

		when(studentRepository.findByIdAndActiveTrue(existingStudent.getId())).thenReturn(Optional.of(existingStudent));
		when(studentRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), existingStudent.getId())).thenReturn(false);
		when(studentRepository.save(existingStudent)).thenReturn(existingStudent);

		StudentResponse response = studentService.updateStudent(existingStudent.getId(), request);

		assertThat(response.getEmail()).isEqualTo(request.getEmail());
		verify(studentRepository).save(existingStudent);
	}

	@Test
	void patchStudent_updates_selective_fields() {
		when(studentRepository.findById(existingStudent.getId())).thenReturn(Optional.of(existingStudent));
		when(studentRepository.existsByEmailIgnoreCaseAndIdNot("new@email.com", existingStudent.getId())).thenReturn(false);
		when(studentRepository.save(existingStudent)).thenReturn(existingStudent);

		StudentPatchRequest patchRequest = new StudentPatchRequest();
		patchRequest.setEmail("new@email.com");
		patchRequest.setBranch("IT");

		StudentResponse response = studentService.patchStudent(existingStudent.getId(), patchRequest);

		assertThat(response.getEmail()).isEqualTo("new@email.com");
		assertThat(response.getBranch()).isEqualTo("IT");
	}

	@Test
	void deleteStudent_soft_deletes_when_record_exists() {
		when(studentRepository.findById(existingStudent.getId())).thenReturn(Optional.of(existingStudent));
		when(studentRepository.save(existingStudent)).thenReturn(existingStudent);

		studentService.deleteStudent(existingStudent.getId(), false);

		assertThat(existingStudent.getActive()).isFalse();
		verify(studentRepository, never()).delete(existingStudent);
		verify(studentRepository).save(existingStudent);
	}

	@Test
	void deleteStudent_hard_deletes_when_requested() {
		when(studentRepository.findById(existingStudent.getId())).thenReturn(Optional.of(existingStudent));

		studentService.deleteStudent(existingStudent.getId(), true);

		verify(studentRepository).delete(existingStudent);
	}
}

