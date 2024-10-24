package com.example.registrationsystem.controller;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperatorController.class)
class OperatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private Request request;
    private Page<Request> requestPage;

    @BeforeEach
    void setUp() {
        request = new Request();
        request.setId(1L);
        request.setContent("Sample content");

        requestPage = new PageImpl<>(Collections.singletonList(request), PageRequest.of(0, 5), 1);
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getSubmittedRequestsReturnPageOfRequests() throws Exception {
        when(requestService.getSubmittedRequests(any(Pageable.class))).thenReturn(requestPage);

        mockMvc.perform(get("/operator/submitted")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(request.getId()))
                .andExpect(jsonPath("$.content[0].content").value(request.getContent()));
    }


    @Test
    @WithMockUser(roles = "OPERATOR")
    void acceptRequestReturnAcceptedRequest() throws Exception {
        when(requestService.acceptRequest(1L)).thenReturn(request);

        mockMvc.perform(post("/operator/accept/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void rejectRequestReturnRejectedRequest() throws Exception {
        when(requestService.rejectRequest(1L)).thenReturn(request);

        mockMvc.perform(post("/operator/reject/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void searchSubmittedRequestsReturnFilteredPageOfRequests() throws Exception {
        when(requestService.getRequestsByStatusAndUser(anyString(), any(Pageable.class))).thenReturn(requestPage);

        mockMvc.perform(get("/operator/submitted/search")
                        .param("userName", "testUser")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(request.getId()))
                .andExpect(jsonPath("$.content[0].content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void viewRequestForOperatorReturnFormattedText() throws Exception {
        when(requestService.getRequestById(1L)).thenReturn(Optional.of(request));
        when(requestService.formatRequestTextForOperator(request.getContent())).thenReturn("Formatted content");

        mockMvc.perform(get("/operator/view/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Formatted content"));
    }
}
