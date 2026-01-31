package com.devcommunity.entity;

import java.time.LocalDate;
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
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int commentId;
	
	private String text;
	private LocalDate createdDate;
	
	@ManyToOne 
	@JoinColumn(name = "postId",nullable = true)
	@JsonBackReference
	private Post post;
	
	@ManyToOne 
	@JoinColumn(name = "responseId",nullable = true)
	@JsonBackReference
	private Response response;
	
	@ManyToOne 
	@JoinColumn(name = "developerId", nullable = false, referencedColumnName = "id")
	@JsonBackReference
	private Developer developer;
	
	@OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Vote> listOfVotes;
	
}
