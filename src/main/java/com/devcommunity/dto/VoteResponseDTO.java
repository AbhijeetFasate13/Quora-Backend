package com.devcommunity.dto;

import com.devcommunity.util.VoteType;

import lombok.Data;

@Data
public class VoteResponseDTO {
    
	private Integer voteId;
    
	private VoteType voteType;
    
	private int developerId;
    
	private String developerName;
    
	private Integer postId;
	
	private Integer respId;
	
	private Integer commentId;
	
}

	
	
