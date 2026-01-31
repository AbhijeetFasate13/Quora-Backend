package com.devcommunity.backend.controller;

import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.ArgumentMatchers.anyString;
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

import com.devcommunity.controller.PostController;
import com.devcommunity.dto.PostRequestDTO;
import com.devcommunity.dto.PostResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IPostService;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Unit tests for PostController
 * Covers success and failure scenarios for all endpoints
 */
@WebMvcTest(PostController.class)
class PostControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private IPostService service;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JWTService jwtService;
		
	private PostRequestDTO reqDTO;
	private PostResponseDTO resDTO;
	
	@BeforeEach
	void setUp() {
		reqDTO = new PostRequestDTO();
		reqDTO.setQuery("What is Java?");
		reqDTO.setTopic("Java");
		reqDTO.setDeveloperId(1);
		
		resDTO = new PostResponseDTO();
		resDTO.setQuery("What is Java?");
		resDTO.setTopic("Java");
		resDTO.setPostDateTime(LocalDateTime.now());
		resDTO.setPostId(5);
		resDTO.setDeveloperId(1);
	}
	
	@Test
	void testGetAllPosts_Success() throws Exception {
		when(service.getAllPost()).thenReturn(List.of(resDTO));
		

		mockMvc.perform(get("/api/posts/all")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].query").value("What is Java?"));
	}
	@Test
	void testGetAllPosts_Failure() throws Exception {
		when(service.getAllPost())
		.thenThrow(new DeveloperCommunityException("There are no posts to display"));
		

		mockMvc.perform(get("/api/posts/all")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("There are no posts to display"));
	}
	
	@Test
	void testGetPostById_Success() throws Exception {
		when(service.getPostById(anyInt())).thenReturn(resDTO);
		
		mockMvc.perform(get("/api/posts/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.query").value("What is Java?"));
	}
	@Test
	void testGetPostById_Failure() throws Exception {
		when(service.getPostById(anyInt()))
		.thenThrow(new DeveloperCommunityException("Post does not exist"));
		

		mockMvc.perform(get("/api/posts/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Post does not exist"));
	}
	
	@Test
	void testGetPostByDev_Success() throws Exception {
		when(service.getAllPostByDev(anyInt())).thenReturn(List.of(resDTO));
		
		mockMvc.perform(get("/api/posts/developer/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].query").value("What is Java?"));
	}
	@Test
	void testGetPostByDev_Failure() throws Exception {
		when(service.getAllPostByDev(anyInt()))
		.thenThrow(new DeveloperCommunityException("There are no posts by this Developer"));
		

		mockMvc.perform(get("/api/posts/developer/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("There are no posts by this Developer"));
	}
	
	@Test
	void testSearchPostByKeyword_Success() throws Exception {
		when(service.getPostByKeyword(anyString())).thenReturn(List.of(resDTO));
		
		mockMvc.perform(get("/api/posts/topic/Java")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].query").value("What is Java?"));
	}
	@Test
	void testSearchPostByKeyword_Failure() throws Exception {
		when(service.getPostByKeyword(anyString()))
		.thenThrow(new DeveloperCommunityException("There are no posts with this keyword"));
		

		mockMvc.perform(get("/api/posts/topic/Java")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("There are no posts with this keyword"));		
	}
	
	@Test
	void testAddPost() throws Exception {
		when(service.addPost(reqDTO)).thenReturn("The post has been added");
		
		mockMvc.perform(post("/api/posts/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(content().string("The post has been added"));
	}
	
	@Test
	void testUpdatePost_Success() throws Exception {
		when(service.updatePost(1, reqDTO)).thenReturn("The post has been updated");
		
		mockMvc.perform(put("/api/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The post has been updated"));
	}
	@Test
	void testUpdatePost_Failure() throws Exception {
		when(service.updatePost(1, reqDTO))
		.thenThrow(new DeveloperCommunityException("Post does not exist"));
		

		mockMvc.perform(put("/api/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDTO))
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Post does not exist"));	
	}
	
	@Test
	void testDeletePost_Success() throws Exception {
		when(service.removePost(anyInt())).thenReturn("The post has been deleted");
		
		mockMvc.perform(delete("/api/posts/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The post has been deleted"));
	}
	@Test
	void testDeletePost_Failure() throws Exception {
		when(service.removePost(anyInt()))
		.thenThrow(new DeveloperCommunityException("The post has been deleted"));
		

		mockMvc.perform(delete("/api/posts/1")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("The post has been deleted"));	
	}
	
	@Test
	void testDeleteMultiplePost_Success() throws Exception {
		when(service.removeMultiplePost(List.of(1, 2))).thenReturn("The posts have been deleted");
		
		mockMvc.perform(delete("/api/posts/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[1, 2]")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("The posts have been deleted"));
	}
	@Test
	void testDeleteMultiplePost_Failure() throws Exception {
		when(service.removeMultiplePost(List.of(2, 3)))
		.thenThrow(new DeveloperCommunityException("Post ID: 1 does not exist"));
		

		mockMvc.perform(delete("/api/posts/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[2, 3]")
				.with(user("testUser").roles("DEVELOPER"))
				.with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Post ID: 1 does not exist"));	
	}
}
