package com.example.secureapp.controller;

import com.example.secureapp.dto.UserResponse;
import com.example.secureapp.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final StudentService studentService;

    public AdminController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return studentService.allUsers();
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        studentService.deleteUser(id);
    }
}
