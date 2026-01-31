package com.devcommunity.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Response {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer respId;
	
	private String answer;
	
	private LocalDateTime respDateTime;
	
	@ManyToOne
	@JoinColumn(name = "postId")
	@JsonBackReference
	private Post post;
	
	@ManyToOne
	@JoinColumn(name = "developerId")
	@JsonBackReference
	private Developer developer;
	
	@OneToMany(mappedBy = "response", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Comment> listOfComments;
	
	@OneToMany(mappedBy = "response", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Vote> listOfVotes;

}
