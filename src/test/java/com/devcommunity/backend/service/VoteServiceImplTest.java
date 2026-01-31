package com.devcommunity.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devcommunity.dto.VoteRequestDTO;
import com.devcommunity.dto.VoteResponseDTO;
import com.devcommunity.entity.Comment;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.entity.Vote;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.ICommentRepo;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.repository.IVoteRepo;
import com.devcommunity.service.impl.VoteServiceImpl;
import com.devcommunity.util.VoteType;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock 
    private IVoteRepo voteRepo;
    @Mock 
    private IDevRepo devRepo;
    @Mock 
    private IPostRepo postRepo;
    @Mock 
    private IResponseRepo responseRepo;
    @Mock 
    private ICommentRepo commentRepo;
    @Mock 
    private ModelMapper modelMapper;

    @InjectMocks 
    private VoteServiceImpl voteServiceImpl;

    @Test
    void testGetVotesByPostId_Success() {
        Vote vote = new Vote();
        vote.setVoteType(VoteType.UPVOTE);
        vote.setPost(new Post());

        VoteResponseDTO dto = new VoteResponseDTO();
        dto.setVoteType(VoteType.UPVOTE);

        when(voteRepo.findByPost_PostId(1)).thenReturn(List.of(vote));
        when(modelMapper.map(vote, VoteResponseDTO.class)).thenReturn(dto);

        List<VoteResponseDTO> result = voteServiceImpl.getVotesByPostId(1);

        assertEquals(1, result.size());
        assertEquals(VoteType.UPVOTE, result.get(0).getVoteType());
    }


       @Test
       void testGetVotesByPostId_Failure() {
       when(voteRepo.findByPost_PostId(99)).thenReturn(Collections.emptyList());

       List<VoteResponseDTO> result = voteServiceImpl.getVotesByPostId(99);

       assertTrue(result.isEmpty());
       }


       @Test
       void testGetVotesByRespId_Success() {
           Vote vote = new Vote();
           vote.setVoteType(VoteType.UPVOTE);
           vote.setResponse(new Response());

           VoteResponseDTO dto = new VoteResponseDTO();
           dto.setVoteType(VoteType.UPVOTE);

           when(voteRepo.findByResponse_RespId(1)).thenReturn(List.of(vote));
           when(modelMapper.map(vote, VoteResponseDTO.class)).thenReturn(dto);

           List<VoteResponseDTO> result = voteServiceImpl.getVotesByRespId(1);

           assertEquals(1, result.size());
           assertEquals(VoteType.UPVOTE, result.get(0).getVoteType());
       }

       @Test
       void testGetVotesByRespId_Failure() {
           when(voteRepo.findByResponse_RespId(99)).thenReturn(Collections.emptyList());

           List<VoteResponseDTO> result = voteServiceImpl.getVotesByRespId(99);

           assertTrue(result.isEmpty());
       }

       @Test
       void testGetVotesByCommentId_Success() {
           Vote vote = new Vote();
           vote.setVoteType(VoteType.DOWNVOTE);
           vote.setComment(new Comment());

           VoteResponseDTO dto = new VoteResponseDTO();
           dto.setVoteType(VoteType.DOWNVOTE);

           when(voteRepo.findByComment_CommentId(1)).thenReturn(List.of(vote));
           when(modelMapper.map(vote, VoteResponseDTO.class)).thenReturn(dto);

           List<VoteResponseDTO> result = voteServiceImpl.getVotesByCommentId(1);

           assertEquals(1, result.size());
           assertEquals(VoteType.DOWNVOTE, result.get(0).getVoteType());
       }

       @Test
       void testGetVotesByCommentId_Failure() {
           when(voteRepo.findByComment_CommentId(99)).thenReturn(Collections.emptyList());

           List<VoteResponseDTO> result = voteServiceImpl.getVotesByCommentId(99);

           assertTrue(result.isEmpty());
       }
       
       @Test
       void testAddVote_Success() throws DeveloperCommunityException {
           // Arrange
           VoteRequestDTO voteDto = new VoteRequestDTO();
           voteDto.setDeveloperId(1);
           voteDto.setVoteType(VoteType.UPVOTE);
           voteDto.setPostId(101); // Voting on a post

           Developer developer = new Developer();
           developer.setId(1);

           Post post = new Post();
           post.setPostId(101);

           Vote vote = new Vote();
           vote.setVoteType(VoteType.UPVOTE);
           vote.setDeveloper(developer);
           vote.setPost(post);

           Vote savedVote = new Vote();
           savedVote.setVoteType(VoteType.UPVOTE);
           savedVote.setDeveloper(developer);
           savedVote.setPost(post);

           VoteResponseDTO responseDTO = new VoteResponseDTO();
           responseDTO.setVoteType(VoteType.UPVOTE);
           responseDTO.setDeveloperId(1);
           responseDTO.setPostId(101);

           // Mock dependencies
           when(devRepo.findById(1)).thenReturn(Optional.of(developer));
           when(postRepo.findById(101)).thenReturn(Optional.of(post));
           when(voteRepo.save(any(Vote.class))).thenReturn(savedVote);
           when(modelMapper.map(savedVote, VoteResponseDTO.class)).thenReturn(responseDTO);

           // Act
           VoteResponseDTO result = voteServiceImpl.addVote(voteDto);

           // Assert
           assertNotNull(result);
           assertEquals(VoteType.UPVOTE, result.getVoteType());
           assertEquals(1, result.getDeveloperId());
           assertEquals(101, result.getPostId());
       }


       
       public String deleteVote(int voteId) throws DeveloperCommunityException {
    	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	    if (auth == null || !auth.isAuthenticated()) {
    	        throw new DeveloperCommunityException("Not authenticated");
    	    }

    	    int developerId = Integer.parseInt(auth.getName());

    	    Vote vote = voteRepo.findById(voteId)
    	        .orElseThrow(() -> new DeveloperCommunityException("Vote not found"));

    	    if (vote.getDeveloper().getId() != developerId) {
    	        throw new DeveloperCommunityException("You are not authorized to delete this vote");
    	    }

    	    voteRepo.deleteById(voteId);
    	    return "Vote deleted successfully";
    	}


    @Test
    void testDeleteVote_Failure() {
        when(voteRepo.findById(99)).thenReturn(Optional.empty());

        DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
            voteServiceImpl.deleteVote(99);
        });

        assertEquals("Vote not found with ID: 99", exception.getMessage());
    }

    @Test
    void testGetVoteByVoteId_Success() {
        Vote vote = new Vote();
        vote.setVoteId(1);
        vote.setVoteType(VoteType.UPVOTE);

        VoteResponseDTO dto = new VoteResponseDTO();
        dto.setVoteType(VoteType.UPVOTE);

        when(voteRepo.findByVoteId(1)).thenReturn(Optional.of(vote));
        when(modelMapper.map(vote, VoteResponseDTO.class)).thenReturn(dto);

        Optional<VoteResponseDTO> result = voteServiceImpl.getVoteByVoteId(1);

        assertTrue(result.isPresent());
        assertEquals(VoteType.UPVOTE, result.get().getVoteType());
    }
  
    @Test
    void testCountByVoteTypeAndComment_CommentIdSuccess() {
        when(voteRepo.countByVoteTypeAndComment_CommentId(VoteType.UPVOTE, 1)).thenReturn(5);

        String result = voteServiceImpl.countByVoteTypeAndComment_CommentId("UPVOTE", 1);
        int idx = result.lastIndexOf(":");
        int count = Integer.parseInt(result.substring(idx+1).trim());

        assertEquals(5, count);
    }

    @Test
    void testCountByVoteTypeAndComment_CommentIdFailure() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            voteServiceImpl.countByVoteTypeAndComment_CommentId("INVALID_TYPE", 1);
        });

        assertEquals("Invalid vote type: INVALID_TYPE", exception.getMessage());
    }
}