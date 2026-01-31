package com.devcommunity.backend.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devcommunity.controller.CommentController;
import com.devcommunity.dto.CommentRequestDTO;
import com.devcommunity.dto.CommentResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.ICommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("removal")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICommentService commentService;

   @MockBean
   private JWTService jwtService;


    @Autowired
    private ObjectMapper objectMapper;

    private CommentRequestDTO requestDTO;
    private CommentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
    	
       requestDTO = new CommentRequestDTO();
       requestDTO.setCommentId(1); //Required for update
  	   requestDTO.setText("Helpful explanation");
       requestDTO.setDeveloperId(1); //  Required for authorization
       requestDTO.setPostId(100);    // Optional, but safe to include
    

        responseDTO = new CommentResponseDTO();
        responseDTO.setCommentId(1);
        responseDTO.setText("Helpful explanation");
        responseDTO.setDeveloperId(1);
        responseDTO.setDeveloperName("Hema");
        responseDTO.setPostId(100);
    }

    
    @Test
    void testAddComment_Success() throws Exception {
        when(commentService.addComment(any(CommentRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/comments/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isCreated()) //  expects 201
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.text").value("Helpful explanation"));
    }

    
    @Test
    void testAddComment_InvalidInput() throws Exception {
        CommentRequestDTO invalidDTO = new CommentRequestDTO(); // missing required fields

        mockMvc.perform(post("/api/comments/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void testGetByCommentId_Success() throws Exception {
        when(commentService.getByCommentId(anyInt())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/comments/1")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.text").value("Helpful explanation"));
    }

    @Test
    void testGetByCommentId_NotFound() throws Exception {
        when(commentService.getByCommentId(999)).thenThrow(new DeveloperCommunityException("Comment not found"));

        mockMvc.perform(get("/api/comments/999")
        		.with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void testGetCommentsByPostId_Success() throws Exception {
        when(commentService.getCommentsByPostId(anyInt())).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/comments/post/100")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].commentId").value(1));
    }
    
    @Test
    void testGetCommentsByPostId_NotFound() throws Exception {
        when(commentService.getCommentsByPostId(999)).thenThrow(new DeveloperCommunityException("Post not found"));

        mockMvc.perform(get("/api/comments/post/999")
        		.with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetCommentsByResponseId_Success() throws Exception {
        List<CommentResponseDTO> responseList = List.of(responseDTO);

        when(commentService.getCommentsByResponseId(1)).thenReturn(responseList);

        mockMvc.perform(get("/api/comments/response/1")
        		.with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1))
                .andExpect(jsonPath("$[0].text").value("Helpful explanation"));
    }

    @Test
    void testGetCommentsByResponseId_NotFound() throws Exception {
        when(commentService.getCommentsByResponseId(999)).thenThrow(new DeveloperCommunityException("Response not found"));

        mockMvc.perform(get("/api/comments/response/999")
        		.with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

   
    @Test
    void testUpdateComment_Success() throws Exception {
        when(commentService.updateComment(any(CommentRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/comments/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.text").value("Helpful explanation"));
    }


    @Test
    void testUpdateComment_ServiceException() throws Exception {
        when(commentService.updateComment(any())).thenThrow(new DeveloperCommunityException("Update failed"));

        mockMvc.perform(put("/api/comments/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void testDeleteComment_Success() throws Exception {
        when(commentService.removeComment(anyInt())).thenReturn(responseDTO);
        mockMvc.perform(delete("/api/comments/remove/1")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1));
    }
    
    
    @Test
    void testDeleteComment_NotFound() throws Exception {
        when(commentService.removeComment(999)).thenThrow(new DeveloperCommunityException("Comment not found"));

        mockMvc.perform(delete("/api/comments/remove/999")
        		.with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testCountVotesByType_Success() throws Exception {
    	when(commentService.getNoOfVotesOnCommentByVoteType(anyString(), anyInt())).thenReturn("5");


        mockMvc.perform(get("/api/comments/votetype/count")
                .param("voteType", "UPVOTE")
                .param("commentId", "1")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
    
 
}
