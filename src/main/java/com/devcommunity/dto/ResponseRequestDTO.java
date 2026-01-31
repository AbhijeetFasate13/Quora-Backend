package com.devcommunity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResponseRequestDTO {
	
	/**
	 * The response to a post posted by the Developer
	 */
	@NotBlank(message = "Query cannot be blank")
	private String answer;
	
	/**
	 * ID of the post the response is for
	 */
	@Min(value = 0, message = "Post ID cannot be negative")
	private Integer postId;
	
	/**
	 * ID of the developer
	 */
	@Min(value = 0, message = "Developer ID cannot be negative")
	private Integer developerId;

}
