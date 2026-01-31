package com.devcommunity.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.devcommunity.dto.PostRequestDTO;
import com.devcommunity.dto.PostResponseDTO;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.service.impl.PostServiceImpl;

/**
 * Unit tests for PostServiceImpl
 * Covers success and failure scenarios for all methods
 */
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
	
	@Mock
	private IPostRepo postRepo;
	
	@Mock
	private IDevRepo devRepo;
	
	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private PostServiceImpl service; 
	
	private Developer developer;
	private Post post;
	private PostRequestDTO postReqDTO;
	private PostResponseDTO postResDTO;
	
	@BeforeEach
	void setUp() {
		developer = new Developer();
		developer.setId(1);
		developer.setDevName("Josh");
		developer.setDevSkill("Java");
		developer.setMemberSince(LocalDate.now());
		developer.setReputation(5);
		
		post = new Post();
		post.setPostId(5);
		post.setDeveloper(developer);
		post.setQuery("I need help with the project! Pls send helppp");
		post.setTopic("Java");
		post.setPostDateTime(LocalDateTime.now());
		
		postReqDTO = new PostRequestDTO();
		postReqDTO.setQuery("I need help with the project! Pls send helppp");
		postReqDTO.setDeveloperId(1);
		postReqDTO.setTopic("Java");
		
		postResDTO = new PostResponseDTO();
		postResDTO.setPostId(5);
		postResDTO.setQuery("I need help with the project! Pls send helppp");
		postResDTO.setTopic("Java");
		postResDTO.setPostDateTime(LocalDateTime.now());
		postResDTO.setDeveloperId(1);
	}
	
	@Test
	void testGetAllPost_Success() throws DeveloperCommunityException {
		when(postRepo.findAll()).thenReturn(List.of(post));
		when(modelMapper.map(post, PostResponseDTO.class)).thenReturn(postResDTO);
		
		List<PostResponseDTO> postList = service.getAllPost();
		
		assertEquals(1, postList.size());
		assertEquals(postResDTO, postList.get(0));
		verify(postRepo, times(1)).findAll();
	}	
	@Test
	void testGetAllPost_Failure() {
		when(postRepo.findAll()).thenReturn(List.of());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllPost();
				});
		
		assertEquals("There are no posts to display", ex.getMessage());
		verify(postRepo, times(1)).findAll();
	}
	
	@Test
	void testGetPostById_Success() throws DeveloperCommunityException {
		when(postRepo.findById(anyInt())).thenReturn(Optional.of(post));
		when(modelMapper.map(post, PostResponseDTO.class)).thenReturn(postResDTO);
		
		PostResponseDTO result = service.getPostById(anyInt());
		
		assertNotNull(result);
		assertEquals(5, result.getPostId());
		verify(postRepo, times(1)).findById(anyInt());
	}
	@Test
	void testGetPostById_Failure() {
		when(postRepo.findById(anyInt())).thenReturn(Optional.empty());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getPostById(anyInt());
				});
		
		assertEquals("Post does not exist", ex.getMessage());
		verify(postRepo, times(1)).findById(anyInt());
	}
	
	@Test
	void testGetAllPostByDev_Success() throws DeveloperCommunityException {
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(postRepo.findByDeveloper(developer)).thenReturn(List.of(post));
		when(modelMapper.map(post, PostResponseDTO.class)).thenReturn(postResDTO);
		
		List<PostResponseDTO> postList = service.getAllPostByDev(anyInt());
		
		assertEquals(1, postList.size());
		assertEquals(postResDTO, postList.get(0));
		assertEquals(1, postList.get(0).getDeveloperId());
		verify(devRepo, times(1)).findById(anyInt());
		verify(postRepo, times(1)).findByDeveloper(developer);
	}
	@Test
	void testGetAllPostByDev_Failure() {
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(postRepo.findByDeveloper(developer)).thenReturn(List.of());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllPostByDev(anyInt());
				});
		
		assertEquals("There are no posts by this Developer", ex.getMessage());
		verify(devRepo, times(1)).findById(anyInt());
		verify(postRepo, times(1)).findByDeveloper(developer);
	}
	
	@Test
	void testGetPostByKeyword_Success() throws DeveloperCommunityException {
		when(postRepo.searchPosts("Java")).thenReturn(List.of(post));
		when(modelMapper.map(post, PostResponseDTO.class)).thenReturn(postResDTO);
		
		List<PostResponseDTO> postList = service.getPostByKeyword("Java");
		
		assertEquals(1, postList.size());
		assertEquals(List.of(postResDTO), postList);
		verify(postRepo, times(1)).searchPosts("Java");
	}
	@Test
	void testGetPostByKeyword_Failure() {
		when(postRepo.searchPosts("Python")).thenReturn(List.of());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getPostByKeyword("Python");
				});
		
		assertEquals("There are no posts with this keyword", ex.getMessage());
		verify(postRepo, times(1)).searchPosts("Python");
	}
	
	@Test
	void testAddPost() {
		when(modelMapper.map(postReqDTO, Post.class)).thenReturn(post);
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(postRepo.save(post)).thenReturn(post);
		
		String result = service.addPost(postReqDTO);
		
		assertNotNull(result);
		assertEquals("The post has been added", result);
		verify(devRepo, times(1)).findById(anyInt());
		verify(postRepo, times(1)).save(post);
	}
	
	@Test
	void testUpdatePost_Success() throws DeveloperCommunityException {
		when(postRepo.findById(anyInt())).thenReturn(Optional.of(post));
		when(modelMapper.map(postReqDTO, Post.class)).thenReturn(post);
		when(postRepo.save(post)).thenReturn(post);
		
		String result = service.updatePost(anyInt(), postReqDTO);
		
		assertNotNull(result);
		assertEquals("The post has been updated", result);
		verify(postRepo, times(1)).findById(anyInt());
		verify(postRepo, times(1)).save(post);
	}
	@Test
	void testUpdatePost_Failure() {
		when(postRepo.findById(anyInt())).thenReturn(Optional.empty());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.updatePost(anyInt(), postReqDTO);
				});
		
		assertEquals("Post does not exist", ex.getMessage());
		verify(postRepo, times(1)).findById(anyInt());
	}
	
	@Test
	void testRemovePost_Success() throws DeveloperCommunityException {
		when(postRepo.existsById(anyInt())).thenReturn(true);
		doNothing().when(postRepo).deleteById(anyInt());
		
		String result = service.removePost(anyInt());
		
		assertNotNull(result);
		assertEquals("The post has been deleted", result);
		verify(postRepo, times(1)).existsById(anyInt());
		verify(postRepo, times(1)).deleteById(anyInt());
	}
	@Test
	void testRemovePost_Failure() {
		when(postRepo.existsById(anyInt())).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removePost(anyInt());
				});
		
		assertEquals("Post does not exist", ex.getMessage());
		verify(postRepo, times(1)).existsById(anyInt());
	}
	
	@Test
	void testRemoveMultiplePost_Success() throws DeveloperCommunityException {
		when(postRepo.existsById(5)).thenReturn(true);
		when(postRepo.existsById(6)).thenReturn(true);
		doNothing().when(postRepo).deleteAllById(List.of(5, 6));
		
		String result = service.removeMultiplePost(List.of(5, 6));
		
		assertNotNull(result);
		assertEquals("The posts have been deleted", result);
		verify(postRepo, times(1)).existsById(5);
		verify(postRepo, times(1)).existsById(6);
		verify(postRepo, times(1)).deleteAllById(List.of(5, 6));
	}
//	test to check if error is thrown when invalid id is given before valid id
	@Test
	void testRemoveMultiplePost_Failure_First() {
		when(postRepo.existsById(8)).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removeMultiplePost(List.of(8, 5));
				});
		
		assertEquals("Post ID: 8 does not exist", ex.getMessage());
		verify(postRepo, times(1)).existsById(8);
	}
//	test to check if error is thrown when invalid id is given after valid id
	@Test
	void testRemoveMultiplePost_Failure_Subsequent() {
		when(postRepo.existsById(5)).thenReturn(true);
		when(postRepo.existsById(8)).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removeMultiplePost(List.of(5, 8));
				});
		
		assertEquals("Post ID: 8 does not exist", ex.getMessage());
		verify(postRepo, times(1)).existsById(5);
		verify(postRepo, times(1)).existsById(8);
	}
	
}
