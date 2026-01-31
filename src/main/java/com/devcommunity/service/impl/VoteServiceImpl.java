package com.devcommunity.service.impl;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.VoteRequestDTO;
import com.devcommunity.dto.VoteResponseDTO;
import com.devcommunity.entity.Comment;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.entity.Vote;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.ICommentRepo;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.repository.IVoteRepo;
import com.devcommunity.service.interfaces.IVoteService;
import com.devcommunity.util.VoteType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoteServiceImpl implements IVoteService {

	private final IVoteRepo voteRepo;
	private final IDevRepo devRepo;
	private final IPostRepo postRepo;
	private final IResponseRepo responseRepo;
	private final ModelMapper modelMapper;
	private final ICommentRepo commentRepo;

	public VoteServiceImpl(ICommentRepo commentRepo, IPostRepo postRepo, IResponseRepo responseRepo, IVoteRepo voteRepo,
			IDevRepo devRepo, ModelMapper modelMapper) {
		this.postRepo = postRepo;
		this.responseRepo = responseRepo;
		this.voteRepo = voteRepo;
		this.modelMapper = modelMapper;
		this.devRepo = devRepo;
		this.commentRepo = commentRepo;
	}

	private static final Logger logger = LoggerFactory.getLogger(VoteServiceImpl.class);

	@Override
	public List<VoteResponseDTO> getVotesByPostId(int postId) {
		logger.info("Fetching votes for postId: {}", postId);
		return voteRepo.findByPost_PostId(postId).stream().map(vote -> modelMapper.map(vote, VoteResponseDTO.class))
				.toList();
	}

	@Override
	public List<VoteResponseDTO> getVotesByRespId(int responseId) {
		logger.info("Fetching votes for responseId: {}", responseId);
		return voteRepo.findByResponse_RespId(responseId).stream()
				.map(vote -> modelMapper.map(vote, VoteResponseDTO.class)).toList();
	}

	@Override
	public List<VoteResponseDTO> getVotesByCommentId(int commentId) {
		logger.info("Fetching votes for commentId: {}", commentId);
		List<Vote> votes = voteRepo.findByComment_CommentId(commentId);
		return votes.stream().map(vote -> modelMapper.map(vote, VoteResponseDTO.class)).toList();
	}

	private Developer authenticateDeveloper(int developerId) throws DeveloperCommunityException {
		return devRepo.findById(developerId).orElseThrow(() -> new DeveloperCommunityException("Developer not found"));
	}

	@Override
	public VoteResponseDTO addVote(VoteRequestDTO voteDto) throws DeveloperCommunityException {
		logger.info("Adding new vote: {}", voteDto);
		Developer developer = authenticateDeveloper(voteDto.getDeveloperId());

		Vote vote = new Vote();
		vote.setVoteType(voteDto.getVoteType());
		vote.setDeveloper(developer);

		// Set comment if commentId is present
		if (voteDto.getCommentId() != null && voteDto.getCommentId() != 0) {

			Comment comment = commentRepo.findById(voteDto.getCommentId()).orElseThrow(
					() -> new DeveloperCommunityException("Comment not found with ID: " + voteDto.getCommentId()));

			List<Vote> existingVote = voteRepo.findByDeveloperIdAndComment_CommentId(developer.getId(),
					voteDto.getCommentId());
			if (!existingVote.isEmpty()) {
				throw new DeveloperCommunityException("You have already voted on this comment.");
			}
			vote.setComment(comment);
		}

		// Set post if postId is present
		else if (voteDto.getPostId() != null && voteDto.getPostId() != 0) {

			Post post = postRepo.findById(voteDto.getPostId()).orElseThrow(
					() -> new DeveloperCommunityException("Post not found with ID: " + voteDto.getPostId()));

			List<Vote> existingVote = voteRepo.findByDeveloperIdAndPost_PostId(developer.getId(), voteDto.getPostId());
			if (!existingVote.isEmpty()) {
				throw new DeveloperCommunityException("You have already voted on this post.");
			}

			vote.setPost(post);
		}

		// Set response if responseId is present
		else if (voteDto.getRespId() != null && voteDto.getRespId() != 0) {

			Response response = responseRepo.findById(voteDto.getRespId()).orElseThrow(
					() -> new DeveloperCommunityException("Response not found with ID: " + voteDto.getRespId()));
			List<Vote> existingVote = voteRepo.findByDeveloperIdAndResponse_RespId(developer.getId(),
					voteDto.getRespId());
			if (!existingVote.isEmpty()) {
				throw new DeveloperCommunityException("You have already voted on this response.");
			}

			vote.setResponse(response);
		}

		else {
			throw new DeveloperCommunityException("Either postId or responseId or commentId must be provided to vote");
		}

		Vote savedVote = voteRepo.save(vote);
		return modelMapper.map(savedVote, VoteResponseDTO.class);
	}

	@Override
	public String deleteVote(int voteId) throws DeveloperCommunityException {
		Optional<Vote> voteOptional = voteRepo.findById(voteId);

		if (voteOptional.isPresent()) {
			Vote vote = voteOptional.get();

			int id = vote.getDeveloper().getId();

			// Check if the vote was created by the same developer
			if (!vote.getDeveloper().getId().equals(id)) {
				logger.error("Unauthorized delete attempt by Developer ID: {}", id);
				throw new DeveloperCommunityException("You are not authorized to delete this vote.");
			}

			voteRepo.deleteById(voteId);
			logger.info("Vote deleted successfully with ID: {}", voteId);
			return "Vote deleted successfully";
		} else {
			logger.error("Vote not found with ID: {}", voteId);
			throw new DeveloperCommunityException("Vote not found with ID: " + voteId);
		}
	}

	@Override
	public Optional<VoteResponseDTO> getVoteByVoteId(int voteId) {
		logger.info("Fetching vote by voteId: {}", voteId);

		Optional<Vote> voteOptional = voteRepo.findByVoteId(voteId);

		return voteOptional.map(vote -> modelMapper.map(vote, VoteResponseDTO.class));
	}

	@Override
	public String countByVoteTypeAndComment_CommentId(String voteType, int commentId) {
		logger.info("Counting votes of type '{}' for comment ID: {}", voteType, commentId);

		VoteType type;
		try {
			type = VoteType.valueOf(voteType.toUpperCase());
		} catch (IllegalArgumentException e) {
			logger.error("Invalid vote type received: {}", voteType);
			throw new IllegalArgumentException("Invalid vote type: " + voteType);
		}
		int count = voteRepo.countByVoteTypeAndComment_CommentId(type, commentId);
		logger.debug("Vote count for type '{}' on comment ID {}: {}", voteType, commentId, count);
		return "Votecount for " + voteType + " of commentId " + commentId + " : " + count;
	}
}