package tn.esprit.studentmanagement.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Application Health and Performance Tests")
class ApplicationHealthTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should start application context successfully")
    void shouldStartApplicationContext() {
        // Test that the application starts without errors
        assertThat(port).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should respond to health check requests")
    void shouldRespondToHealthCheck() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/students/getAllStudents", 
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should handle repeated requests efficiently")
    void shouldHandleRepeatedRequests() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/student/students/getAllStudents", 
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Ensure all 10 requests complete within 10 seconds (reasonable for integration test)
        assertThat(duration).isLessThan(10000);
    }
}