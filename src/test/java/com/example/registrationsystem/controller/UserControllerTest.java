package com.example.registrationsystem.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.model.RequestStatus;
import com.example.registrationsystem.model.User;
import com.example.registrationsystem.service.RequestService;
import com.example.registrationsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserService userService;


    private User user;
    private Request request;
    private Page<Request> requestPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "testUser", Collections.singleton("password"));
        request = new Request(1L, user, RequestStatus.SUBMITTED, "Test request content", LocalDateTime.now());
        requestPage = new PageImpl<>(List.of(request));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserRequestsReturnPageOfRequests() throws Exception {

        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(requestService.getRequestsByUser(any(User.class), any(Pageable.class))).thenReturn(requestPage);

        mockMvc.perform(get("/user/request")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(request.getId()))
                .andExpect(jsonPath("$.content[0].content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRequestCreatedRequest() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(requestService.createRequest(any(User.class), anyString())).thenReturn(request);

        mockMvc.perform(post("/user/create")
                        .param("content", "Test request content")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void editDraftRequestEditedRequest() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(requestService.editDraftRequest(anyLong(), any(User.class), anyString())).thenReturn(request);

        mockMvc.perform(put("/user/edit/{id}", 1L)
                        .param("newContent", "Updated request content")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitRequestReturnSubmittedRequest() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(requestService.sendRefinedRequest(anyLong(), any(User.class))).thenReturn(request);

        mockMvc.perform(post("/user/submit/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.content").value(request.getContent()));
    }
}

