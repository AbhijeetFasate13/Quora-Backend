package com.devcommunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperRequestDTO {
	private String devName;
	private String devSkill;
}