package com.example.registrationsystem.controller;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operator")
@PreAuthorize("hasRole('OPERATOR')")
public class OperatorController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/submitted")
    public Page<Request> getSubmittedRequests(@PageableDefault(size = 5) Pageable pageable) {
        return requestService.getSubmittedRequests(pageable);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Request> acceptRequest(@PathVariable Long id) {
        Request request = requestService.acceptRequest(id);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id) {
        Request request = requestService.rejectRequest(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/submitted/search")
    public Page<Request> searchSubmittedRequests(@RequestParam String userName, Pageable pageable) {
        return requestService.getRequestsByStatusAndUser(userName, pageable);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<String> viewRequestForOperator(@PathVariable Long id) {
        Request request = requestService.getRequestById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String formattedText = requestService.formatRequestTextForOperator(request.getContent());
        return ResponseEntity.ok(formattedText);
    }

}
