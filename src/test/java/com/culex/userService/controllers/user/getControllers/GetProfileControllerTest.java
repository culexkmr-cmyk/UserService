package com.culex.userService.controllers.user.getControllers;

import com.culex.userService.service.user.getData.GetDataDto;
import com.culex.userService.service.user.getData.GetUserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("GetProfile Controller Unit Tests")
public class GetProfileControllerTest {

    @MockitoBean
    private GetUserData getUserData;

    @Autowired
    private GetProfile getProfile;

    @Test
    @DisplayName("getProfileByUsername: should return OK with user data when user exists")
    void getProfileByUsername_ValidUsername_ShouldReturnOk() {
        // Arrange
        String username = "validUser";
        Instant createdAt = Instant.now();
        // Используем GetDataDto вместо Map
        GetDataDto mockProfile = new GetDataDto(
                username,
                1L,
                "user@example.com",
                "CoolNick",
                createdAt
        );

        when(getUserData.getDataByUsername(username)).thenReturn(mockProfile);

        // Act
        ResponseEntity<?> response = getProfile.getProfileByUsername(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockProfile, response.getBody());
        verify(getUserData).getDataByUsername(username);
    }

    @Test
    @DisplayName("getProfileByUsername: should propagate exception when user not found")
    void getProfileByUsername_UserNotFound_ShouldPropagateException() {
        // Arrange
        String username = "nonExistentUser";

        when(getUserData.getDataByUsername(username))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> getProfile.getProfileByUsername(username));
        verify(getUserData).getDataByUsername(username);
    }

    @Test
    @DisplayName("getProfileById: should return OK with user data when user exists")
    void getProfileById_ValidId_ShouldReturnOk() {
        // Arrange
        Long userId = 1L;
        Instant createdAt = Instant.now();
        // Используем GetDataDto вместо Map
        GetDataDto mockProfile = new GetDataDto(
                "testUser",
                userId,
                "user@example.com",
                "CoolNick",
                createdAt
        );

        when(getUserData.getDataById(userId)).thenReturn(mockProfile);

        // Act
        ResponseEntity<?> response = getProfile.getProfileById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockProfile, response.getBody());
        verify(getUserData).getDataById(userId);
    }

    @Test
    @DisplayName("getProfileById: should propagate exception when user not found")
    void getProfileById_UserNotFound_ShouldPropagateException() {
        // Arrange
        Long userId = 999L;

        when(getUserData.getDataById(userId))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> getProfile.getProfileById(userId));
        verify(getUserData).getDataById(userId);
    }
}