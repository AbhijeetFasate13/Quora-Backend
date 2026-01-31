package com.devcommunity.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.devcommunity.dto.VoteRequestDTO;
import com.devcommunity.dto.VoteResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;


public interface IVoteService {

	  
	    public String deleteVote(int voteId) throws DeveloperCommunityException;

		public VoteResponseDTO addVote(VoteRequestDTO voteDTO) throws DeveloperCommunityException;

		public Optional<VoteResponseDTO> getVoteByVoteId(int voteId);

		public List<VoteResponseDTO> getVotesByRespId(int responseId);

		public List<VoteResponseDTO> getVotesByPostId(int postId);

		public String countByVoteTypeAndComment_CommentId(String voteType, int commentId); //NOSONAR

		public List<VoteResponseDTO> getVotesByCommentId(int commentId);

	    
}
