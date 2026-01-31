package com.devcommunity.service.interfaces;

import java.util.List;

import com.devcommunity.dto.DeveloperRequestDTO;
import com.devcommunity.dto.DeveloperResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IDeveloperService {

	String addDeveloper(int id,DeveloperRequestDTO developer) throws DeveloperCommunityException;

	String updateDeveloper(int id,DeveloperRequestDTO developer) throws DeveloperCommunityException;

	DeveloperResponseDTO getDeveloperById(Integer devId) throws DeveloperCommunityException;

	List<DeveloperResponseDTO> getAllDevelopers();

	DeveloperResponseDTO getByMaxReputation() throws DeveloperCommunityException;
	
	List<DeveloperResponseDTO> searchDevelopersBySkill(String skill);

}
