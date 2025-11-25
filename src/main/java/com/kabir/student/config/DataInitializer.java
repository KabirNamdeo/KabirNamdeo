package com.kabir.student.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kabir.student.model.entity.Student;
import com.kabir.student.model.entity.UserAccount;
import com.kabir.student.repository.StudentRepository;
import com.kabir.student.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer {
	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Bean
	CommandLineRunner seedStudents(StudentRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				List<Student> students = List.of(
						Student.builder().fullName("Aisha Verma").email("aisha.verma@example.com").phone("+91-9000011111").branch("Computer Science").yop(2023).active(true).build(),
						Student.builder().fullName("Kabir Ahmed").email("kabir.ahmed@example.com").phone("+91-9000022222").branch("Electronics").yop(2022).active(true).build(),
						Student.builder().fullName("Zoya Khan").email("zoya.khan@example.com").phone("+91-9000033333").branch("Mechanical").yop(2024).active(true).build());
				repository.saveAll(students);
				log.info("Seeded {} sample students", students.size());
			}

			if (userRepository.count() == 0) {
				userRepository.save(UserAccount.builder()
						.fullName("Demo Admin")
						.email("admin@college.local")
						.passwordHash(passwordEncoder.encode("Admin@123"))
						.role(UserAccount.Role.ADMIN)
						.build());
				log.info("Seeded default admin user (admin@college.local / Admin@123)");
			}
		};
	}
}

