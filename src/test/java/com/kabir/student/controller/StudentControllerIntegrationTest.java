package com.kabir.student.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabir.student.StudentManagementCrudApiApplication;
import com.kabir.student.support.AbstractMySqlContainerTest;
import com.kabir.student.dto.StudentRequest;
import com.kabir.student.model.entity.Student;
import com.kabir.student.repository.StudentRepository;

@SpringBootTest(classes = StudentManagementCrudApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false"
})
class StudentControllerIntegrationTest extends AbstractMySqlContainerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private StudentRepository studentRepository;

	@BeforeEach
	void cleanDb() {
		studentRepository.deleteAll();
	}

	@Test
	void create_and_get_student_flow() throws Exception {
		StudentRequest request = baseRequest();

		String responseBody = mockMvc.perform(post("/api/students")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.fullName", is(request.getFullName())))
				.andReturn()
				.getResponse()
				.getContentAsString();

		Long studentId = objectMapper.readTree(responseBody).path("id").asLong();

		mockMvc.perform(get("/api/students/{id}", studentId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(studentId.intValue())))
				.andExpect(jsonPath("$.email", is(request.getEmail())));
	}

	@Test
	void list_students_supports_filters_and_pagination() throws Exception {
		studentRepository.save(Student.builder()
				.fullName("Aisha Verma")
				.email("aisha.verma@example.com")
				.phone("+91-9000000001")
				.branch("CSE")
				.yop(2023)
				.active(true)
				.build());

		studentRepository.save(Student.builder()
				.fullName("Kabir Ahmed")
				.email("kabir.ahmed@example.com")
				.phone("+91-9000000002")
				.branch("ECE")
				.yop(2022)
				.active(true)
				.build());

		mockMvc.perform(get("/api/students")
				.param("branch", "CSE")
				.param("yop", "2023")
				.param("page", "0")
				.param("size", "5")
				.param("sort", "fullName,asc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].branch", equalTo("CSE")))
				.andExpect(jsonPath("$.page", is(0)))
				.andExpect(jsonPath("$.size", is(5)));
	}

	@Test
	void patch_and_delete_student() throws Exception {
		Student student = studentRepository.save(Student.builder()
				.fullName("Zoya Khan")
				.email("zoya.khan@example.com")
				.phone("+91-9000000003")
				.branch("IT")
				.yop(2024)
				.active(true)
				.build());

		mockMvc.perform(patch("/api/students/{id}", student.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "phone": "+91-9000000123",
						  "active": true
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.phone", is("+91-9000000123")));

		mockMvc.perform(delete("/api/students/{id}", student.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/students/{id}", student.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	void put_updates_student() throws Exception {
		Student student = studentRepository.save(Student.builder()
				.fullName("Initial Name")
				.email("initial@example.com")
				.phone("+91-9000000999")
				.branch("ME")
				.yop(2021)
				.active(true)
				.build());

		StudentRequest updateRequest = StudentRequest.builder()
				.fullName("Updated Name")
				.email("updated@example.com")
				.phone("+91-9000000111")
				.branch("CE")
				.yop(2025)
				.build();

		mockMvc.perform(put("/api/students/{id}", student.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fullName", is("Updated Name")))
				.andExpect(jsonPath("$.branch", is("CE")));
	}

	private StudentRequest baseRequest() {
		return StudentRequest.builder()
				.fullName("Integration Test")
				.email("integration@example.com")
				.phone("+91-9000000000")
				.branch("CSE")
				.yop(2023)
				.build();
	}
}

