package com.kabir.student.util;

import org.springframework.data.jpa.domain.Specification;

import com.kabir.student.model.entity.Student;

public final class StudentSpecifications {

	private StudentSpecifications() {
	}

	public static Specification<Student> hasBranch(String branch) {
		return (root, query, cb) -> branch == null ? null : cb.equal(cb.lower(root.get("branch")), branch.toLowerCase());
	}

	public static Specification<Student> hasYop(Integer yop) {
		return (root, query, cb) -> yop == null ? null : cb.equal(root.get("yop"), yop);
	}

	public static Specification<Student> isActive() {
		return (root, query, cb) -> cb.isTrue(root.get("active"));
	}
}

