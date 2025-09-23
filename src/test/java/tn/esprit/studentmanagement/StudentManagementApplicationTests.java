package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.studentmanagement.controllers.StudentController;
import tn.esprit.studentmanagement.services.StudentService;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StudentManagementApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private StudentController studentController;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentRepository studentRepository;

    @Test
    void contextLoads() {
        // Verify application context loads successfully
        assertThat(applicationContext).isNotNull();
    }
    
    @Test
    void controllersAreLoaded() {
        // Verify all controllers are properly loaded
        assertThat(studentController).isNotNull();
    }
    
    @Test
    void servicesAreLoaded() {
        // Verify all services are properly loaded
        assertThat(studentService).isNotNull();
    }
    
    @Test
    void repositoriesAreLoaded() {
        // Verify all repositories are properly loaded
        assertThat(studentRepository).isNotNull();
    }
}
