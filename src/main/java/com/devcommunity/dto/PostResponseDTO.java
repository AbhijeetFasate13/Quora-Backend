package com.devcommunity.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PostResponseDTO {
	
	/**
	 * ID of the post
	 */
	private Integer postId;
	
	/**
	 * Main body of the post
	 */
	private String query;
	
	/**
	 * Topic of the post
	 */
	private String topic;
	
	/**
	 * Time the post was created or updated
	 */
	private LocalDateTime postDateTime;
	
	/**
	 * ID of the developer who created the post
	 */
	private Integer developerId;
	
	/**
	 * Name of the developer who created the post
	 */
	private String developerName;
	
	/**
	 * Responses added to the post
	 */
	private List<ResponseResponseDTO> responses;
	
	/**
	 * Comments added to the post
	 */
	private List<CommentResponseDTO> comments;
	
	/**
	 * Votes added to the post
	 */
	private List<VoteResponseDTO> votes;
	
}
