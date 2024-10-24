package com.example.registrationsystem.controller;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.model.User;
import com.example.registrationsystem.service.RequestService;
import com.example.registrationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @GetMapping("/request")
    public Page<Request> getUserRequests(Principal principal, @PageableDefault(size = 5) Pageable pageable) {
        User user = userService.getUserByUsername(principal.getName());
        return requestService.getRequestsByUser(user, pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<Request> createRequest(Principal principal, @RequestParam String content) {
        User user = userService.getUserByUsername(principal.getName());
        Request request = requestService.createRequest(user, content);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Request> editDraftRequest(@PathVariable Long id, Principal principal, @RequestParam String newContent) {
        User user = userService.getUserByUsername(principal.getName());
        Request request = requestService.editDraftRequest(id, user, newContent);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<Request> submitRequest(@PathVariable Long id, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        Request request = requestService.sendRefinedRequest(id, user);
        return ResponseEntity.ok(request);
    }
}
