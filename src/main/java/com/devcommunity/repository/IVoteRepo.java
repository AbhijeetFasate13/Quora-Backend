package com.devcommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcommunity.entity.Vote;
import com.devcommunity.util.VoteType;


public interface IVoteRepo extends JpaRepository<Vote,Integer> 
{
	
	public Optional<Vote> findByVoteId(int voteId);
	public List<Vote> findByResponse_RespId(int responseId);
	public List<Vote> findByPost_PostId(int postId);
	public List<Vote> getByResponse_RespId(int responseId);
	public List<Vote> getByPost_PostId(int postId);
	public int countByVoteTypeAndComment_CommentId(VoteType type, int commentId);
	public List<Vote> findByComment_CommentId(int commentId);
	public List<Vote> findByDeveloperIdAndPost_PostId(int developerId, int postId);
	public List<Vote> findByDeveloperIdAndComment_CommentId(int developerId, int commentId);
	public List<Vote> findByDeveloperIdAndResponse_RespId(Integer id, Integer respId);

}
