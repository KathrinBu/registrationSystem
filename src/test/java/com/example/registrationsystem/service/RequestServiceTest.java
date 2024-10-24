package com.example.registrationsystem.service;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.model.RequestStatus;
import com.example.registrationsystem.model.User;
import com.example.registrationsystem.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRequestsByUser() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Request> requestPage = new PageImpl<>(Collections.singletonList(new Request()));

        when(requestRepository.findByUser(user, pageable)).thenReturn(requestPage);

        Page<Request> result = requestService.getRequestsByUser(user, pageable);

        assertThat(result).isNotEmpty();
    }

    @Test
    void testCreateRequest() {
        User user = new User();
        Request savedRequest = new Request();
        savedRequest.setUser(user);
        savedRequest.setContent("Test Content");
        savedRequest.setStatus(RequestStatus.DRAFT);
        savedRequest.setCreationDate(LocalDateTime.now());

        when(requestRepository.save(any(Request.class))).thenReturn(savedRequest);

        Request result = requestService.createRequest(user, "Test Content");

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getStatus()).isEqualTo(RequestStatus.DRAFT);
    }

    @Test
    void testGetSubmittedRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Request> requestPage = new PageImpl<>(Collections.singletonList(new Request()));

        when(requestRepository.findByStatus(RequestStatus.SUBMITTED, pageable)).thenReturn(requestPage);

        Page<Request> result = requestService.getSubmittedRequests(pageable);

        assertThat(result).isNotEmpty();
    }

    @Test
    void testAcceptRequest() {
        Request request = new Request();
        request.setStatus(RequestStatus.SUBMITTED);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.acceptRequest(1L);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    void testAcceptRequestNotSubmitted() {
        Request request = new Request();
        request.setStatus(RequestStatus.DRAFT);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> requestService.acceptRequest(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only submitted requests can be accepted");
    }

    @Test
    void testRejectRequest() {
        Request request = new Request();
        request.setStatus(RequestStatus.SUBMITTED);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.rejectRequest(1L);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    void testRejectRequestNotSubmitted() {
        Request request = new Request();
        request.setStatus(RequestStatus.DRAFT);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> requestService.rejectRequest(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only submitted requests can be rejected");
    }

    @Test
    void testEditDraftRequest() {
        User user = new User();
        Request request = new Request();
        request.setUser(user);
        request.setStatus(RequestStatus.DRAFT);

        when(requestRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.editDraftRequest(1L, user, "New Content");

        assertThat(result.getContent()).isEqualTo("New Content");
    }

    @Test
    void testSendRefinedRequest() {
        User user = new User();
        Request request = new Request();
        request.setUser(user);
        request.setStatus(RequestStatus.DRAFT);

        when(requestRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.sendRefinedRequest(1L, user);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.SUBMITTED);
    }

    @Test
    void testFormatRequestTextForOperator() {
        String formattedText = requestService.formatRequestTextForOperator("Hello");
        assertThat(formattedText).isEqualTo("H-e-l-l-o");
    }

    @Test
    void testGetRequestById() {
        Request request = new Request();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        Optional<Request> result = requestService.getRequestById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(request);
    }
}
