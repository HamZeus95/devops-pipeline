package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Student Service Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setIdStudent(1L);
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setEmail("john.doe@example.com");
        testStudent.setPhone("+1234567890");
        testStudent.setDateOfBirth(LocalDate.of(2000, 1, 15));
        testStudent.setAddress("123 Main St");
    }

    @Test
    @DisplayName("Should return all students when getAllStudents is called")
    void shouldReturnAllStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getAllStudents();

        // Then
        assertThat(actualStudents).isNotNull();
        assertThat(actualStudents).hasSize(1);
        assertThat(actualStudents.get(0).getFirstName()).isEqualTo("John");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return student when valid ID is provided")
    void shouldReturnStudentById() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When
        Student foundStudent = studentService.getStudentById(1L);

        // Then
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getFirstName()).isEqualTo("John");
        assertThat(foundStudent.getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when student ID does not exist")
    void shouldReturnNullWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Student foundStudent = studentService.getStudentById(999L);

        // Then
        assertThat(foundStudent).isNull();
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should save student successfully")
    void shouldSaveStudent() {
        // Given
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        Student savedStudent = studentService.saveStudent(testStudent);

        // Then
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getFirstName()).isEqualTo("John");
        verify(studentRepository, times(1)).save(testStudent);
    }

    @Test
    @DisplayName("Should delete student by ID")
    void shouldDeleteStudent() {
        // Given
        doNothing().when(studentRepository).deleteById(anyLong());

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository, times(1)).deleteById(1L);
    }
}