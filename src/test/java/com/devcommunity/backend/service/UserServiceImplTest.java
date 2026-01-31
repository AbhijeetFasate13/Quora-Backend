package com.devcommunity.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devcommunity.dto.UserRequestDTO;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.IUserRepo;
import com.devcommunity.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl service;
    
    @Mock
    private BCryptPasswordEncoder encoder;
    
    @Mock
    IUserRepo repo;

    private User user;
    private UserRequestDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User("John", "Password@123");
        user.setId(1);
        userDTO = new UserRequestDTO("John", "Password@123");
        
    }

    @Test
    void testGetUser_Success() throws DeveloperCommunityException {
        when(repo.existsById(1)).thenReturn(true);
        when(repo.findById(1)).thenReturn(Optional.of(user));

        User result = service.getUser(1);

        assertEquals("John", result.getUsername());
        assertEquals("Password@123", result.getPassword());
    }

    @Test
    void testGetUser_Failure() {
        when(repo.existsById(1)).thenReturn(false);

        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.getUser(1)
        );

        assertEquals("User with 1 not found.", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() throws DeveloperCommunityException {
        when(repo.findById(1)).thenReturn(Optional.of(user));
        when(repo.findByUsername("John")).thenReturn(Optional.of(user));
        

        String result = service.updateUser(1, userDTO);

        assertEquals("User updated successfully", result);
        verify(repo).save(user);
    }

    @Test
    void testUpdateUser_Failure_UserNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());
        when(encoder.encode("NewPassword@123")).thenReturn("EncodedPassword");
        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.updateUser(1, userDTO)
        );

        assertEquals("User with ID 1 not found.", exception.getMessage());
    }

    @Test
    void testUpdateUser_Failure_InvalidPassword() {
        when(repo.findById(1)).thenReturn(Optional.of(user));
        UserRequestDTO weakPasswordDTO = new UserRequestDTO("John", "weak");

        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.updateUser(1, weakPasswordDTO)
        );

        assertTrue(exception.getMessage().contains("Password doesn't fit the criteria"));
    }

    @Test
    void testDeleteUser_Success() throws DeveloperCommunityException {
        when(repo.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(repo).delete(user);

        String result = service.deleteUser(1);

        assertEquals("User deleted successfully", result);
    }

    @Test
    void testDeleteUser_Failure() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        DeveloperCommunityException exception = assertThrows(
            DeveloperCommunityException.class,
            () -> service.deleteUser(1)
        );

        assertEquals("User with ID 1 not found.", exception.getMessage());
    }
}