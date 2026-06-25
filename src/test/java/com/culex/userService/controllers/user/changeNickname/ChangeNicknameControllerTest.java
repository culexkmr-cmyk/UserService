package com.culex.userService.controllers.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.service.user.changeNickname.ChangeNicknameService;
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
@DisplayName("ChangeNicknameController Unit Tests")
public class ChangeNicknameControllerTest {

    @MockitoBean
    private ChangeNicknameService changeNicknameService;

    @Autowired
    private ChangeNicknameController changeNicknameController;

    @Test
    @DisplayName("updateNickname: should return OK with success message on success")
    void updateNickname_ValidRequest_ShouldReturnOk() {
        Long userId = 1L;
        String newNickname = "NewCoolNick";
        String username = "testUser";

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(username);
        when(changeNicknameService.changeNickname(newNickname, userId)).thenReturn(mockUser);

        ChangeNicknameDto dto = new ChangeNicknameDto(newNickname);
        ResponseEntity<?> response = changeNicknameController.updateNickname(dto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success nickname change for user " + username, response.getBody());
        verify(changeNicknameService).changeNickname(newNickname, userId);
    }

    @Test
    @DisplayName("updateNickname: should propagate exception when nickname is invalid")
    void updateNickname_InvalidNickname_ShouldPropagateException() {
        Long userId = 1L;
        String invalidNickname = "";

        when(changeNicknameService.changeNickname(invalidNickname, userId))
                .thenThrow(new IllegalArgumentException("Nickname cannot be empty"));

        ChangeNicknameDto dto = new ChangeNicknameDto(invalidNickname);

        assertThrows(IllegalArgumentException.class, 
                () -> changeNicknameController.updateNickname(dto, userId));
        verify(changeNicknameService).changeNickname(invalidNickname, userId);
    }
}