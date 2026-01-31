package com.devcommunity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.PostRequestDTO;
import com.devcommunity.dto.PostResponseDTO;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IPostRepo;
import com.devcommunity.service.interfaces.IPostService;

@Service
public class PostServiceImpl implements IPostService {

	private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

	private static final String POST_UPDATED = "The post has been updated";
	private static final String POST_DELETED = "The post has been deleted";
	private static final String POSTS_DELETED = "The posts have been deleted";
	private static final String POST_CREATED = "The post has been added";
	private static final String TABLE_EMPTY = "There are no posts to display";
	private static final String DEV_ID_NOT_FOUND = "Developer does not exist";
	private static final String POST_ID_NOT_FOUND = "Post does not exist";
	private static final String POST_BY_DEV_NOT_FOUND = "There are no posts by this Developer";
	private static final String POST_BY_KEYWORD_NOT_FOUND = "There are no posts with this keyword";

	private final IDevRepo devRepo;
	private final IPostRepo postRepo;
	private final ModelMapper modelMapper;

	public PostServiceImpl(IPostRepo postRepo, ModelMapper modelMapper, IDevRepo devRepo) {
		this.devRepo = devRepo;
		this.postRepo = postRepo;
		this.modelMapper = modelMapper;
	}

	/**
	 * Fetches all posts
	 * 
	 * @return list of posts
	 * @throws DeveloperCommunityException if there are no posts
	 */
	@Override
	public List<PostResponseDTO> getAllPost() throws DeveloperCommunityException {
		List<Post> postList = postRepo.findAll();

		if (postList.isEmpty()) {
			logger.error(TABLE_EMPTY);
			throw (new DeveloperCommunityException(TABLE_EMPTY));
		}

		return postList.stream().map(e -> modelMapper.map(e, PostResponseDTO.class)).toList();
	}

	/**
	 * Fetches post by id
	 * 
	 * @param postId of the post
	 * @return post
	 * @throws DeveloperCommunityException if the post id does not exist
	 */
	@Override
	public PostResponseDTO getPostById(Integer postId) throws DeveloperCommunityException {
		Optional<Post> post = postRepo.findById(postId);

		if (post.isEmpty()) {
			logger.error(POST_ID_NOT_FOUND);
			throw (new DeveloperCommunityException(POST_ID_NOT_FOUND));
		}

		return modelMapper.map(post.get(), PostResponseDTO.class);
	}

	/**
	 * Fetches post by developer
	 * 
	 * @param devId of the developer
	 * @return List of posts
	 * @throws DeveloperCommunityException if there are no posts by the developer
	 */
	@Override
	public List<PostResponseDTO> getAllPostByDev(Integer devId) throws DeveloperCommunityException {
		Developer developer = devRepo.findById(devId)
				.orElseThrow(() -> new DeveloperCommunityException(DEV_ID_NOT_FOUND));

		List<Post> postList = postRepo.findByDeveloper(developer);

		if (postList.isEmpty()) {
			logger.error(POST_BY_DEV_NOT_FOUND);
			throw (new DeveloperCommunityException(POST_BY_DEV_NOT_FOUND));
		}

		return postList.stream().map(e -> modelMapper.map(e, PostResponseDTO.class)).toList();
	}

	/**
	 * Fetches post by keyword checks if keyword is present in the query or topic
	 * 
	 * @param keyword
	 * @return List of posts
	 * @throws DeveloperCommunityException if there are not posts with the keyword
	 *                                     present
	 */
	@Override
	public List<PostResponseDTO> getPostByKeyword(String keyword) throws DeveloperCommunityException {
		List<Post> postList = postRepo.searchPosts(keyword);

		if (postList.isEmpty()) {
			logger.error(POST_BY_KEYWORD_NOT_FOUND);
			throw (new DeveloperCommunityException(POST_BY_KEYWORD_NOT_FOUND));
		}

		return postList.stream().map(e -> modelMapper.map(e, PostResponseDTO.class)).toList();
	}

	/**
	 * Adds a post
	 * 
	 * @param postDTO containing post details
	 * @return string confirming operation
	 */
	@Override
	public String addPost(PostRequestDTO postDTO) {
		Post post = modelMapper.map(postDTO, Post.class);
		post.setPostId(null);
		Optional<Developer> developer = devRepo.findById(postDTO.getDeveloperId());

		if (developer.isPresent())
			post.setDeveloper(developer.get());
		post.setPostDateTime(LocalDateTime.now());

		postRepo.save(post);

		return POST_CREATED;
	}

	/**
	 * Updates the post by its id
	 * 
	 * @param postId  of the post
	 * @param postDTO containing details of the post
	 * @return string confirming operation
	 * @throws DeveloperCommunityException if post id does not exist
	 */
	@Override
	public String updatePost(Integer postId, PostRequestDTO postDTO) throws DeveloperCommunityException {
		Optional<Post> post = postRepo.findById(postId);

		if (post.isEmpty()) {
			logger.error(POST_ID_NOT_FOUND);
			throw (new DeveloperCommunityException(POST_ID_NOT_FOUND));
		}

		Post updatePost = modelMapper.map(postDTO, Post.class);

//		checking if it is blank, to set previous values to the field.
//		otherwise field will be blank or user will be forced to enter values for every field
		if (postDTO.getQuery() == null)
			updatePost.setQuery(post.get().getQuery());
		if (postDTO.getTopic() == null)
			updatePost.setTopic(post.get().getTopic());

		updatePost.setPostId(postId);
		updatePost.setPostDateTime(LocalDateTime.now());
		
		updatePost.setResponses(post.get().getResponses());
		updatePost.setComments(post.get().getComments());
		updatePost.setVotes(post.get().getVotes());

		postRepo.save(updatePost);

		return POST_UPDATED;
	}

	/**
	 * Deletes the post by its id
	 * 
	 * @param postId of the post
	 * @return string confirming the operation
	 * @throws DeveloperCommunityException if post id does not exist
	 */
	@Override
	public String removePost(Integer postId) throws DeveloperCommunityException {
		if (postRepo.existsById(postId)) {
			postRepo.deleteById(postId);

			return POST_DELETED;
		}

		logger.error(POST_ID_NOT_FOUND);
		throw (new DeveloperCommunityException(POST_ID_NOT_FOUND));
	}

	/**
	 * Deletes the posts by its ids Only deletes if all the ids exist, batch
	 * operation
	 * 
	 * @param List of ids
	 * @return string confirming the operation
	 * @throws DeveloperCommunityException if post id does not exist
	 */
	@Override
	public String removeMultiplePost(List<Integer> postIds) throws DeveloperCommunityException {

		for (Integer id : postIds) {
			if (!postRepo.existsById(id)) {
				logger.error("Post ID: " + id + " does not exist"); //NOSONAR
				throw (new DeveloperCommunityException("Post ID: " + id + " does not exist"));
			}
		}

		postRepo.deleteAllById(postIds);

		return POSTS_DELETED;
	}

}
