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
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.ResponseRequestDTO;
import com.devcommunity.dto.ResponseResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/responses")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Response Controller", description = "Handles operations related to Response")

public class ResponseController {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponseController.class);
	
	private final IResponseService respService;
	
	public ResponseController(IResponseService respService) {
		this.respService = respService;
	}
	
	/**
	 * Fetches all responses to a post by its post id
	 * @param postId of the post
	 * @return List of responses
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/{postId}")
	@Operation(summary = "Get all responses for a post")
	public ResponseEntity<List<ResponseResponseDTO>> getAllResponsesByPost(@PathVariable Integer postId) throws DeveloperCommunityException {
		logger.info("Get all responses by post API called");
		return ResponseEntity.ok(respService.getAllResponseByPost(postId));
	}
	
	/**
	 * Fetches all responses by a developer
	 * @param devId of the developer
	 * @return List of responses
	 * @throws DeveloperCommunityException
	 */
	@GetMapping("/developer/{devId}")
	@Operation(summary = "Get all responses by a dev")
	public ResponseEntity<List<ResponseResponseDTO>> getAllResponsesByDev(@PathVariable Integer devId) throws DeveloperCommunityException {
		logger.info("Get all responses by dev API called");
		return ResponseEntity.ok(respService.getAllResponseByDeveloper(devId));
	}
	
	/**
	 * Add a response to a post
	 * @param respDTO containing response details
	 * @return String confirming operation
	 */
	@PostMapping("/")
	@Operation(summary = "Add response", description = "Enter response, post id, dev id")
	public ResponseEntity<String> addResponse(@Valid @RequestBody ResponseRequestDTO respDTO) {
		logger.info("Add response API called");
		return ResponseEntity.status(HttpStatus.CREATED).body(respService.addResponse(respDTO));
	}
	
	/**
	 * Update a response by its id
	 * @param respId of the response
	 * @param respDTO containing response details
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@PutMapping("/{respId}")
	@Operation(summary = "Update response", description = "Enter response id, response, post id, dev id")
	public ResponseEntity<String> updateResponse(@PathVariable Integer respId, @Valid @RequestBody ResponseRequestDTO respDTO) throws DeveloperCommunityException {
		logger.info("Update response API called");
		return ResponseEntity.ok(respService.updateResponse(respId, respDTO));
	}
	
	/**
	 * Delete a response by its id
	 * @param respId of the response
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@DeleteMapping("/{respId}")
	@Operation(summary = "Delete response", description = "Enter response id")
	public ResponseEntity<String> deleteResponse(@PathVariable Integer respId) throws DeveloperCommunityException {
		logger.info("Delete response API called");
		return ResponseEntity.ok(respService.removeResponse(respId));
	}
	
	/**
	 * Delete multiple responses by their ids
	 * @param List of the response ids
	 * @return String confirming operation
	 * @throws DeveloperCommunityException
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "Delete multiple responses", description = "Enter list of response ids")
	public ResponseEntity<String> deleteMultipleResponse(@RequestBody List<Integer> ids) throws DeveloperCommunityException {
		logger.info("Delete multiple responses API called");
		return ResponseEntity.ok(respService.removeMultipleResponse(ids));
	}
}
