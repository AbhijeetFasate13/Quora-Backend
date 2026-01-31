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
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.PostRequestDTO;
import com.devcommunity.dto.PostResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IPostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Controller", description = "Handles operations related to Post")
public class PostController {
	 	
	private static final Logger logger = LoggerFactory.getLogger(PostController.class);
	
	private final IPostService postService;
	
	public PostController(IPostService postService) {
		this.postService = postService;
	}
	
	/**
	 * Fetches all posts
	 * @return List of posts
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/all")
	@Operation(summary = "Get all posts")
	public ResponseEntity<List<PostResponseDTO>> getAllPosts() throws DeveloperCommunityException {
		logger.info("Get all posts API called");
		return ResponseEntity.ok(postService.getAllPost());
	}
	
	/**
	 * Fetches post by its post id
	 * @param postId of the post
	 * @return post
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/{postId}")
	@Operation(summary = "Get post by id", description = "Enter post id")
	public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Integer postId) throws DeveloperCommunityException {
		logger.info("Get post by id API called");
		return ResponseEntity.ok(postService.getPostById(postId));
	}
	
	/**
	 * Fetches all posts created by a developer
	 * @param devId of the developer
	 * @return List of posts
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/developer/{devId}")
	@Operation(summary = "Get post by dev id", description = "Enter developer id")
	public ResponseEntity<List<PostResponseDTO>> getPostByDev(@PathVariable Integer devId) throws DeveloperCommunityException {
		logger.info("Get post by dev id API called");
		return ResponseEntity.ok(postService.getAllPostByDev(devId));
	}
	
	/**
	 * Fetches all posts containing a keyword either in query or topic fields
	 * @param keyword
	 * @return List of posts
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/topic/{keyword}")
	@Operation(summary = "Get post by keyword", description = "Enter keyword")
	public ResponseEntity<List<PostResponseDTO>> searchPostByKeyword(@PathVariable String keyword) throws DeveloperCommunityException {
		logger.info("Get post by keyword API called");
		return ResponseEntity.ok(postService.getPostByKeyword(keyword));
	}
	
	/**
	 * Adds a post
	 * @param postDTO containing post details
	 * @return String confirming operation
	 */
	@PostMapping("/")
	@Operation(summary = "Add post", description = "Enter query, topic, dev id")
	public ResponseEntity<String> addPost(@Valid @RequestBody PostRequestDTO postDTO) {
		logger.info("Add post API called");
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(postDTO));
		
	}
	
	/**
	 * Updates a post by its post id
	 * Does not validate the DTO as some fields are allowed to be blank, handled in service layer
	 * @param postId of the post
	 * @param postDTO containing post details
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@PutMapping("/{postId}")
	@Operation(summary = "Update post", description = "Enter postId and query, topic, dev id")
	public ResponseEntity<String> updatePost(@PathVariable Integer postId, @RequestBody PostRequestDTO postDTO ) throws DeveloperCommunityException {
		logger.info("Update post API called");
		return ResponseEntity.ok(postService.updatePost(postId, postDTO));
	}
	
	/**
	 * Deletes a post by its post id
	 * @param postId of the post
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@DeleteMapping("/{postId}")
	@Operation(summary = "Delete post", description = "Enter post id")
	public ResponseEntity<String> deletePost(@PathVariable Integer postId) throws DeveloperCommunityException {
		logger.info("Delete post API called");
		return ResponseEntity.ok(postService.removePost(postId));
	}
	
	/**
	 * Deletes multiple posts by their ids
	 * @param List of the post ids
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "Delete multiple posts", description = "Enter list of post ids")
	public ResponseEntity<String> deleteMultiplePost(@RequestBody List<Integer> ids) throws DeveloperCommunityException {
		logger.info("Delete multiple posts API called");
		return ResponseEntity.ok(postService.removeMultiplePost(ids));
	}
	
}
