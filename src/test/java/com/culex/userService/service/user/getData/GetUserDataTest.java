package com.culex.userService.service.user.getData;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GetUserDataTest {

    @MockitoBean
    private UserRepository repository;

    @MockitoBean
    private GetDataValidation validation;

    @Autowired
    private GetUserData getUserData;

    @Test
    @DisplayName("getDataById: should validate, find user and return GetDataDto for valid id")
    void getDataById_ValidId_ShouldReturnDto() {
        Long userId = 1L;
        String username = "testUser";
        String email = "test@example.com";
        String nickname = "TestNick";
        Instant createdAt = Instant.now();

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.getEmail()).thenReturn(email);
        when(mockUser.getNickname()).thenReturn(nickname);
        when(mockUser.getCreatedAt()).thenReturn(createdAt);

        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        GetDataDto result = getUserData.getDataById(userId);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(nickname, result.nickname());
        assertEquals(createdAt, result.createdAt());

        verify(validation).allValidation(userId);
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("getDataById: should throw EntityNotFoundException if user not found")
    void getDataById_UserNotFound_ShouldThrow() {
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> getUserData.getDataById(userId));

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(validation).allValidation(userId);
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("getDataById: should throw exception if validation fails")
    void getDataById_InvalidId_ShouldThrowAndNotFind() {
        Long userId = null;

        doThrow(new IllegalArgumentException("User ID must not be null"))
                .when(validation).allValidation(userId);

        assertThrows(IllegalArgumentException.class,
                () -> getUserData.getDataById(userId));

        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("getDataByUsername: should validate, find user and return GetDataDto for valid username")
    void getDataByUsername_ValidUsername_ShouldReturnDto() {
        String username = "testUser";
        Long userId = 1L;
        String email = "test@example.com";
        String nickname = "TestNick";
        Instant createdAt = Instant.now();

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.getEmail()).thenReturn(email);
        when(mockUser.getNickname()).thenReturn(nickname);
        when(mockUser.getCreatedAt()).thenReturn(createdAt);

        when(repository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        GetDataDto result = getUserData.getDataByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(nickname, result.nickname());
        assertEquals(createdAt, result.createdAt());

        verify(validation).allValidation(username);
        verify(repository).findByUsername(username);
    }

    @Test
    @DisplayName("getDataByUsername: should throw UsernameNotFoundException if user not found")
    void getDataByUsername_UserNotFound_ShouldThrow() {
        String username = "nonExistentUser";

        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> getUserData.getDataByUsername(username));

        assertEquals("User not found with username: nonExistentUser", exception.getMessage());
        verify(validation).allValidation(username);
        verify(repository).findByUsername(username);
    }

    @Test
    @DisplayName("getDataByUsername: should throw exception if validation fails")
    void getDataByUsername_InvalidUsername_ShouldThrowAndNotFind() {
        String username = null;

        doThrow(new IllegalArgumentException("Username must not be null or blank"))
                .when(validation).allValidation(username);

        assertThrows(IllegalArgumentException.class,
                () -> getUserData.getDataByUsername(username));

        verify(repository, never()).findByUsername(anyString());
    }
}