package com.example.registrationsystem.controller;

import com.example.registrationsystem.model.User;
import com.example.registrationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Page<User> getUsers(@RequestParam(required = false) String name, Pageable pageable) {
        return userService.getUsers(name, pageable);
    }

    @PostMapping("/{userId}/assign-role")
    public ResponseEntity<User> assignRole(@PathVariable Long userId, @RequestParam String role) {
        User user = userService.assignRole(userId, role);
        return ResponseEntity.ok(user);
    }
}
