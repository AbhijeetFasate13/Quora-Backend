package com.devcommunity.service.interfaces;

import java.util.List;

import com.devcommunity.dto.CommentRequestDTO;
import com.devcommunity.dto.CommentResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;


public interface ICommentService {

	public CommentResponseDTO addComment(CommentRequestDTO  dto) throws DeveloperCommunityException;

	public List<CommentResponseDTO > getCommentsByPostId(Integer postId) throws DeveloperCommunityException;

	public List<CommentResponseDTO > getCommentsByResponseId(Integer resId) throws DeveloperCommunityException;

	public CommentResponseDTO removeComment(int commentId) throws DeveloperCommunityException;

	public CommentResponseDTO getByCommentId(int commentId) throws DeveloperCommunityException;

	public String getNoOfVotesOnCommentByVoteType(String voteType, int commentId);

	public CommentResponseDTO updateComment(CommentRequestDTO dto) throws DeveloperCommunityException;

	
}
