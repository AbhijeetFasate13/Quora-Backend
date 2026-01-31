package com.devcommunity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequestDTO {
	
	/**
	 * The query posted by the developer
	 * Cannot be blank during creation
	 */
	@NotBlank(message = "Query cannot be blank")
	private String query;
	
	/**
	 * The topic posted by the developer
	 * Cannot be blank during creation
	 */
	@NotBlank(message = "Topic cannot be blank")
	private String topic;
	
	/**
	 * ID of the developer who created the post
	 */
	@Min(value = 0, message = "Developer ID cannot be negative")
	private Integer developerId;
	
}
