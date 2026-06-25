package com.culex.userService.controllers.auth.JWT;

import com.culex.userService.service.auth.JWT.TokenUpdater;
import com.culex.userService.service.auth.JWT.UpdateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("UpdateTokenController Unit Tests")
public class UpdateTokenControllerTest {

    @MockitoBean
    private TokenUpdater tokenUpdater;

    @Autowired
    private UpdateTokenController updateTokenController;

    @Test
    @DisplayName("updateToken: should return OK with new tokens on success")
    void updateToken_ValidRequest_ShouldReturnOkWithTokens() {
        // Arrange
        Long userId = 1L;
        String jti = "unique-jti-12345";

        // Используем UpdateResponse вместо Map
        UpdateResponse newTokens = new UpdateResponse(
                "new-access-token",
                "new-refresh-token"
        );

        when(tokenUpdater.updateToken(userId, jti)).thenReturn(newTokens);

        UpdateTokenDto dto = new UpdateTokenDto(jti);

        // Act
        ResponseEntity<?> response = updateTokenController.updateToken(dto, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newTokens, response.getBody());
        verify(tokenUpdater).updateToken(userId, jti);
    }

    @Test
    @DisplayName("updateToken: should propagate exception when token is invalid")
    void updateToken_InvalidJti_ShouldPropagateException() {
        // Arrange
        Long userId = 1L;
        String jti = "invalid-jti";

        when(tokenUpdater.updateToken(userId, jti))
                .thenThrow(new RuntimeException("Refresh token not found"));

        UpdateTokenDto dto = new UpdateTokenDto(jti);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> updateTokenController.updateToken(dto, userId));
        verify(tokenUpdater).updateToken(userId, jti);
    }
}