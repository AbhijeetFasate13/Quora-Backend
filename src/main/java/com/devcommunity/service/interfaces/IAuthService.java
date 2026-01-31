package com.devcommunity.service.interfaces;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.dto.UserResponseDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IAuthService {
	UserResponseDTO register(UserRequestDTO userDTO) throws DeveloperCommunityException;

	UserResponseDTO verify(UserRequestDTO userDTO) throws DeveloperCommunityException;
}
