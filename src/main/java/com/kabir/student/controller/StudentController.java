package com.kabir.student.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kabir.student.dto.PagedResponse;
import com.kabir.student.dto.StudentPatchRequest;
import com.kabir.student.dto.StudentRequest;
import com.kabir.student.dto.StudentResponse;
import com.kabir.student.service.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@Validated
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
		return studentService.createStudent(request);
	}

	@GetMapping("/{id}")
	public StudentResponse getStudent(@PathVariable Long id) {
		return studentService.getStudent(id);
	}

	@GetMapping
	public PagedResponse<StudentResponse> listStudents(
			@RequestParam(required = false) String branch,
			@RequestParam(required = false) Integer yop,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "fullName,asc") String sort) {

		String[] sortParts = sort.split(",");
		String field = sortParts[0];
		String direction = sortParts.length > 1 ? sortParts[1] : "asc";
		return studentService.listStudents(branch, yop, page, size, field, direction);
	}

	@PutMapping("/{id}")
	public StudentResponse updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
		return studentService.updateStudent(id, request);
	}

	@PatchMapping("/{id}")
	public StudentResponse patchStudent(@PathVariable Long id, @Valid @RequestBody StudentPatchRequest request) {
		return studentService.patchStudent(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteStudent(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean hard) {
		studentService.deleteStudent(id, hard);
	}
}

