package com.devcommunity.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "developer")
public class Developer {

	@Id
	private Integer id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	@JsonBackReference
	private User user;

	private String devName;
	private String devSkill;
	private LocalDate memberSince;
	private Integer reputation;
	
	@OneToMany(mappedBy = "developer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Post> listOfPosts;

	@OneToMany(mappedBy = "developer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Comment> listOfComments;

	@OneToMany(mappedBy = "developer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Response> listOfResponses;

	@OneToMany(mappedBy = "developer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference
	private List<Vote> listOfVotes;
}