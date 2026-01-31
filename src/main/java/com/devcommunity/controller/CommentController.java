package com.devcommunity.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.CommentRequestDTO;
import com.devcommunity.dto.CommentResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.ICommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Comment Controller", description = "Handles operations related to comments on posts and responses, including adding, updating, deleting, and retrieving comments.")

public class CommentController {


	// Logger for debugging and tracking
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	
    private final ICommentService commentService;

    // Constructor injection for the service layer
    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }
    
    //Adds a new comment to a post or response.

    @PostMapping("/add")
    @Operation(summary = "Add a comment", description = "Adds a new comment to a post or response.")
    public ResponseEntity<CommentResponseDTO> addComment(@Valid @RequestBody CommentRequestDTO dto) throws DeveloperCommunityException {
    	logger.info("Adding comment: {}", dto);
    	CommentResponseDTO response = commentService.addComment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Updates an existing comment.
    
    @PutMapping("/update")
    @Operation(summary = "Update a comment", description = "Updates the content of an existing comment.")
    public ResponseEntity<CommentResponseDTO> updateComment(@Valid @RequestBody CommentRequestDTO comment) throws DeveloperCommunityException {
        logger.info("Updating comment with ID: {}", comment.getCommentId());
    	CommentResponseDTO response = commentService.updateComment(comment);
        return ResponseEntity.ok(response);
    }
    
    //Deletes a comment by its ID.
    
    @DeleteMapping("/remove/{commentId}")
    @Operation(summary = "Delete a comment", description = "Removes a comment by its ID.")
    public ResponseEntity<CommentResponseDTO> removeComment(@Valid @PathVariable int commentId) throws DeveloperCommunityException {
    	logger.info("Removing comment with ID: {}", commentId);
    	CommentResponseDTO response = commentService.removeComment(commentId);
        return ResponseEntity.ok(response);
    }

   //Returns the number of votes of a specific type for a comment.
    
    @GetMapping("/votetype/count")
    @Operation(summary = "Count votes on a comment", description = "Returns the number of votes of a specific type for a comment.")
    public ResponseEntity<String> getNoOfVotesOnCommentByVoteType(@Valid @RequestParam String voteType,
                                                                  @RequestParam int commentId)
    {
    	logger.info("Getting vote count for type '{}' on comment ID {}", voteType, commentId);
        String count = commentService.getNoOfVotesOnCommentByVoteType(voteType, commentId);
        return ResponseEntity.ok(count);
    }
    
    //Fetches a comment using its unique ID.

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Fetches a comment using its unique ID.")
    public ResponseEntity<CommentResponseDTO> getByCommentId(@Valid @PathVariable int commentId) throws DeveloperCommunityException {
    	logger.info("Fetching comment with ID: {}", commentId);
    	CommentResponseDTO response = commentService.getByCommentId(commentId);
        return ResponseEntity.ok(response);
    }

   // Retrieves all comments associated with a specific post.
    
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get comments for a post", description = "Retrieves all comments associated with a specific post.")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@Valid @PathVariable int postId) throws DeveloperCommunityException {
    	logger.info("Fetching comments for post ID: {}", postId);
        List<CommentResponseDTO> responses = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(responses);
    }
    
   // Retrieves all comments associated with a specific response.

    @GetMapping("/response/{responseId}")
    @Operation(summary = "Get comments for a response", description = "Retrieves all comments associated with a specific response.")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByResponseId(@Valid @PathVariable int responseId) throws DeveloperCommunityException {
    	logger.info("Fetching comments for response ID: {}", responseId);
    	List<CommentResponseDTO> responses = commentService.getCommentsByResponseId(responseId);
        return ResponseEntity.ok(responses);
    }
}