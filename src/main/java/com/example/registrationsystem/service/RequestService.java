package com.example.registrationsystem.service;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.model.RequestStatus;
import com.example.registrationsystem.model.User;
import com.example.registrationsystem.repository.RequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public Page<Request> getRequestsByUser(User user, Pageable pageable) {
        return requestRepository.findByUser(user, pageable);
    }

    public Request createRequest(User user, String content) {
        Request request = new Request();
        request.setUser(user);
        request.setContent(content);
        request.setStatus(RequestStatus.DRAFT);
        request.setCreationDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public Page<Request> getSubmittedRequests(Pageable pageable) {
        return requestRepository.findByStatus(RequestStatus.SUBMITTED, pageable);
    }

    public Request acceptRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStatus().equals(RequestStatus.SUBMITTED)) {
            throw new IllegalStateException("Only submitted requests can be accepted");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        return requestRepository.save(request);
    }

    public Request rejectRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStatus().equals(RequestStatus.SUBMITTED)) {
            throw new IllegalStateException("Only submitted requests can be rejected");
        }

        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }

    public Page<Request> getRequestsByStatusAndUser(String userName, Pageable pageable) {
        return requestRepository.findByUserIgnoreCaseAndStatus(userName, RequestStatus.SUBMITTED, pageable);
    }

    public Request editDraftRequest(Long requestId, User user, String newContent) {
        Request request = requestRepository.findByIdAndUser(requestId, user)
                .orElseThrow(() -> new RuntimeException("Draft request not found or not authorized"));

        if (!request.getStatus().equals(RequestStatus.DRAFT)) {
            throw new IllegalStateException("Only draft requests can be edited");
        }

        request.setContent(newContent);
        return requestRepository.save(request);
    }

    public Request sendRefinedRequest(Long requestId, User user) {
        Request request = requestRepository.findByIdAndUser(requestId, user)
                .orElseThrow(() -> new RuntimeException("Request not found or not authorized"));

        if (!request.getStatus().equals(RequestStatus.DRAFT)) {
            throw new IllegalStateException("Only draft requests can be submitted");
        }

        request.setStatus(RequestStatus.SUBMITTED);
        return requestRepository.save(request);
    }

    public String formatRequestTextForOperator(String text) {
        return text.chars()
                .mapToObj(c -> (char) c + "-")
                .collect(Collectors.joining()).replaceAll("-$", "");
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }
}
