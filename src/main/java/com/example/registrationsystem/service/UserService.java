package com.example.registrationsystem.service;

import com.example.registrationsystem.model.User;
import com.example.registrationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + name));
    }

    public Page<User> getUsers(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return userRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User assignRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        user.getRoles().add(role.toUpperCase());
        return userRepository.save(user);
    }
}
