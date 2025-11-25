package com.kabir.student.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.kabir.student.dto.PagedResponse;
import com.kabir.student.dto.StudentPatchRequest;
import com.kabir.student.dto.StudentRequest;
import com.kabir.student.dto.StudentResponse;
import com.kabir.student.exception.StudentNotFoundException;
import com.kabir.student.mapper.StudentMapper;
import com.kabir.student.model.entity.Student;
import com.kabir.student.repository.StudentRepository;
import com.kabir.student.service.StudentService;
import com.kabir.student.util.PageMapper;
import com.kabir.student.util.StudentSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
	private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
	private static final String DEFAULT_SORT = "fullName";
	private final StudentRepository studentRepository;

	@Override
	@Transactional
	public StudentResponse createStudent(StudentRequest request) {
		validateEmailUniqueness(request.getEmail(), null);
		Student student = StudentMapper.toEntity(request);
		Student saved = studentRepository.save(student);
		log.info("Created student with id {}", saved.getId());
		return StudentMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public StudentResponse getStudent(Long id) {
		Student student = studentRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new StudentNotFoundException(id));
		return StudentMapper.toResponse(student);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<StudentResponse> listStudents(String branch, Integer yop, int page, int size, String sortField, String sortDirection) {
		Pageable pageable = PageRequest.of(page, size, buildSort(sortField, sortDirection));
		Specification<Student> spec = Specification.where(StudentSpecifications.isActive())
				.and(StudentSpecifications.hasBranch(normalize(branch)))
				.and(StudentSpecifications.hasYop(yop));

		Page<StudentResponse> result = studentRepository.findAll(spec, pageable)
				.map(StudentMapper::toResponse);
		return PageMapper.from(result);
	}

	@Override
	@Transactional
	public StudentResponse updateStudent(Long id, StudentRequest request) {
		Student student = studentRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new StudentNotFoundException(id));

		validateEmailUniqueness(request.getEmail(), id);

		student.setFullName(request.getFullName());
		student.setEmail(request.getEmail());
		student.setPhone(request.getPhone());
		student.setBranch(request.getBranch());
		student.setYop(request.getYop());
		Student saved = studentRepository.save(student);
		log.info("Updated student {}", saved.getId());
		return StudentMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public StudentResponse patchStudent(Long id, StudentPatchRequest request) {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new StudentNotFoundException(id));

		if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(student.getEmail())) {
			validateEmailUniqueness(request.getEmail(), id);
			student.setEmail(request.getEmail());
		}
		if (request.getFullName() != null) {
			student.setFullName(request.getFullName());
		}
		if (request.getPhone() != null) {
			student.setPhone(request.getPhone());
		}
		if (request.getBranch() != null) {
			student.setBranch(request.getBranch());
		}
		if (request.getYop() != null) {
			student.setYop(request.getYop());
		}
		if (request.getActive() != null) {
			student.setActive(request.getActive());
		}
		Student saved = studentRepository.save(student);
		log.info("Patched student {}", saved.getId());
		return StudentMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public void deleteStudent(Long id, boolean hardDelete) {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new StudentNotFoundException(id));

		if (hardDelete) {
			studentRepository.delete(student);
			log.warn("Hard deleted student {}", id);
			return;
		}

		if (Boolean.FALSE.equals(student.getActive())) {
			log.info("Student {} is already inactive", id);
			return;
		}

		student.deactivate();
		studentRepository.save(student);
		log.info("Soft deleted student {}", id);
	}

	private void validateEmailUniqueness(String email, Long currentId) {
		boolean exists = currentId == null
				? studentRepository.existsByEmailIgnoreCase(email)
				: studentRepository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
		if (exists) {
			throw new IllegalArgumentException("Email is already registered");
		}
	}

	private Sort buildSort(String field, String direction) {
		String sortField = StringUtils.hasText(field) ? field : DEFAULT_SORT;
		Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
		return Sort.by(sortDirection, sortField);
	}

	private String normalize(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}

