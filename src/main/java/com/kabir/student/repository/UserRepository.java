package com.kabir.student.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kabir.student.model.entity.UserAccount;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
	boolean existsByEmailIgnoreCase(String email);

	Optional<UserAccount> findByEmailIgnoreCase(String email);
}

