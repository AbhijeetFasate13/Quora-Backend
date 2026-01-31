package com.devcommunity.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IUserRepo;
import com.devcommunity.service.interfaces.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	private IUserRepo repo;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	public UserServiceImpl(IUserRepo repo) {
		this.repo = repo;
	}

	private boolean validatePassword(String rawPassword) {
		return rawPassword.matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$");
	}

	@Override
	public User getUser(int id) throws DeveloperCommunityException {
		if (repo.existsById(id)) {
			return repo.findById(id).get(); // NOSONAR
		}
		throw new DeveloperCommunityException("User with " + id + " not found.");
	}

	@Override
	public String updateUser(int id, UserRequestDTO userDTO) throws DeveloperCommunityException {
		User user = repo.findById(id)
				.orElseThrow(() -> new DeveloperCommunityException("User with ID " + id + " not found."));

		String newUsername = userDTO.getUsername();
		String newPassword = userDTO.getPassword();

		if (newUsername != null && !newUsername.trim().isEmpty()) {
			user.setUsername(newUsername);
		}

		if (newPassword == null) {
			repo.save(user);
			return "User updated successfully";
		} else if (validatePassword(newPassword)) {
			user.setPassword(encoder.encode(newPassword));
		} else {
			throw new DeveloperCommunityException(
					"Password doesn't fit the criteria of 8 <= length <= 20 and/or having at least one lowercase letter, one uppercase letter, one digit, one special character.");
		}

		repo.save(user);
		return "User updated successfully";
	}

	@Override
	public String deleteUser(int id) throws DeveloperCommunityException {
		User user = repo.findById(id)
				.orElseThrow(() -> new DeveloperCommunityException("User with ID " + id + " not found."));
		repo.delete(user);
		return "User deleted successfully";
	}

	@Override
	public User getUserByUsername(String name) throws DeveloperCommunityException {
		if (repo.existsByUsername(name)) {
			return repo.findByUsername(name).get(); // NOSONAR
		}
		throw new DeveloperCommunityException("User with name: " + name + " not found.");
	}

}