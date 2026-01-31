package com.devcommunity.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperResponseDTO {
    private Integer id;
    private String devName;
    private String devSkill;
    private LocalDate memberSince;
    private Integer reputation;
    private int totalPosts;
    private int totalComments;
    private int totalResponses;
    private int totalVotes;
}

