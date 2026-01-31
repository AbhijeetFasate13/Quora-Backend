package com.devcommunity.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.devcommunity.dto.DeveloperRequestDTO;
import com.devcommunity.dto.DeveloperResponseDTO;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.User;
import com.devcommunity.entity.Vote;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IUserRepo;
import com.devcommunity.repository.IVoteRepo;
import com.devcommunity.service.interfaces.IDeveloperService;
import com.devcommunity.util.VoteType;

@Service
public class DeveloperServiceImpl implements IDeveloperService {
	private IDevRepo iDevRepo;
	private IUserRepo iUserRepo;
	private ModelMapper modelMapper;
	private IVoteRepo iVoteRepo;

	public DeveloperServiceImpl(IDevRepo iDevRepo, IUserRepo iUserRepo, ModelMapper modelMapper, IVoteRepo iVoteRepo) {
		this.iDevRepo = iDevRepo;
		this.iUserRepo = iUserRepo;
		this.modelMapper = modelMapper;
		this.iVoteRepo = iVoteRepo;
	}

	private DeveloperResponseDTO developerFieldSetterHelper(Developer developer) {
		DeveloperResponseDTO devResponseDTO = modelMapper.map(developer, DeveloperResponseDTO.class);
		devResponseDTO.setTotalPosts(developer.getListOfPosts().size());
		devResponseDTO.setTotalResponses(developer.getListOfResponses().size());
		devResponseDTO.setTotalComments(developer.getListOfComments().size());
		devResponseDTO.setTotalVotes(developer.getListOfVotes().size());
		List<Vote> votes = iVoteRepo.findAll();
		votes = votes.stream()
				.filter(x -> developer.getListOfPosts().contains(x.getPost())
						|| developer.getListOfResponses().contains(x.getResponse())
						|| developer.getListOfComments().contains(x.getComment()))
				.toList();
		int reputation = votes.stream().mapToInt(x -> x.getVoteType().equals(VoteType.UPVOTE) ? 1 : 0).reduce(0,
				(a, b) -> a + b);
		reputation += votes.stream().mapToInt(x -> x.getVoteType().equals(VoteType.DOWNVOTE) ? -1 : 0).reduce(0,
				(a, b) -> a + b);
		devResponseDTO.setReputation(reputation / 5);
		return devResponseDTO;
	}

	@Override
	public String addDeveloper(int id, DeveloperRequestDTO dto) throws DeveloperCommunityException {
		User user = iUserRepo.findById(id).orElseThrow(() -> new DeveloperCommunityException("User not found."));
		Developer developer = modelMapper.map(dto, Developer.class);
		developer.setUser(user);
		developer.setMemberSince(LocalDate.now());
		developer.setReputation(0);
		iDevRepo.save(developer);

		return "Developer added successfully!";
	}

	@Override
	public String updateDeveloper(int id, DeveloperRequestDTO dto) throws DeveloperCommunityException {
		if (iDevRepo.findById(id).isEmpty()) {
			addDeveloper(id, dto);
		}
		Developer developer = iDevRepo.findById(id).get();
		String newDevName = dto.getDevName();
		String newDevSkill = dto.getDevSkill();
		if (newDevName != null)
			developer.setDevName(dto.getDevName());
		if (newDevSkill != null)
			developer.setDevSkill(dto.getDevSkill());
		iDevRepo.save(developer);
		return "Developer updated successfully!";
	}

	@Override
	public DeveloperResponseDTO getDeveloperById(Integer devId) throws DeveloperCommunityException {
		Developer developer = iDevRepo.findById(devId)
				.orElseThrow(() -> new DeveloperCommunityException("Developer not found."));

		return developerFieldSetterHelper(developer);
	}

	@Override
	public List<DeveloperResponseDTO> getAllDevelopers() {
		List<Developer> dbResult = iDevRepo.findAll();
		List<DeveloperResponseDTO> responseResult = new ArrayList<>();
		for (Developer developer : dbResult) {
			responseResult.add(developerFieldSetterHelper(developer));
		}
		return responseResult;
	}

	@Override
	public DeveloperResponseDTO getByMaxReputation() throws DeveloperCommunityException {
		Developer developer = iDevRepo.findTopByOrderByReputationDesc();
		if (developer == null) {
			throw new DeveloperCommunityException("There are no developers in the database.");
		}
		return developerFieldSetterHelper(developer);
	}

	@Override
	public List<DeveloperResponseDTO> searchDevelopersBySkill(String skill) {
		List<Developer> dbResult = iDevRepo.findByDevSkillContainingIgnoreCase(skill);
		List<DeveloperResponseDTO> responseResult = new ArrayList<>();
		for (Developer developer : dbResult) {
			responseResult.add(developerFieldSetterHelper(developer));
		}
		return responseResult;
	}
}