package com.devcommunity.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devcommunity.controller.VoteController;
import com.devcommunity.dto.VoteRequestDTO;
import com.devcommunity.dto.VoteResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IVoteService;
import com.devcommunity.util.VoteType;
import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("removal")
@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IVoteService voteService;

    @MockBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private VoteRequestDTO requestDTO;
    private VoteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new VoteRequestDTO();
        requestDTO.setVoteType(VoteType.UPVOTE);
        requestDTO.setDeveloperId(1);
        requestDTO.setPostId(100);

        responseDTO = new VoteResponseDTO();
        responseDTO.setVoteId(1);
        responseDTO.setVoteType(VoteType.UPVOTE);
        responseDTO.setDeveloperId(1);
        responseDTO.setPostId(100);
    }

    @Test
    void testAddVote_Success() throws Exception {
        when(voteService.addVote(any(VoteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/votes/addVote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voteId").value(1))
                .andExpect(jsonPath("$.voteType").value("UPVOTE"));
    }

    @Test
    void testAddVote_InvalidInput() throws Exception {
        VoteRequestDTO invalidDTO = new VoteRequestDTO();

        mockMvc.perform(post("/api/votes/addVote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteVote_Success() throws Exception {
        mockMvc.perform(delete("/api/votes/deleteVote/1")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Vote deleted successfully for id: 1"));
    }

    @Test
    void testDeleteVote_NotFound() throws Exception {
        when(voteService.deleteVote(999)).thenThrow(new DeveloperCommunityException("Vote not found"));

        mockMvc.perform(delete("/api/votes/deleteVote/999")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Vote not found"));
    }

    @Test
    void testGetVotesByPostId_Success() throws Exception {
        when(voteService.getVotesByPostId(100)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/votes/getVotesByPostId/100")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].voteId").value(1));
    }

    @Test
    void testGetVotesByResponseId_Success() throws Exception {
        when(voteService.getVotesByRespId(200)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/votes/getVotesByResponseId/200")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].voteId").value(1));
    }

    @Test
    void testGetVotesByCommentId_Success() throws Exception {
        when(voteService.getVotesByCommentId(100)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/votes/comments/100/votes")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].voteId").value(1));
    }

    @Test
    void testGetVoteById_Success() throws Exception {
        when(voteService.getVoteByVoteId(1)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/votes/getVoteById/1")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteId").value(1));
    }

    @Test
    void testCountVotesByType_Success() throws Exception {
        when(voteService.countByVoteTypeAndComment_CommentId("UPVOTE", 1)).thenReturn("5");

        mockMvc.perform(get("/api/votes/comments/1/votes/count")
                .param("voteType", "UPVOTE")
                .with(user("testUser").roles("DEVELOPER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }


}
