package com.devcommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devcommunity.entity.Comment;

@Repository
public interface ICommentRepo extends JpaRepository<Comment,Integer> {

	List<Comment> findByDeveloperId(int developerId);

	List<Comment> findByPost_PostId(int postId);

	List<Comment> findByResponse_RespId(int respId);

	Optional<Comment> findByCommentId(int commentId);



}
