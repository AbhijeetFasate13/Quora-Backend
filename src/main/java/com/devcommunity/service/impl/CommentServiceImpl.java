package com.devcommunity.service.impl;

import java.time.LocalDate;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.CommentRequestDTO;
import com.devcommunity.dto.CommentResponseDTO;
import com.devcommunity.entity.Comment;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.ICommentRepo;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.repository.IVoteRepo;
import com.devcommunity.service.interfaces.ICommentService;
import com.devcommunity.util.VoteType;

@Service
public class CommentServiceImpl implements ICommentService {


        private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
        private static final String COMMENT_NOT_FOUND = "Comment not found";

	
	    private final ICommentRepo commentRepo;
	    private final IDevRepo devRepo;
	    private final IPostRepo postRepo;
	    private final IResponseRepo responseRepo;
	    private final IVoteRepo voteRepo;
	    private final ModelMapper modelMapper;

	    // Constructor injection for all required repositories and utilities
	    public CommentServiceImpl(ICommentRepo iCommentRepo, IDevRepo developerRepository,
	    		IPostRepo postRepository,IResponseRepo responseRepository,IVoteRepo iVoteRepo,ModelMapper modelMapper)
	    {
	        this.commentRepo = iCommentRepo;
	        this.devRepo = developerRepository;
	        this.postRepo = postRepository;
	        this.responseRepo = responseRepository;
	        this.voteRepo = iVoteRepo;
	        this.modelMapper = modelMapper;
	    }

	    // Authentication: Check if developer exists
	    private Developer authenticateDeveloper(int developerId) throws DeveloperCommunityException {
	    	logger.debug("Authenticating developer with ID: {}", developerId);
	    	return devRepo.findById(developerId)
	                .orElseThrow(() -> new DeveloperCommunityException("Developer not found"));
	    }

	    // Authorization: Check if developer is the owner of the comment
	    private void authorizeCommentOwner(Comment comment, int developerId) throws DeveloperCommunityException {
	        if (!comment.getDeveloper().getId().equals(developerId)) {
	        	logger.warn("Unauthorized access attempt by developer ID: {}", developerId);
	            throw new DeveloperCommunityException("You are not authorized to modify this comment");
	        }
	    }

	    @Override
	    public CommentResponseDTO addComment(CommentRequestDTO dto) throws DeveloperCommunityException {
	    	 logger.info("Adding comment: {}", dto);
	    	Developer developer = authenticateDeveloper(dto.getDeveloperId());

	        Comment comment = new Comment();
	        comment.setText(dto.getText());
	        comment.setCreatedDate(LocalDate.now());
	        comment.setDeveloper(developer);

	        if (dto.getPostId() != 0) {
	            Post post = postRepo.findById(dto.getPostId())
	                    .orElseThrow(() -> new DeveloperCommunityException("Post not found"));
	            comment.setPost(post);
	        } else if (dto.getResponseId() != 0) {
	            Response response = responseRepo.findById(dto.getResponseId())
	                    .orElseThrow(() -> new DeveloperCommunityException("Response not found"));
	            comment.setResponse(response);
	        } else {
	            throw new DeveloperCommunityException("Either postId or responseId must be provided");
	        }

	        Comment saved = commentRepo.save(comment);
	        logger.debug("Comment saved with ID: {}", saved.getCommentId());
	        return mapToResponseDTO(saved);
	    }

	    @Override
	    public CommentResponseDTO updateComment(CommentRequestDTO dto) throws DeveloperCommunityException {
	    	logger.info("Updating comment with ID: {}", dto.getCommentId());
	        Comment comment = commentRepo.findByCommentId(dto.getCommentId())
	                .orElseThrow(() -> new DeveloperCommunityException(COMMENT_NOT_FOUND));

	        authenticateDeveloper(dto.getDeveloperId());
	        authorizeCommentOwner(comment, dto.getDeveloperId());

	        comment.setText(dto.getText());
		    comment.setCommentId(dto.getCommentId());
		    Comment updated = commentRepo.save(comment);
		    logger.debug("Comment updated: {}", updated);
	        return mapToResponseDTO(updated);
	        
	    }

	 
	    @Override
	    public String getNoOfVotesOnCommentByVoteType(String voteType, int commentId) {
            logger.info("Counting votes of type '{}' for comment ID: {}", voteType, commentId);
	        VoteType type;
	        try {
	            type = VoteType.valueOf(voteType.trim().toUpperCase());
	        } catch (IllegalArgumentException e) {
	        	 logger.error("Invalid vote type: {}", voteType);
	            throw new IllegalArgumentException("Invalid vote type: " + voteType + ". Allowed values: UPVOTE, DOWNVOTE");
	        }

	        int count=voteRepo.countByVoteTypeAndComment_CommentId(type, commentId);
	        logger.debug("Vote count for type '{}' on comment ID {}: {}", voteType, commentId, count);
	         return "Votecount for "+voteType+" of commentId "+commentId+" : "+count;
	    }


	    @Override
	    public CommentResponseDTO getByCommentId(int commentId) throws DeveloperCommunityException {
	    	logger.info("Fetching comment by ID: {}", commentId);
	    	Comment comment = commentRepo.findByCommentId(commentId)
	                .orElseThrow(() -> new DeveloperCommunityException(COMMENT_NOT_FOUND));
	        return mapToResponseDTO(comment);
	    }

	    public List<CommentResponseDTO> getCommentsByPostId(Integer postId) throws DeveloperCommunityException {
	        logger.info("Fetching comments for post ID: {}", postId);
	    	List<Comment> comments = commentRepo.findByPost_PostId(postId);
	        if (comments.isEmpty()) {
	            throw new DeveloperCommunityException("No comments found for the given post");
	        }
	        return comments.stream()
	                .map(this::mapToResponseDTO).toList();
	    }

	    @Override
	    public List<CommentResponseDTO> getCommentsByResponseId(Integer responseId) throws DeveloperCommunityException {
	    	logger.info("Fetching comments for response ID: {}", responseId);
	    	List<Comment> comments = commentRepo.findByResponse_RespId(responseId);
	        if (comments.isEmpty()) {
	            throw new DeveloperCommunityException("No comments found for the given response");
	        }
	        return comments.stream()
	                .map(this::mapToResponseDTO)
	                .toList();
	    }

	    private CommentResponseDTO mapToResponseDTO(Comment comment) {
	        CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
	        dto.setDeveloperId(comment.getDeveloper().getId());
	        dto.setDeveloperName(comment.getDeveloper().getDevName());
	        if (comment.getPost() != null) dto.setPostId(comment.getPost().getPostId());
	        if (comment.getResponse() != null) dto.setResponseId(comment.getResponse().getRespId());
	        return dto;
	    }

	    @Override
	    public CommentResponseDTO removeComment(int commentId) throws DeveloperCommunityException 
	    {
	        logger.info("Removing comment with ID: {}", commentId);
	    	Comment comment = commentRepo.findByCommentId(commentId)
	                .orElseThrow(() -> new DeveloperCommunityException(COMMENT_NOT_FOUND));

	        int developerId = comment.getDeveloper().getId();
	        authenticateDeveloper(developerId);
	        authorizeCommentOwner(comment, developerId);

	        CommentResponseDTO dto = mapToResponseDTO(comment);
	        commentRepo.deleteById(commentId);
	        logger.debug("Comment deleted: {}", commentId);
	        return dto;
	    }  
}
