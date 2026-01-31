package com.devcommunity.backend.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.devcommunity.controller.ResponseController;
import com.devcommunity.dto.ResponseRequestDTO;
import com.devcommunity.dto.ResponseResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for ResponseController
 * Covers success and failure scenarios for all endpoints
 */
@WebMvcTest(ResponseController.class)
class ResponseControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private IResponseService service;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JWTService jwtService;
	
	private ResponseRequestDTO reqDTO;
	private ResponseResponseDTO resDTO;
	
	@BeforeEach
	void setUp() {
		reqDTO = new ResponseRequestDTO();
		reqDTO.setAnswer("Java is a programming language");
		reqDTO.setDeveloperId(1);
		reqDTO.setPostId(5);
		
		resDTO = new ResponseResponseDTO();
		resDTO.setAnswer("Java is a programming language");
		resDTO.setRespDateTime(LocalDateTime.now());
		resDTO.setRespId(2);
		resDTO.setDeveloperId(1);
		resDTO.setPostId(5);
	}
	
	@Test
	void testGetAllResponsesByPost_Success() throws Exception {
		when(service.getAllResponseByPost(anyInt())).thenReturn(List.of(resDTO));
		
		mockMvc.perform(get("/api/responses/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].answer").value("Java is a programming language"));
	}
	@Test
	void testGetAllResponsesByPost_Failure() throws Exception {
		when(service.getAllResponseByPost(anyInt()))
		.thenThrow(new DeveloperCommunityException("There are no responses for this post"));
		
		mockMvc.perform(get("/api/responses/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("There are no responses for this post"));
	}
	
	@Test
	void testGetAllResponsesByDev_Success() throws Exception {
		when(service.getAllResponseByDeveloper(anyInt())).thenReturn(List.of(resDTO));
		
		mockMvc.perform(get("/api/responses/developer/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].answer").value("Java is a programming language"));
	}
	@Test
	void testGetAllResponsesByDev_Failure() throws Exception {
		when(service.getAllResponseByDeveloper(anyInt()))
		.thenThrow(new DeveloperCommunityException("There are no responses by this developer"));
		
		mockMvc.perform(get("/api/responses/developer/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("There are no responses by this developer"));
	}
	
	@Test
	void testAddResponse() throws Exception {
		when(service.addResponse(reqDTO)).thenReturn("The response has been added");
		
		mockMvc.perform(post("/api/responses/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(content().string("The response has been added"));
	}
	
	@Test
	void testUpdateResponse_Success() throws Exception {
		when(service.updateResponse(1, reqDTO)).thenReturn("The response has been updated");
		
		mockMvc.perform(put("/api/responses/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The response has been updated"));
	}
	@Test
	void testUpdateResponse_Failure() throws Exception {
		when(service.updateResponse(1, reqDTO))
		.thenThrow(new DeveloperCommunityException("Response does not exist"));
		
		mockMvc.perform(put("/api/responses/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Response does not exist"));
	}
	
	@Test
	void testDeleteResponse_Success() throws Exception {
		when(service.removeResponse(anyInt())).thenReturn("The response has been deleted");
		
		mockMvc.perform(delete("/api/responses/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The response has been deleted"));
	}
	@Test
	void testDeleteResponse_Failure() throws Exception {
		when(service.removeResponse(anyInt()))
		.thenThrow(new DeveloperCommunityException("Response does not exist"));
		
		mockMvc.perform(delete("/api/responses/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Response does not exist"));
	}
	
	@Test
	void testDeleteMultipleResponse_Success() throws Exception {
		when(service.removeMultipleResponse(List.of(2, 3))).thenReturn("The responses have been deleted");
		
		mockMvc.perform(delete("/api/responses/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[2, 3]")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The responses have been deleted"));
	}
	@Test
	void testDeleteMultipleResponse_Failure() throws Exception {
		when(service.removeMultipleResponse(List.of(2, 3)))
		.thenThrow(new DeveloperCommunityException("Response ID: 2 does not exist"));
		
		mockMvc.perform(delete("/api/responses/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[2, 3]")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Response ID: 2 does not exist"));
	}
}
