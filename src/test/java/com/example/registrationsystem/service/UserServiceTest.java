package com.example.registrationsystem.service;

import com.example.registrationsystem.model.User;
import com.example.registrationsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByUsername() {
        User user = new User();
        user.setName("ivan");

        when(userRepository.findByName("ivan")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("ivan");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("ivan");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with name: unknown");
    }

    @Test
    void testGetUsersNameFilter() {
        User user = new User();
        user.setName("ivan");
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findByNameContainingIgnoreCase("ivan", pageable)).thenReturn(usersPage);

        Page<User> result = userService.getUsers("ivan", pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).isEqualTo("ivan");
    }

    @Test
    void testGetUsersWithoutNameFilter() {
        User user = new User();
        user.setName("ivan");
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        Page<User> result = userService.getUsers(null, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).isEqualTo("ivan");
    }

    @Test
    void testAssignRole() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.assignRole(1L, "admin");

        assertThat(result).isNotNull();
        assertThat(result.getRoles()).contains("ADMIN");
    }

    @Test
    void testAssignRoleNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRole(1L, "admin"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with ID: 1");
    }

    @Test
    void testAssignRoleEmptyRole() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.assignRole(1L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role cannot be empty");
    }
}
