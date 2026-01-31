package com.devcommunity.service.interfaces;

import java.util.List;

import com.devcommunity.dto.ResponseRequestDTO;
import com.devcommunity.dto.ResponseResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IResponseService {

	public List<ResponseResponseDTO> getAllResponseByPost(Integer postId) throws DeveloperCommunityException;
	
	public List<ResponseResponseDTO> getAllResponseByDeveloper(Integer devId) throws DeveloperCommunityException;
	
	public String addResponse(ResponseRequestDTO response);

	public String updateResponse(Integer respId, ResponseRequestDTO response) throws DeveloperCommunityException;
	
	public String removeResponse(Integer respId) throws DeveloperCommunityException;
	
	public String removeMultipleResponse(List<Integer> respIds) throws DeveloperCommunityException;

}
