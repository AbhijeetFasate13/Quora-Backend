package com.devcommunity.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Handles operations related to sign up and sign in.")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
    private IAuthService service;
    public AuthController(IAuthService service) {
    	this.service = service;
    }

    /**
     * 
     * @param userDTO
     * @return Response user object with username and encrypted password.
     * @throws DeveloperCommunityException
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user.", description = "Adds a new user to the database, saving its credentials.")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO userDTO) throws DeveloperCommunityException {
    	logger.info("Register API called");
        return ResponseEntity.ok(service.register(userDTO));

    }

    /**
     * 
     * @param userDTO
     * @return If successful then, returns jwt or else "fail".
     * @throws DeveloperCommunityException
     */
    @PostMapping("/login")
    @Operation(summary = "Logs in the user and return the jwt.", description = "Verifies if the user credentials are valid and exist in db if yes, then returns a jwt.")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserRequestDTO userDTO) throws DeveloperCommunityException{
    	logger.info("Login API called");
        return ResponseEntity.ok(service.verify(userDTO));
    }
    
    
}