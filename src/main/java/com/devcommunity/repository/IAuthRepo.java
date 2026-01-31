package com.devcommunity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devcommunity.entity.User;


@Repository
public interface IAuthRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}