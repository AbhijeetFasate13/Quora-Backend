package com.devcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CommentRequestDTO {
	    @NotBlank(message = "Comment text must not be blank")
	    private String text;
	    @PositiveOrZero(message = "Developer ID must be a positive number")
	    private int developerId;
	    @PositiveOrZero(message = "Post ID must be a positive number")
	    private int postId;      
	    @PositiveOrZero(message = "Response ID must be a positive number")
	    private int responseId;
	    @PositiveOrZero(message = "Comment ID must be a positive number")
	    private int commentId;
	
}
