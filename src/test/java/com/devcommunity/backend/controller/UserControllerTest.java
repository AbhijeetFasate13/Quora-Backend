package com.devcommunity.backend.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import com.devcommunity.backend.config.NoSecurityConfig;
import com.devcommunity.controller.UserController;
import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.exception.ErrorResponse;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(NoSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
	private JWTService jwtService;
    
    private User user;
    private UserRequestDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User("John", "Password@123");
        userDTO = new UserRequestDTO("John", "Password@123");
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(service.getUser(1)).thenReturn(user);

        mockMvc.perform(get("/api/user/id/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("John"))
            .andExpect(jsonPath("$.password").value("Password@123"));
    }

    @Test
    void testUpdateUserById_Success() throws Exception {
        when(service.updateUser(anyInt(), any(UserRequestDTO.class))).thenReturn("User updated successfully");

        mockMvc.perform(put("/api/user/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("User updated successfully"));
    }

    @Test
    void testDeleteUserById_Success() throws Exception {
        when(service.deleteUser(1)).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/api/user/delete/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(service.getUser(99)).thenThrow(new DeveloperCommunityException("User with 99 not found."));

        mockMvc.perform(get("/api/user/id/99"))
            .andExpect(status().is(400))
            .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(LocalDate.now(), "User with 99 not found."))));
    }
}