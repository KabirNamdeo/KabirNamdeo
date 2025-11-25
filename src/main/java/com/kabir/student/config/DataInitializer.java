package com.kabir.student.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.kabir.student.model.entity.Student;
import com.kabir.student.repository.StudentRepository;

@Configuration
@Profile("dev")
public class DataInitializer {
	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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
		};
	}
}

