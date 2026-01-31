package com.devcommunity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.ResponseRequestDTO;
import com.devcommunity.dto.ResponseResponseDTO;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.repository.IResponseRepo;
import com.devcommunity.service.interfaces.IResponseService;

@Service
public class ResponseServiceImpl implements IResponseService {

	private static final Logger logger = LoggerFactory.getLogger(ResponseServiceImpl.class);

	private static final String RESP_CREATED = "The response has been added";
	private static final String RESP_UPDATED = "The response has been updated";
	private static final String RESP_DELETED = "The response has been deleted";
	private static final String RESPS_DELETED = "The responses have been deleted";
	private static final String RESP_NOT_FOUND_FOR_POST = "There are no responses for this post";
	private static final String RESP_NOT_FOUND_FOR_DEV = "There are no responses by this developer";
	private static final String RESP_NOT_FOUND = "Response does not exist";
	private static final String POST_NOT_FOUND = "Post does not exist";
	private static final String DEV_NOT_FOUND = "Developer does not exist";

	private final IResponseRepo respRepo;
	private final IPostRepo postRepo;
	private final IDevRepo devRepo;
	private final ModelMapper modelMapper;

	public ResponseServiceImpl(IResponseRepo respRepo, IPostRepo postRepo, IDevRepo devRepo, ModelMapper modelMapper) {
		this.respRepo = respRepo;
		this.postRepo = postRepo;
		this.devRepo = devRepo;
		this.modelMapper = modelMapper;
	}

	/**
	 * Fetches all responses for a post by post id
	 * 
	 * @param postId of the post
	 * @return List of responses
	 * @throws DeveloperCommunityException if there are no responses to the post
	 */
	@Override
	public List<ResponseResponseDTO> getAllResponseByPost(Integer postId) throws DeveloperCommunityException {
		if (postRepo.existsById(postId)) {
			List<Response> responses = respRepo.findByPost_postId(postId);

			if (responses.isEmpty()) {
				throw (new DeveloperCommunityException(RESP_NOT_FOUND_FOR_POST));
			}

			return responses.stream().map(e -> modelMapper.map(e, ResponseResponseDTO.class))
					.toList();
		}

		throw (new DeveloperCommunityException(POST_NOT_FOUND));
	}

	/**
	 * Fetches all responses by a developer
	 * 
	 * @param devId of the developer
	 * @return List of responses
	 * @throws DeveloperCommunityException if there are no responses by the
	 *                                     developer or if there is no developer
	 *                                     with that id
	 */
	@Override
	public List<ResponseResponseDTO> getAllResponseByDeveloper(Integer devId) throws DeveloperCommunityException {
		if (devRepo.existsById(devId)) {
			List<Response> responses = respRepo.findByDeveloper_id(devId);

			if (responses.isEmpty()) {
				throw (new DeveloperCommunityException(RESP_NOT_FOUND_FOR_DEV));
			}

			return responses.stream().map(e -> modelMapper.map(e, ResponseResponseDTO.class))
					.toList();
		}

		throw (new DeveloperCommunityException(DEV_NOT_FOUND));
	}

	/**
	 * Adds a response
	 * 
	 * @param responseDTO containing details
	 * @return string confirming operation
	 */
	@Override
	public String addResponse(ResponseRequestDTO responseDTO) {
		Response response = modelMapper.map(responseDTO, Response.class);

		Optional<Post> post = postRepo.findById(responseDTO.getPostId());
		Optional<Developer> developer = devRepo.findById(responseDTO.getDeveloperId());

		if (post.isPresent())
			response.setPost(post.get());
		if (developer.isPresent())
			response.setDeveloper(developer.get());
		response.setRespDateTime(LocalDateTime.now());

		respRepo.save(response);

		return RESP_CREATED;
	}

	/**
	 * Updates a response by its id
	 * 
	 * @param respId      of the response
	 * @param responseDTO containing details
	 * @return string confirming operation
	 * @throws DeveloperCommunityException if response id or post id or developer id
	 *                                     does not exist
	 */
	@Override
	public String updateResponse(Integer respId, ResponseRequestDTO responseDTO) throws DeveloperCommunityException {
		Optional<Response> response = respRepo.findById(respId);
		Optional<Developer> developer = devRepo.findById(responseDTO.getDeveloperId());
		Optional<Post> post = postRepo.findById(responseDTO.getPostId());

		if (response.isEmpty()) {
			throw (new DeveloperCommunityException(RESP_NOT_FOUND));
		}
		if (developer.isEmpty()) {
			throw (new DeveloperCommunityException(DEV_NOT_FOUND));
		}
		if (post.isEmpty()) {
			throw (new DeveloperCommunityException(POST_NOT_FOUND));
		}

		Response updateResponse = modelMapper.map(responseDTO, Response.class);
		updateResponse.setRespId(response.get().getRespId());
		updateResponse.setPost(post.get());
		updateResponse.setDeveloper(developer.get());
		updateResponse.setRespDateTime(LocalDateTime.now());
		
		updateResponse.setListOfComments(response.get().getListOfComments());
		updateResponse.setListOfVotes(response.get().getListOfVotes());

		respRepo.save(updateResponse);

		return RESP_UPDATED;
	}

	/**
	 * Deletes a response by its id
	 * 
	 * @param respId of the response
	 * @return string confirming operation
	 * @throws DeveloperCommunityException if the response id does not exist
	 */
	@Override
	public String removeResponse(Integer respId) throws DeveloperCommunityException {
		if (respRepo.existsById(respId)) {
			respRepo.deleteById(respId);

			return RESP_DELETED;
		}

		throw (new DeveloperCommunityException(RESP_NOT_FOUND));
	}

	/**
	 * Deletes responses by its ids Only deletes if all the ids exist, batch
	 * operation
	 * 
	 * @param List of ids
	 * @return string confirming operation
	 * @throws DeveloperCommunityException if response id does not exist
	 */
	@Override
	public String removeMultipleResponse(List<Integer> respIds) throws DeveloperCommunityException {

		for (Integer id : respIds) {
			if (!respRepo.existsById(id)) {
				logger.error("Response ID: " + id + " does not exist"); //NOSONAR
				throw (new DeveloperCommunityException("Response ID: " + id + " does not exist"));
			}
		}

		respRepo.deleteAllById(respIds);

		return RESPS_DELETED;
	}

}
