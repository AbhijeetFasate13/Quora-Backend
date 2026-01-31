package com.devcommunity.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ResponseResponseDTO {
	
	/**
	 * ID of the response
	 */
	private Integer respId;
	
	/**
	 * Main body of the response
	 */
	private String answer;
	
	/**
	 * Time the response was created or updated
	 */
	private LocalDateTime respDateTime;
	
	/**
	 * ID of the post the response is for
	 */
	private Integer postId;
	
	/**
	 * ID of the developer who created the response
	 */
	private Integer developerId;
	
	/**
	 * Name of the developer who created the response
	 */
	private String developerName;
	
	/**
	 * Comments added to the Response
	 */
	private List<CommentResponseDTO> comments;
	
	/**
	 * Votes added to the Response
	 */
	private List<VoteResponseDTO> votes;

}
