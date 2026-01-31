package com.devcommunity.service.interfaces;

import com.devcommunity.dto.TokenResponse;
import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IAuthService {
	UserResponseDTO register(UserRequestDTO userDTO) throws DeveloperCommunityException;

	TokenResponse verify(UserRequestDTO userDTO) throws DeveloperCommunityException;
}
