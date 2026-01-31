package com.devcommunity.service.interfaces;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IUserService {
	public User getUser(int id) throws DeveloperCommunityException;

	public String updateUser(int id, UserRequestDTO userDTO) throws DeveloperCommunityException;

	public String deleteUser(int id) throws DeveloperCommunityException;
	
	public User getUserByUsername(String name) throws DeveloperCommunityException;
}
