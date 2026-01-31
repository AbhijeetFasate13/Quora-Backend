package com.devcommunity.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.TokenResponse;
import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.entity.RefreshToken;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IAuthRepo;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.RefreshTokenService;
import com.devcommunity.service.interfaces.IAuthService;
import com.devcommunity.service.interfaces.IUserService;

@Service
public class AuthServiceImpl implements IAuthService {
	private JWTService jwtService;
	private AuthenticationManager authManager;
	private IAuthRepo repo;
	private ModelMapper modelMapper;
	private IUserService userService;
	private RefreshTokenService refreshTokenService;

	public AuthServiceImpl(JWTService jwtService, AuthenticationManager authManager, IAuthRepo repo,
			ModelMapper modelMapper, IUserService userService, RefreshTokenService refreshTokenService) {
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.repo = repo;
		this.modelMapper = modelMapper;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
	}

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	@Override
	public UserResponseDTO register(UserRequestDTO userDTO) throws DeveloperCommunityException {
		// Validate password first
		if (!userDTO.getPassword().matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$")) {
			throw new DeveloperCommunityException(
					"Password must be 8-20 characters long, have at least one digit, one special character, one lowercase, one uppercase letter, and no whitespace.");
		}
		if(repo.findByUsername(userDTO.getUsername()).isPresent()) {
			throw new DeveloperCommunityException("Username already in use, try different username");
		}

		// Proceed with registration
		User user = modelMapper.map(userDTO, User.class);
		user.setPassword(encoder.encode(userDTO.getPassword()));
		return modelMapper.map(repo.save(user), UserResponseDTO.class);
	}

	@Override
	public TokenResponse verify(UserRequestDTO userDTO) throws DeveloperCommunityException {
		try {
			User user = modelMapper.map(userDTO, User.class);
			String userName = user.getUsername();
			Authentication authentication = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

			if (authentication != null && authentication.isAuthenticated()) {
				// Generate access token
				String accessToken = jwtService.generateToken(user.getUsername());
				
				// Get user and create refresh token
				User authenticatedUser = userService.getUserByUsername(userName);
				RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);
				
				// Return both tokens
				return TokenResponse.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken.getToken())
						.tokenType("Bearer")
						.expiresIn(jwtService.getAccessTokenExpiration() / 1000) // Convert to seconds
						.build();
			} else {
				throw new DeveloperCommunityException("Authentication failed for user: " + user.getUsername());
			}

		} catch (Exception e) {
			throw new DeveloperCommunityException("Error during authentication: " + e.getMessage());
		}
	}
}