package com.devcommunity.controller;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.VoteRequestDTO;
import com.devcommunity.dto.VoteResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IVoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Vote Controller", description = "Manages voting actions on posts, responses, and comments including adding, deleting, and retrieving votes.")

public class VoteController {


    // Logger for tracking and debugging
    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);

   
    private final IVoteService voteService;
    
    // Constructor injection for vote service
     public VoteController(IVoteService voteService) {
		super();
		this.voteService = voteService;
	}

    //Adds a new vote (upvote or downvote) to a post, response, or comment.
     
    @PostMapping("/addVote")
    @Operation(summary = "Add a new vote", description = "Allows a developer to upvote or downvote a post, response, or comment.")
    public ResponseEntity<VoteResponseDTO> addVote(@Valid @RequestBody VoteRequestDTO voteDTO) throws DeveloperCommunityException 
    {
    logger.info("Adding vote: {}", voteDTO);
    VoteResponseDTO responseDTO = voteService.addVote(voteDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

   
    @DeleteMapping("/deleteVote/{voteId}")
    @Operation(summary = "Delete a vote", description = "Deletes a vote by its unique vote ID.")
    public String deleteVote(@PathVariable int voteId) throws DeveloperCommunityException {
    	logger.info("Deleting vote with ID: {}", voteId);
    	voteService.deleteVote(voteId);
        return "Vote deleted successfully for id: "+voteId;
    }

   
    @GetMapping("/getVotesByPostId/{postId}")
    @Operation(summary = "Get votes for a post", description = "Retrieves all votes associated with a specific post.")
    public List<VoteResponseDTO> getVotesByPostId(@PathVariable int postId) {
        logger.info("Fetching votes for post ID: {}", postId);
        return voteService.getVotesByPostId(postId);
    }

   
    @GetMapping("/getVotesByResponseId/{responseId}")
    @Operation(summary = "Get votes for a response", description = "Retrieves all votes associated with a specific response.")
    public List<VoteResponseDTO> getVotesByRespId(@PathVariable int responseId) {
    	 logger.info("Fetching votes for response ID: {}", responseId);
        return voteService.getVotesByRespId(responseId);
    }
    
   
    @GetMapping("/comments/{commentId}/votes")
    @Operation(summary = "Get votes for a comment", description = "Retrieves all votes associated with a specific comment.")
    public ResponseEntity<List<VoteResponseDTO>> getVotesByCommentId(@PathVariable int commentId) {
    	logger.info("Fetching votes for comment ID: {}", commentId);
    	List<VoteResponseDTO> votes = voteService.getVotesByCommentId(commentId);
        return ResponseEntity.ok(votes);
    }
   
  
    @GetMapping("/getVoteById/{voteId}")
    @Operation(summary = "Get vote by ID", description = "Fetches a single vote using its unique vote ID.")
    public Optional<VoteResponseDTO> getVoteById(@PathVariable int voteId) 
    {
    	logger.info("Fetching vote with ID: {}", voteId);
        return voteService.getVoteByVoteId(voteId);
    }
    
   
    
    @GetMapping("/comments/{commentId}/votes/count")
    @Operation(summary = "Count votes by type for a comment", description = "Returns the number of upvotes or downvotes for a specific comment.")
    public ResponseEntity<String> getVotesByType(@Valid  @RequestParam String voteType, @PathVariable int commentId) 
    {
    	logger.info("Counting '{}' votes for comment ID: {}", voteType, commentId);
        String count = voteService.countByVoteTypeAndComment_CommentId( voteType, commentId);
        return ResponseEntity.ok(count);
    }

}