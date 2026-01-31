package com.devcommunity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcommunity.entity.User;

public interface IUserRepo extends JpaRepository<User, Integer> {

	boolean existsByUsername(String newUsername);

	Optional<User> findByUsername(String username);
  
}