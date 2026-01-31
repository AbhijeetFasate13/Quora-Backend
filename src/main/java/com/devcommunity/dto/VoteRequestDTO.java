package com.devcommunity.dto;

import com.devcommunity.util.VoteType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class VoteRequestDTO {
	
    @NotNull(message = "Vote type must not be null")
    private VoteType voteType;

    @Positive(message = "Developer ID must be a positive number")
    private int developerId;

    @PositiveOrZero(message = "Comment ID must be a positive number")
    private Integer commentId;

    @PositiveOrZero(message = "Post ID must be a positive number")
    private Integer postId;

    @PositiveOrZero(message = "Response ID must be a positive number")
    private Integer respId;

	
}
