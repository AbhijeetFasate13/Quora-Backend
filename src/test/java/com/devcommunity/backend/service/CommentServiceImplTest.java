package com.devcommunity.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import com.devcommunity.dto.CommentRequestDTO;
import com.devcommunity.dto.CommentResponseDTO;
import com.devcommunity.entity.Comment;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.ICommentRepo;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.repository.IVoteRepo;
import com.devcommunity.service.impl.CommentServiceImpl;
import com.devcommunity.util.VoteType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {

	@InjectMocks
	private CommentServiceImpl commentServiceImpl;

	@Mock
	private ICommentRepo commentRepo;
	@Mock
	private IDevRepo devRepo;
	@Mock
	private IPostRepo postRepo;
	@Mock
	private IResponseRepo responseRepo;
	@Mock
	private IVoteRepo voteRepo;
	@Mock
	private ModelMapper modelMapper;

	private CommentRequestDTO commentRequestDTO;
	private CommentResponseDTO commentResponseDTO;
	private Developer developer;
	private Comment comment;

	@BeforeEach
	void setup() {
		developer = new Developer();
		developer.setId(1);
		developer.setDevName("Hema");

		commentRequestDTO = new CommentRequestDTO();
		commentRequestDTO.setDeveloperId(1);
		commentRequestDTO.setText(
				"Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.");
		commentRequestDTO.setPostId(100);
		commentRequestDTO.setCommentId(1);

		comment = new Comment();
		comment.setCommentId(1);
		comment.setText(
				"Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.");
		comment.setDeveloper(developer);
		comment.setCreatedDate(LocalDate.now());

		commentResponseDTO = new CommentResponseDTO();
		commentResponseDTO.setCommentId(1);
		commentResponseDTO.setText(
				"Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.");
		commentResponseDTO.setDeveloperId(1);
		commentResponseDTO.setDeveloperName("Hema");
		commentResponseDTO.setPostId(100);
	}

	@Test
	void testAddComment_Success() throws DeveloperCommunityException {
		Post post = new Post();
		post.setPostId(100);

		comment.setPost(post);

		when(devRepo.findById(1)).thenReturn(Optional.of(developer));
		when(postRepo.findById(100)).thenReturn(Optional.of(post));
		when(commentRepo.save(any(Comment.class))).thenReturn(comment);
		when(modelMapper.map(any(Comment.class), eq(CommentResponseDTO.class))).thenReturn(commentResponseDTO);

		CommentResponseDTO result = commentServiceImpl.addComment(commentRequestDTO);

		assertNotNull(result);
		assertEquals("Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.",
				result.getText());
		assertEquals(1, result.getDeveloperId());
	}

	@Test
	void testAddCommentDeveloperNotFound() {
		when(devRepo.findById(1)).thenReturn(Optional.empty());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.addComment(commentRequestDTO);
		});

		assertEquals("Developer not found", exception.getMessage());
	}

	@Test
	void testUpdateComment_Success() throws DeveloperCommunityException {
		// Arrange
		
		Comment updatedComment = new Comment();
		updatedComment.setCommentId(1);
		updatedComment.setText("Updated Comment");
		updatedComment.setDeveloper(comment.getDeveloper());

		CommentResponseDTO expectedResponse = new CommentResponseDTO();
		expectedResponse.setCommentId(1);
		expectedResponse.setText("Updated Comment");

		when(commentRepo.findByCommentId(1)).thenReturn(Optional.of(comment));
		when(devRepo.findById(1)).thenReturn(Optional.of(comment.getDeveloper()));
		when(commentRepo.save(any(Comment.class))).thenReturn(updatedComment);
		when(modelMapper.map(any(Comment.class), eq(CommentResponseDTO.class))).thenReturn(expectedResponse);

		// Act
		CommentResponseDTO result = commentServiceImpl.updateComment(commentRequestDTO);

		// Assert
		assertNotNull(result);
		assertEquals("Updated Comment", result.getText());
		assertEquals(1, result.getCommentId());
	}

	@Test
	void testUpdateCommentUnauthorizedDeveloper() {
	    // Arrange
	    Developer actualOwner = new Developer();
	    actualOwner.setId(2); // actual owner of the comment

	    comment.setCommentId(1);
	    comment.setDeveloper(actualOwner); // Assign actual owner to the comment

	    commentRequestDTO.setCommentId(1); // ID of the comment to update
	    commentRequestDTO.setDeveloperId(1); // ID of the developer trying to update

	    Developer d = new Developer();
	    d.setId(1); // Authenticated developer (not the owner)
	    d.setDevName("HEMA");

	    when(commentRepo.findByCommentId(1)).thenReturn(Optional.of(comment));
	    when(devRepo.findById(1)).thenReturn(Optional.of(d));

	    // Act & Assert
	    DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
	        commentServiceImpl.updateComment(commentRequestDTO);
	    });

	    assertEquals("You are not authorized to modify this comment", exception.getMessage());
	}

	@Test
	void testGetNoOfVotesOnCommentByVoteType_Success() {
		when(voteRepo.countByVoteTypeAndComment_CommentId(VoteType.UPVOTE, 1)).thenReturn(5);

		String resultTemp = commentServiceImpl.getNoOfVotesOnCommentByVoteType("UPVOTE", 1);
		int idx = resultTemp.lastIndexOf(":");
		int count = Integer.parseInt(resultTemp.substring(idx+1).trim());
		assertEquals(5, count);
	}

	@Test
	void testGetNoOfVotesOnCommentByVoteTypeInvalidType() {
		assertThrows(IllegalArgumentException.class, () -> {
			commentServiceImpl.getNoOfVotesOnCommentByVoteType("INVALID", 1);
		});
	}

	@Test
	void testGetByCommentId_Success() throws DeveloperCommunityException {
		when(commentRepo.findByCommentId(10)).thenReturn(Optional.of(comment));
		when(modelMapper.map(comment, CommentResponseDTO.class)).thenReturn(commentResponseDTO);
		CommentResponseDTO result = commentServiceImpl.getByCommentId(10);
		assertNotNull(result);
		assertEquals(1, result.getCommentId());
		assertEquals("Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.",
				result.getText());
	}

	@Test
	void testGetByCommentId_Failure() {
		when(commentRepo.findByCommentId(10)).thenReturn(Optional.empty());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.getByCommentId(10);
		});

		assertEquals("Comment not found", exception.getMessage());
	}

	@Test
	void testGetCommentsByPostId_Success() throws DeveloperCommunityException {
		List<Comment> comments = List.of(comment);
		when(commentRepo.findByPost_PostId(100)).thenReturn(comments);
		when(modelMapper.map(comment, CommentResponseDTO.class)).thenReturn(commentResponseDTO);

		List<CommentResponseDTO> result = commentServiceImpl.getCommentsByPostId(100);

		assertEquals(1, result.size());
		assertEquals("Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.",
				result.get(0).getText());
	}

	@Test
	void testGetCommentsByPostIdEmptyList() {

		Integer postId = 1;
		when(commentRepo.findByPost_PostId(postId)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.getCommentsByPostId(postId);
		});

	}

	@Test
	void testGetCommentsByPostId_Failure() {
		when(commentRepo.findByPost_PostId(100)).thenReturn(Collections.emptyList());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.getCommentsByPostId(100);
		});

		assertEquals("No comments found for the given post", exception.getMessage());
	}

	@Test
	void testGetCommentsByResponseId_Success() throws DeveloperCommunityException {
		List<Comment> comments = List.of(comment);
		when(commentRepo.findByResponse_RespId(200)).thenReturn(comments);
		when(modelMapper.map(comment, CommentResponseDTO.class)).thenReturn(commentResponseDTO);

		List<CommentResponseDTO> result = commentServiceImpl.getCommentsByResponseId(200);

		assertEquals(1, result.size());
		assertEquals(1, result.get(0).getCommentId());
	}

	@Test
	void testGetCommentsByResponseIdEmptyList() {

		when(commentRepo.findByResponse_RespId(1)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.getCommentsByResponseId(1);
		});

	}

	@Test
	void testGetCommentsByResponseId_Failure() {
		when(commentRepo.findByResponse_RespId(200)).thenReturn(Collections.emptyList());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.getCommentsByResponseId(200);
		});

		assertEquals("No comments found for the given response", exception.getMessage());
	}

	@Test
	void testRemoveComment_Success() throws DeveloperCommunityException {
		// Arrange
		when(commentRepo.findByCommentId(10)).thenReturn(Optional.of(comment));
		when(devRepo.findById(1)).thenReturn(Optional.of(developer));
		when(modelMapper.map(comment, CommentResponseDTO.class)).thenReturn(commentResponseDTO);

		// Act
		CommentResponseDTO result = commentServiceImpl.removeComment(10);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getCommentId());
		assertEquals("Thanks! This solution worked perfectly for my case. Just had to tweak the input format slightly.",
				result.getText());
		assertEquals(1, result.getDeveloperId());

		// Verify that the comment was deleted
		verify(commentRepo).deleteById(10);
	}

	@Test
	void testRemoveCommentFailureCommentNotFound() {
		when(commentRepo.findByCommentId(10)).thenReturn(Optional.empty());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.removeComment(10);
		});

		assertEquals("Comment not found", exception.getMessage());
	}

	@Test
	void testRemoveCommentFailureDeveloperNotFound() {
		when(commentRepo.findByCommentId(10)).thenReturn(Optional.of(comment));
		when(devRepo.findById(1)).thenReturn(Optional.empty());

		DeveloperCommunityException exception = assertThrows(DeveloperCommunityException.class, () -> {
			commentServiceImpl.removeComment(10);
		});

		assertEquals("Developer not found", exception.getMessage());
	}

}
