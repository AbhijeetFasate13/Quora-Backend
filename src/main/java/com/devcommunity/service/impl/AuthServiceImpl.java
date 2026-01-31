package com.devcommunity.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IAuthRepo;
import com.devcommunity.service.JWTService;
import com.devcommunity.service.interfaces.IAuthService;
import com.devcommunity.service.interfaces.IUserService;

@Service
public class AuthServiceImpl implements IAuthService {
	private JWTService jwtService;
	private AuthenticationManager authManager;
	private IAuthRepo repo;
	private ModelMapper modelMapper;
	private IUserService userService;

	public AuthServiceImpl(JWTService jwtService, AuthenticationManager authManager, IAuthRepo repo,
			ModelMapper modelMapper, IUserService userService) {
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.repo = repo;
		this.modelMapper = modelMapper;
		this.userService = userService;
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
	public UserResponseDTO verify(UserRequestDTO userDTO) throws DeveloperCommunityException {
		try {
			User user = modelMapper.map(userDTO, User.class);
			String userName = user.getUsername();
			Authentication authentication = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

			if (authentication != null && authentication.isAuthenticated()) {
				String token = jwtService.generateToken(user.getUsername());
				UserResponseDTO returnObj = modelMapper.map(userService.getUserByUsername(userName),
						UserResponseDTO.class);
				returnObj.setToken(token);
				return returnObj;
			} else {
				throw new DeveloperCommunityException("Authentication failed for user: " + user.getUsername());
			}

		} catch (Exception e) {
			throw new DeveloperCommunityException("Error during authentication: " + e.getMessage());
		}
	}
}