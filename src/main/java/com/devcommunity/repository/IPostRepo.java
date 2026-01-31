package com.devcommunity.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;

public interface IPostRepo extends JpaRepository<Post, Integer>{
	
	List<Post> findByDeveloper(Developer developer);
	List<Post> findByTopic(String topic);
	
	//to get posts which contains the keyword in both query and topic
	@Query("SELECT p FROM Post p "
			+ "WHERE LOWER(p.query) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(p.topic) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Post> searchPosts(@Param("keyword") String keyword);

	
}
