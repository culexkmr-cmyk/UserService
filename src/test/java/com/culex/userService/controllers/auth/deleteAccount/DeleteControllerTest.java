package com.culex.userService.controllers.auth.deleteAccount;

import com.culex.userService.service.auth.deleteAccount.DeleteService;
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
@DisplayName("DeleteController Unit Tests")
public class DeleteControllerTest {

    @MockitoBean
    private DeleteService deleteService;

    @Autowired
    private DeleteController deleteController;

    @Test
    @DisplayName("deleteAccount: should return OK when deletion succeeds")
    void deleteAccount_ValidUserId_ShouldReturnOk() {
        Long userId = 1L;

        doNothing().when(deleteService).deleteUser(userId);

        ResponseEntity<?> response = deleteController.deleteAccount(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(deleteService).deleteUser(userId);
    }

    @Test
    @DisplayName("deleteAccount: should propagate exception when user not found")
    void deleteAccount_UserNotFound_ShouldPropagateException() {
        Long userId = 999L;

        doThrow(new RuntimeException("User not found"))
                .when(deleteService).deleteUser(userId);

        assertThrows(RuntimeException.class, () -> deleteController.deleteAccount(userId));
        verify(deleteService).deleteUser(userId);
    }
}