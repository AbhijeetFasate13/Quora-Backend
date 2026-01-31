package com.devcommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcommunity.entity.Response;

public interface IResponseRepo extends JpaRepository<Response, Integer>{

	List<Response> findByPost_postId(Integer postId);
	List<Response> findByDeveloper_id(Integer devId);
}
