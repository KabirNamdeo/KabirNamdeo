package com.kabir.student.service;

import com.kabir.student.dto.PagedResponse;
import com.kabir.student.dto.StudentPatchRequest;
import com.kabir.student.dto.StudentRequest;
import com.kabir.student.dto.StudentResponse;

public interface StudentService {
	StudentResponse createStudent(StudentRequest request);

	StudentResponse getStudent(Long id);

	PagedResponse<StudentResponse> listStudents(String branch, Integer yop, int page, int size, String sortField, String sortDirection);

	StudentResponse updateStudent(Long id, StudentRequest request);

	StudentResponse patchStudent(Long id, StudentPatchRequest request);

	void deleteStudent(Long id, boolean hardDelete);
}

