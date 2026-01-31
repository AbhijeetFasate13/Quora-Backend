package com.devcommunity.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.devcommunity.dto.ResponseRequestDTO;
import com.devcommunity.dto.ResponseResponseDTO;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.service.impl.ResponseServiceImpl;

/**
 * Unit tests for ResponseServiceImpl
 * Covers success and failure scenarios for all methods
 */
@ExtendWith(MockitoExtension.class)
class ResponseServiceImplTest {
	
	@Mock
	private IResponseRepo respRepo;
	
	@Mock
	private IPostRepo postRepo;
	
	@Mock
	private IDevRepo devRepo;
	
	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private ResponseServiceImpl service;
	
	private Developer developer;
	private Post post;
	private Response response;
	private ResponseRequestDTO respReqDTO;
	private ResponseResponseDTO respResDTO;
	
	@BeforeEach
	void setUp() {
		developer = new Developer();
		developer.setId(2);
		developer.setDevName("Gopi");
		developer.setDevSkill("Java");
		developer.setMemberSince(LocalDate.now());
		developer.setReputation(50);
		
		post = new Post();
		post.setPostId(5);
		post.setDeveloper(developer);
		post.setQuery("I need help with the project! Pls send helppp");
		post.setTopic("Java");
		post.setPostDateTime(LocalDateTime.now());
		
		response = new Response();
		response.setRespId(2);
		response.setAnswer("Pray. Or maybe learn, Or maybe pray and learn");
		response.setRespDateTime(LocalDateTime.now());
		response.setPost(post);
		response.setDeveloper(developer);
		
		respReqDTO = new ResponseRequestDTO();
		respReqDTO.setAnswer("Pray. Or maybe learn, Or maybe pray and learn");
		respReqDTO.setDeveloperId(2);
		respReqDTO.setPostId(5);
		
		respResDTO = new ResponseResponseDTO();
		respResDTO.setRespId(2);
		respResDTO.setAnswer("Pray. Or maybe learn, Or maybe pray and learn");
		respResDTO.setRespDateTime(LocalDateTime.now());
		respResDTO.setDeveloperId(2);
		respResDTO.setPostId(5);
	}
	
