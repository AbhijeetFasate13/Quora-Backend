package com.devcommunity.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcommunity.dto.DeveloperRequestDTO;
import com.devcommunity.dto.DeveloperResponseDTO;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.service.interfaces.IDeveloperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dev")
@Tag(name = "Developer Controller", description = "Handles operations related to developer entity.")
public class DeveloperController {
	
	private static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);
	
	private IDeveloperService service;
	
	public DeveloperController(IDeveloperService service) {
		this.service = service;
	}
	
	/**
	 * 
	 * @return All developers present in db.
	 */
	@GetMapping("/all")
	@Operation(summary = "Get all developers in the database.")
	public ResponseEntity<List<DeveloperResponseDTO>> getAllDevelopers(){
		logger.info("Get all developers API called.");
		return ResponseEntity.ok(service.getAllDevelopers());
	}
	
	/**
	 * 
	 * @param id
	 * @return Developer with provided id.
	 * @throws DeveloperCommunityException 
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Get the developer by developerId.", description = "Enter the developerId.")
	public ResponseEntity<DeveloperResponseDTO> getDeveloperById(@PathVariable int id) throws DeveloperCommunityException {
		logger.info("Get developer by id API called.");
		return ResponseEntity.ok(service.getDeveloperById(id));
	}
	
	/**
	 * 
	 * @return Developer with max reputation.
	 * @throws DeveloperCommunityException 
	 */
	@GetMapping("/reputation/max")
	@Operation(summary = "Get the developer by max reputation.")
	public ResponseEntity<DeveloperResponseDTO> getDeveloperWithMaxReputation() throws DeveloperCommunityException {
		logger.info("Get dev by max reputation called.");
		return ResponseEntity.ok(service.getByMaxReputation());
	}
	
	/**
	 * 
	 * @param skill
	 * @return Developers having the provided skill.
	 */
	@GetMapping("/skill/{skill}")
	@Operation(summary = "Get all developers by their skill.", description = "Enter the skill.")
	public ResponseEntity<List<DeveloperResponseDTO>> getDevelopersHavingSkill(@PathVariable String skill){
		logger.info("Get all developers by skill API called.");
		return ResponseEntity.ok(service.searchDevelopersBySkill(skill));
	}
	
	/**
	 * 
	 * @param id
	 * @param dto
	 * @return String acknowledging if the developer is added or not.
	 * @throws DeveloperCommunityException 
	 */
	@PostMapping("/create/{id}")
	@Operation(summary = "Add developer.", description = "Enter the developerId and devName and/or devSkill.")
	public ResponseEntity<String> addDeveloper(@PathVariable int id,@RequestBody DeveloperRequestDTO dto) throws DeveloperCommunityException {
		return ResponseEntity.ok(service.addDeveloper(id,dto));
	}
	
	/**
	 * 
	 * @param id
	 * @param dto
	 * @return String acknowledging if the developer is updated or not.
	 * @throws DeveloperCommunityException 
	 */
	@PutMapping("/update/{id}")
	@Operation(summary = "Update developer.", description = "Enter the developerId and devName and/or devSkill.")
	public ResponseEntity<String> updateDeveloper(@PathVariable int id, @RequestBody DeveloperRequestDTO dto) throws DeveloperCommunityException {
		logger.info("Get all responses by post API called");
		return ResponseEntity.ok(service.updateDeveloper(id,dto));
	}
}
