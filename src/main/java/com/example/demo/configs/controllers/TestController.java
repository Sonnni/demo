package com.example.demo.configs.controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "public API";
    }

    @GetMapping("/parent")
    @PreAuthorize("hasRole('PARENT') or hasRole('TEACHER')")
    public String userAccess() {
        return "parent API";
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public String adminAccess() {
        return "teacher API";
    }
}
