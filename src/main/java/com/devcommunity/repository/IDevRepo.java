package com.devcommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devcommunity.entity.Developer;

@Repository
public interface IDevRepo extends JpaRepository<Developer, Integer> {

    Developer findTopByOrderByReputationDesc();
    List<Developer> findByDevSkillContainingIgnoreCase(String skill);
}