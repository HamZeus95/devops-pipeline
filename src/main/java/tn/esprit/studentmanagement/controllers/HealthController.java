package tn.esprit.studentmanagement.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class HealthController {
    @RequestMapping("/check")
    public String check() {
        return "Service is up and running!";
    }

}
