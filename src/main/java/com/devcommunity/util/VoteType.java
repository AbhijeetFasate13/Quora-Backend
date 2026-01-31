package com.devcommunity.util;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum VoteType {
	UPVOTE,DOWNVOTE
}