	@Test
	void testGetAllResponseByPost_Success() throws DeveloperCommunityException {
		when(postRepo.existsById(anyInt())).thenReturn(true);
		when(respRepo.findByPost_postId(anyInt())).thenReturn(List.of(response));
		when(modelMapper.map(response, ResponseResponseDTO.class)).thenReturn(respResDTO);
		
		List<ResponseResponseDTO> respList = service.getAllResponseByPost(anyInt());
		
		assertEquals(1, respList.size());
		assertEquals(respResDTO, respList.get(0));
		verify(postRepo, times(1)).existsById(anyInt());
		verify(respRepo, times(1)).findByPost_postId(anyInt());
	}
	@Test
	void testGetAllResponsesByPost_Failure_Post() {
		when(postRepo.existsById(anyInt())).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllResponseByPost(anyInt());
				});
		
		assertEquals("Post does not exist", ex.getMessage());
		verify(postRepo, times(1)).existsById(anyInt());
	}
	@Test
	void testGetAllResponsesByPost_Failure_Resp() {
		when(postRepo.existsById(anyInt())).thenReturn(true);
		when(respRepo.findByPost_postId(anyInt())).thenReturn(List.of());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllResponseByPost(anyInt());
				});
		
		assertEquals("There are no responses for this post", ex.getMessage());
		verify(postRepo, times(1)).existsById(anyInt());
		verify(respRepo, times(1)).findByPost_postId(anyInt());
	}
	
	@Test
	void testGetAllResponseByDeveloper_Success() throws DeveloperCommunityException {
		when(devRepo.existsById(anyInt())).thenReturn(true);
		when(respRepo.findByDeveloper_id(anyInt())).thenReturn(List.of(response));
		when(modelMapper.map(response, ResponseResponseDTO.class)).thenReturn(respResDTO);
		
		List<ResponseResponseDTO> respList = service.getAllResponseByDeveloper(anyInt());
		
		assertEquals(1, respList.size());
		assertEquals(respResDTO, respList.get(0));
		verify(devRepo, times(1)).existsById(anyInt());
		verify(respRepo, times(1)).findByDeveloper_id(anyInt());
	}
	@Test
	void testGetAllResponseByDeveloper_Failure_Dev() {
		when(devRepo.existsById(anyInt())).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllResponseByDeveloper(anyInt());
				});
		
		assertEquals("Developer does not exist", ex.getMessage());
		verify(devRepo, times(1)).existsById(anyInt());
	}
	@Test
	void testGetAllResponseByDeveloper_Failure_Resp() {
		when(devRepo.existsById(anyInt())).thenReturn(true);
		when(respRepo.findByDeveloper_id(anyInt())).thenReturn(List.of());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.getAllResponseByDeveloper(anyInt());
				});
		
		assertEquals("There are no responses by this developer", ex.getMessage());
		verify(devRepo, times(1)).existsById(anyInt());
		verify(respRepo, times(1)).findByDeveloper_id(anyInt());
	}
	
	@Test
	void testAddResponse() {
		when(modelMapper.map(respReqDTO, Response.class)).thenReturn(response);
		when(postRepo.findById(anyInt())).thenReturn(Optional.of(post));
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(respRepo.save(response)).thenReturn(response);
		
		String result = service.addResponse(respReqDTO);
		
		assertNotNull(result);
		assertEquals("The response has been added", result);
		verify(postRepo, times(1)).findById(anyInt());
		verify(devRepo, times(1)).findById(anyInt());
		verify(respRepo, times(1)).save(response);
	}
	
	@Test
	void testUpdateResponse_Success() throws DeveloperCommunityException {
		when(respRepo.findById(anyInt())).thenReturn(Optional.of(response));
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(postRepo.findById(anyInt())).thenReturn(Optional.of(post));
		when(modelMapper.map(respReqDTO, Response.class)).thenReturn(response);
		when(respRepo.save(response)).thenReturn(response);
		
		String result = service.updateResponse(anyInt(), respReqDTO);
		
		assertNotNull(result);
		assertEquals("The response has been updated", result);
		verify(respRepo, times(1)).findById(anyInt());
		verify(devRepo, times(1)).findById(anyInt());
		verify(postRepo, times(1)).findById(anyInt());
		verify(respRepo, times(1)).save(response);
	}
	@Test
	void testUpdateResponse_Failure_Resp() {
		when(respRepo.findById(anyInt())).thenReturn(Optional.empty());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.updateResponse(anyInt(), respReqDTO);
				});
		
		assertEquals("Response does not exist", ex.getMessage());
		verify(respRepo, times(1)).findById(anyInt());
	}
	@Test
	void testUpdateResponse_Failure_Dev() {
		when(respRepo.findById(anyInt())).thenReturn(Optional.of(response));
		when(devRepo.findById(anyInt())).thenReturn(Optional.empty());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.updateResponse(anyInt(), respReqDTO);
				});
		
		assertEquals("Developer does not exist", ex.getMessage());
		verify(devRepo, times(1)).findById(anyInt());
	}
	@Test
	void testUpdateResponse_Failure_Post() {
		when(respRepo.findById(anyInt())).thenReturn(Optional.of(response));
		when(devRepo.findById(anyInt())).thenReturn(Optional.of(developer));
		when(postRepo.findById(anyInt())).thenReturn(Optional.empty());
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.updateResponse(anyInt(), respReqDTO);
				});
		
		assertEquals("Post does not exist", ex.getMessage());
		verify(postRepo, times(1)).findById(anyInt());
	}
	
	@Test
	void testRemoveResponse_Success() throws DeveloperCommunityException {
		when(respRepo.existsById(anyInt())).thenReturn(true);
		doNothing().when(respRepo).deleteById(anyInt());
		
		String result = service.removeResponse(anyInt());
		
		assertNotNull(result);
		assertEquals("The response has been deleted", result);
		verify(respRepo, times(1)).existsById(anyInt());
		verify(respRepo, times(1)).deleteById(anyInt());
	}
	@Test
	void testRemoveResponse_Failure() {
		when(respRepo.existsById(anyInt())).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removeResponse(anyInt());
				});
		
		assertEquals("Response does not exist", ex.getMessage());
		verify(respRepo, times(1)).existsById(anyInt());
	}
	
	@Test
	void testRemoveMultipleResponse_Success() throws DeveloperCommunityException {
		when(respRepo.existsById(2)).thenReturn(true);
		when(respRepo.existsById(3)).thenReturn(true);
		doNothing().when(respRepo).deleteAllById(List.of(2, 3));
		
		String result = service.removeMultipleResponse(List.of(2, 3));
		
		assertNotNull(result);
		assertEquals("The responses have been deleted", result);
		verify(respRepo, times(1)).existsById(2);
		verify(respRepo, times(1)).existsById(3);
		verify(respRepo, times(1)).deleteAllById(List.of(2, 3));
	}
//	test to check if error is thrown when invalid id is given before valid id
	@Test
	void testRemoveMultipleResponse_Failure_First() {
		when(respRepo.existsById(8)).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removeMultipleResponse(List.of(8, 2));
				});
		
		assertEquals("Response ID: 8 does not exist", ex.getMessage());
		verify(respRepo, times(1)).existsById(8);
	}
//	test to check if error is thrown when invalid id is given after valid id
	@Test
	void testRemoveMultipleResponse_Failure_Subsequent() {
		when(respRepo.existsById(2)).thenReturn(true);
		when(respRepo.existsById(8)).thenReturn(false);
		
		DeveloperCommunityException ex = 
				assertThrows(DeveloperCommunityException.class, () -> {
					service.removeMultipleResponse(List.of(2, 8));
				});
		
		assertEquals("Response ID: 8 does not exist", ex.getMessage());
		verify(respRepo, times(1)).existsById(2);
		verify(respRepo, times(1)).existsById(8);
	}
	
}

