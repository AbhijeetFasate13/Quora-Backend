package com.devcommunity.backend.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devcommunity.backend.config.NoSecurityConfig;
import com.devcommunity.controller.DeveloperController;
import com.devcommunity.dto.DeveloperRequestDTO;
import com.devcommunity.dto.DeveloperResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.exception.ErrorResponse;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IDeveloperService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeveloperController.class)
@Import(NoSecurityConfig.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDeveloperService service;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private JWTService jwtService;

    private DeveloperResponseDTO responseDTO;
    private DeveloperRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new DeveloperResponseDTO(
            1, "John", "Java", LocalDate.of(2023, 1, 1), 10,
            5, 3, 2, 7
        );

        requestDTO = new DeveloperRequestDTO("John", "Java");
    }

    @Test
    void testGetAllDevelopers_Success() throws Exception {
        when(service.getAllDevelopers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/dev/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].devName").value("John"))
            .andExpect(jsonPath("$[0].devSkill").value("Java"));
    }

    @Test
    void testGetDeveloperById_Success() throws Exception {
        when(service.getDeveloperById(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/dev/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.devName").value("John"))
            .andExpect(jsonPath("$.reputation").value(10));
    }

    @Test
    void testGetDeveloperById_NotFound() throws Exception {
        when(service.getDeveloperById(99)).thenThrow(new DeveloperCommunityException("Developer not found."));

        mockMvc.perform(get("/api/dev/99"))
            .andExpect(status().is(400))
            .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(LocalDate.now(), "Developer not found."))));
    }

    @Test
    void testGetDeveloperWithMaxReputation_Success() throws Exception {
        when(service.getByMaxReputation()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/dev/reputation/max"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reputation").value(10));
    }

    @Test
    void testGetDeveloperWithMaxReputation_Failure() throws Exception {
        when(service.getByMaxReputation()).thenThrow(new DeveloperCommunityException("There are no developers in the database."));

        mockMvc.perform(get("/api/dev/reputation/max"))
            .andExpect(status().is(400))
            .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(LocalDate.now(), "There are no developers in the database."))));
    }

    @Test
    void testSearchDevelopersBySkill_Success() throws Exception {
        when(service.searchDevelopersBySkill("Java")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/dev/skill/Java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].devSkill").value("Java"));
    }

    @Test
    void testSearchDevelopersBySkill_Empty() throws Exception {
        when(service.searchDevelopersBySkill("Unknown")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/dev/skill/Unknown"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAddDeveloper_Success() throws Exception {
        when(service.addDeveloper(anyInt(), any(DeveloperRequestDTO.class))).thenReturn("Developer added successfully!");

        mockMvc.perform(post("/api/dev/create/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("Developer added successfully!"));
    }

    @Test
    void testAddDeveloper_Failure() throws Exception {
        when(service.addDeveloper(anyInt(), any(DeveloperRequestDTO.class)))
            .thenThrow(new DeveloperCommunityException("User not found."));

        mockMvc.perform(post("/api/dev/create/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().is(400))
            .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(LocalDate.now(), "User not found."))));
    }

    @Test
    void testUpdateDeveloper_Success() throws Exception {
        when(service.updateDeveloper(anyInt(), any(DeveloperRequestDTO.class))).thenReturn("Developer updated successfully!");

        mockMvc.perform(put("/api/dev/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("Developer updated successfully!"));
    }

    @Test
    void testUpdateDeveloper_Failure() throws Exception {
        when(service.updateDeveloper(anyInt(), any(DeveloperRequestDTO.class)))
            .thenThrow(new DeveloperCommunityException("Developer not found."));

        mockMvc.perform(put("/api/dev/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().is(400))
            .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(LocalDate.now(), "Developer not found."))));
    }
}