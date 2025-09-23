package tn.esprit.studentmanagement.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.studentmanagement.entities.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Student Repository Tests")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setFirstName("Alice");
        testStudent.setLastName("Wonder");
        testStudent.setEmail("alice.wonder@example.com");
        testStudent.setPhone("+1111111111");
        testStudent.setDateOfBirth(LocalDate.of(1998, 3, 15));
        testStudent.setAddress("123 Wonderland");
    }

    @Test
    @DisplayName("Should save and retrieve student successfully")
    void shouldSaveAndRetrieveStudent() {
        // When
        Student savedStudent = studentRepository.save(testStudent);

        // Then
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getIdStudent()).isNotNull();
        assertThat(savedStudent.getFirstName()).isEqualTo("Alice");
        assertThat(savedStudent.getEmail()).isEqualTo("alice.wonder@example.com");
    }

    @Test
    @DisplayName("Should find student by ID")
    void shouldFindStudentById() {
        // Given
        Student savedStudent = studentRepository.save(testStudent);

        // When
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getIdStudent());

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getFirstName()).isEqualTo("Alice");
        assertThat(foundStudent.get().getEmail()).isEqualTo("alice.wonder@example.com");
    }

    @Test
    @DisplayName("Should return empty when student ID does not exist")
    void shouldReturnEmptyWhenStudentNotFound() {
        // When
        Optional<Student> foundStudent = studentRepository.findById(999L);

        // Then
        assertThat(foundStudent).isEmpty();
    }

    @Test
    @DisplayName("Should find all students")
    void shouldFindAllStudents() {
        // Given
        Student student2 = new Student();
        student2.setFirstName("Bob");
        student2.setLastName("Builder");
        student2.setEmail("bob.builder@example.com");
        student2.setPhone("+2222222222");
        student2.setDateOfBirth(LocalDate.of(1997, 6, 20));
        student2.setAddress("456 Construction Ave");

        studentRepository.save(testStudent);
        studentRepository.save(student2);

        // When
        List<Student> allStudents = studentRepository.findAll();

        // Then
        assertThat(allStudents).hasSize(2);
        assertThat(allStudents.stream().map(Student::getFirstName))
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudent() {
        // Given
        Student savedStudent = studentRepository.save(testStudent);
        Long studentId = savedStudent.getIdStudent();

        // When
        studentRepository.deleteById(studentId);

        // Then
        Optional<Student> deletedStudent = studentRepository.findById(studentId);
        assertThat(deletedStudent).isEmpty();
    }

    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudent() {
        // Given
        Student savedStudent = studentRepository.save(testStudent);

        // When
        savedStudent.setFirstName("Alice Updated");
        savedStudent.setEmail("alice.updated@example.com");
        Student updatedStudent = studentRepository.save(savedStudent);

        // Then
        assertThat(updatedStudent.getFirstName()).isEqualTo("Alice Updated");
        assertThat(updatedStudent.getEmail()).isEqualTo("alice.updated@example.com");
        assertThat(updatedStudent.getIdStudent()).isEqualTo(savedStudent.getIdStudent());
    }
}