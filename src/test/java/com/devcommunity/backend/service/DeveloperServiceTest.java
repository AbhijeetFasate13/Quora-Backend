package com.devcommunity.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import com.devcommunity.dto.DeveloperRequestDTO;
import com.devcommunity.dto.DeveloperResponseDTO;
import com.devcommunity.entity.Comment;
import com.devcommunity.entity.Developer;
import com.devcommunity.entity.Post;
import com.devcommunity.entity.Response;
import com.devcommunity.entity.User;
import com.devcommunity.entity.Vote;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IDevRepo;
import com.devcommunity.repository.IUserRepo;
import com.devcommunity.service.impl.DeveloperServiceImpl;
import com.devcommunity.util.VoteType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeveloperServiceTest {

    @Mock
    private IDevRepo iDevRepo;

    @Mock
    private IUserRepo iUserRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DeveloperServiceImpl service;

    private Developer developer;
    private DeveloperResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(1);
        developer.setDevName("John");
        developer.setDevSkill("Java");
        developer.setMemberSince(LocalDate.of(2025, 1, 1));
        developer.setReputation(2);

        developer.setListOfPosts(Arrays.asList(new Post(), new Post()));
        developer.setListOfResponses(Arrays.asList(new Response()));
        developer.setListOfComments(Arrays.asList(new Comment(), new Comment(), new Comment()));
        Vote upVote = new Vote();
        upVote.setVoteType(VoteType.UPVOTE);
        Vote downVote = new Vote();
        downVote.setVoteType(VoteType.DOWNVOTE);
        developer.setListOfVotes(Arrays.asList(
            upVote,upVote,upVote,downVote,downVote
        ));

        responseDTO = new DeveloperResponseDTO();
        responseDTO.setId(1);
        responseDTO.setDevName("John");
        responseDTO.setDevSkill("Java");
        responseDTO.setReputation(0); // (3 - 2) / 5 = 0
        responseDTO.setTotalPosts(2);
        responseDTO.setTotalResponses(1);
        responseDTO.setTotalComments(3);
        responseDTO.setTotalVotes(5);
    }

    @Test
    void testGetDeveloperById_Success() throws DeveloperCommunityException {
        when(iDevRepo.findById(1)).thenReturn(Optional.of(developer));
        when(modelMapper.map(developer, DeveloperResponseDTO.class)).thenReturn(new DeveloperResponseDTO());

        DeveloperResponseDTO result = service.getDeveloperById(1);
        assertNotNull(result);
        assertEquals(0, result.getReputation());
        assertEquals(2, result.getTotalPosts());
        assertEquals(3, result.getTotalComments());
    }

    @Test
    void testGetDeveloperById_NotFound() {
        when(iDevRepo.findById(99)).thenReturn(Optional.empty());

        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.getDeveloperById(99)
        );

        assertEquals("Developer not found.", exception.getMessage());
    }

    @Test
    void testGetAllDevelopers_EmptyList() {
        when(iDevRepo.findAll()).thenReturn(Collections.emptyList());

        List<DeveloperResponseDTO> result = service.getAllDevelopers();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllDevelopers_Success() {
        when(iDevRepo.findAll()).thenReturn(Arrays.asList(developer));
        when(modelMapper.map(developer, DeveloperResponseDTO.class)).thenReturn(responseDTO);

        List<DeveloperResponseDTO> result = service.getAllDevelopers();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getDevName());
    }

    @Test
    void testGetByMaxReputation_Success() throws DeveloperCommunityException {
        when(iDevRepo.findTopByOrderByReputationDesc()).thenReturn(developer);
        when(modelMapper.map(developer, DeveloperResponseDTO.class)).thenReturn(new DeveloperResponseDTO());

        DeveloperResponseDTO result = service.getByMaxReputation();
        assertNotNull(result);
        assertEquals(0, result.getReputation());
    }

    @Test
    void testGetByMaxReputation_NoDeveloper() {
        when(iDevRepo.findTopByOrderByReputationDesc()).thenReturn(null);

        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.getByMaxReputation()
        );

        assertEquals("There are no developers in the database.", exception.getMessage());
    }

    @Test
    void testSearchDevelopersBySkill_Success() {
        when(iDevRepo.findByDevSkillContainingIgnoreCase("Java")).thenReturn(Arrays.asList(developer));
        when(modelMapper.map(developer, DeveloperResponseDTO.class)).thenReturn(responseDTO);

        List<DeveloperResponseDTO> result = service.searchDevelopersBySkill("Java");
        assertFalse(result.isEmpty());
        assertEquals("Java", result.get(0).getDevSkill());
    }

    @Test
    void testAddDeveloper_Success() throws DeveloperCommunityException {
        DeveloperRequestDTO requestDTO = new DeveloperRequestDTO();
        requestDTO.setDevName("John");
        requestDTO.setDevSkill("Java");

        User user = new User();
        when(iUserRepo.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(requestDTO, Developer.class)).thenReturn(developer);

        String result = service.addDeveloper(1, requestDTO);
        assertEquals("Developer added successfully!", result);
        verify(iDevRepo, times(1)).save(developer);
    }

    @Test
    void testUpdateDeveloper_Success() throws DeveloperCommunityException {
        DeveloperRequestDTO requestDTO = new DeveloperRequestDTO();
        requestDTO.setDevName("Johnny");
        requestDTO.setDevSkill("Spring");

        when(iDevRepo.findById(1)).thenReturn(Optional.of(developer));

        String result = service.updateDeveloper(1, requestDTO);
        assertEquals("Developer updated successfully!", result);
        assertEquals("Johnny", developer.getDevName());
        assertEquals("Spring", developer.getDevSkill());
    }
}