package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Student Controller Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        
        testStudent = new Student();
        testStudent.setFirstName("Jane");
        testStudent.setLastName("Smith");
        testStudent.setEmail("jane.smith@example.com");
        testStudent.setPhone("+1987654321");
        testStudent.setDateOfBirth(LocalDate.of(1999, 5, 20));
        testStudent.setAddress("456 Oak Avenue");
        
        testStudent = studentRepository.save(testStudent);
    }

    @Test
    @DisplayName("Should return all students with HTTP 200")
    void shouldGetAllStudents() throws Exception {
        mockMvc.perform(get("/students/getAllStudents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Jane")))
                .andExpect(jsonPath("$[0].lastName", is("Smith")))
                .andExpect(jsonPath("$[0].email", is("jane.smith@example.com")));
    }

    @Test
    @DisplayName("Should return specific student by ID with HTTP 200")
    void shouldGetStudentById() throws Exception {
        mockMvc.perform(get("/students/getStudent/{id}", testStudent.getIdStudent())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")));
    }

    @Test
    @DisplayName("Should create new student with HTTP 200")
    void shouldCreateStudent() throws Exception {
        Student newStudent = new Student();
        newStudent.setFirstName("Bob");
        newStudent.setLastName("Johnson");
        newStudent.setEmail("bob.johnson@example.com");
        newStudent.setPhone("+1555123456");
        newStudent.setDateOfBirth(LocalDate.of(2001, 8, 10));
        newStudent.setAddress("789 Pine Street");

        mockMvc.perform(post("/students/createStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Bob")))
                .andExpect(jsonPath("$.lastName", is("Johnson")))
                .andExpect(jsonPath("$.email", is("bob.johnson@example.com")));
    }

    @Test
    @DisplayName("Should update existing student with HTTP 200")
    void shouldUpdateStudent() throws Exception {
        testStudent.setFirstName("Jane Updated");
        testStudent.setEmail("jane.updated@example.com");

        mockMvc.perform(put("/students/updateStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jane Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")));
    }
}