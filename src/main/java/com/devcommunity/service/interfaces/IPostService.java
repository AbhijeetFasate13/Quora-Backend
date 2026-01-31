package com.devcommunity.service.interfaces;

import java.util.List;

import com.devcommunity.dto.PostRequestDTO;
import com.devcommunity.dto.PostResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;

public interface IPostService {
	
	public List<PostResponseDTO> getAllPost() throws DeveloperCommunityException; 

	public PostResponseDTO getPostById(Integer postId) throws DeveloperCommunityException;
	
	public List<PostResponseDTO> getAllPostByDev(Integer devId) throws DeveloperCommunityException; 
	
	public List<PostResponseDTO> getPostByKeyword(String keyword) throws DeveloperCommunityException; 
		
	public String addPost(PostRequestDTO postDTO);

	public String updatePost(Integer postId, PostRequestDTO postDTO) throws DeveloperCommunityException;

	public String removePost(Integer postId) throws DeveloperCommunityException;
	
	public String removeMultiplePost(List<Integer> postIds) throws DeveloperCommunityException;  

}