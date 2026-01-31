package com.devcommunity.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.TokenResponse;
import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.entity.RefreshToken;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.RefreshTokenService;
import com.devcommunity.service.interfaces.IAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Handles operations related to sign up and sign in.")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
    private IAuthService service;
    private RefreshTokenService refreshTokenService;
    private JWTService jwtService;
    
    public AuthController(IAuthService service, RefreshTokenService refreshTokenService, JWTService jwtService) {
    	this.service = service;
    	this.refreshTokenService = refreshTokenService;
    	this.jwtService = jwtService;
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
     * @return Returns access token and refresh token
     * @throws DeveloperCommunityException
     */
    @PostMapping("/login")
    @Operation(summary = "Logs in the user and returns JWT tokens.", 
               description = "Verifies user credentials and returns access token (15 min) and refresh token (7 days).")
    public ResponseEntity<TokenResponse> login(@RequestBody UserRequestDTO userDTO) throws DeveloperCommunityException{
    	logger.info("Login API called");
        return ResponseEntity.ok(service.verify(userDTO));
    }
    
    /**
     * Refresh access token using refresh token
     * 
     * @param request Map containing refreshToken
     * @return New access token with same refresh token
     * @throws DeveloperCommunityException
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", 
               description = "Generates a new access token using a valid refresh token. The refresh token remains the same.")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> request) 
        throws DeveloperCommunityException {
    	logger.info("Refresh token API called");
    	
        String refreshTokenStr = request.get("refreshToken");
        
        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            throw new DeveloperCommunityException("Refresh token is required");
        }
        
        // Find and verify refresh token
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
        refreshTokenService.verifyExpiration(refreshToken);
        
        // Generate new access token
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user.getUsername());
        
        return ResponseEntity.ok(TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshTokenStr) // Return same refresh token
            .tokenType("Bearer")
            .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
            .build());
    }
    
    /**
     * Logout and revoke refresh token
     * 
     * @param request Map containing refreshToken
     * @return Success message
     * @throws DeveloperCommunityException
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token", 
               description = "Revokes the refresh token, preventing it from being used for new access tokens.")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) 
        throws DeveloperCommunityException {
    	logger.info("Logout API called");
    	
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeToken(refreshToken);
        }
        
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    
    
}