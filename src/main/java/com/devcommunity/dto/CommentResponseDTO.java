package com.devcommunity.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CommentResponseDTO {
	
	private int commentId;
	
	private String text;
	
	private LocalDate createdDate;
	
	private int developerId;
	
	private String developerName; 
	
	private int postId;          
	
	private int responseId;
	
	private List<VoteResponseDTO> votes;
}
