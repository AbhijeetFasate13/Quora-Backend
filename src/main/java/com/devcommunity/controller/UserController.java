package com.devcommunity.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Handles operations like fetching, updating, deleting user.")
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private IUserService service;
    public UserController(IUserService service) {
    	this.service = service;
    }
    
    /**
     * 
     * @param id
     * @return If found, returns the user response object.
     */
    @GetMapping("/id/{id}")
    @Operation(summary = "Get user by id", description = "Enter user id.")
    public ResponseEntity<User> getUserBy(@PathVariable int id) throws DeveloperCommunityException {
    	logger.info("Get user by id API called.");
        return ResponseEntity.ok(service.getUser(id));
    }
 
    /**
     * 
     * @param id
     * @param userDTO
     * @return If found, then updates user credentials with given info and return a string acknowledging it.
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Update user credentials", description = "Enter userId and username and/or password.")
    public ResponseEntity<String> updateUserBy(@PathVariable int id, @RequestBody UserRequestDTO userDTO) throws DeveloperCommunityException {
    	logger.info("Update user by id API called.");
        return ResponseEntity.ok(service.updateUser(id, userDTO));
    }
    
    /**
     * 
     * @param id
     * @return If found, then deletes the user and acknowledges it with a string response.
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user", description = "Enter user id.")
    public ResponseEntity<String> deleteUserBy(@PathVariable int id) throws DeveloperCommunityException {
    	logger.info("Delete user by id API called.");
    	return ResponseEntity.ok(service.deleteUser(id));
    }
    
}