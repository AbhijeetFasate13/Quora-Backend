package com.devcommunity.entity;

import com.devcommunity.util.VoteType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "votes")
public class Vote {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer voteId;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private VoteType voteType;
	
	@ManyToOne                                          
	@JoinColumn(name = "postId",nullable=true)
	@JsonBackReference
	private Post post;
	
	@ManyToOne
	@JoinColumn(name = "responseId",nullable=true)
	@JsonBackReference
	private Response response;

    @ManyToOne
    @JoinColumn(name = "comment_id",nullable=true)
    @JsonBackReference
    private Comment comment;

	@ManyToOne 
	@JoinColumn(name = "developerId", nullable = false, referencedColumnName = "id")
	@JsonBackReference
	private Developer developer;

}
