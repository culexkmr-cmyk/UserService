package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChangeNicknameServiceTest {

    @MockitoBean
    private ChangeNicknameValidation validation;

    @MockitoBean
    private UserRepository repository;

    @Autowired
    private ChangeNicknameService changeNicknameService;

    @Test
    @DisplayName("changeNickname: should validate, set new nickname and save user for valid data")
    void changeNickname_ValidData_ShouldSetAndSave() {
        String newNickname = "NewCoolNickname";
        Long userId = 1L;
        String oldNickname = "OldNickname";

        User mockUser = mock(User.class);
        when(mockUser.getNickname()).thenReturn(oldNickname);

        when(validation.allValidation(newNickname, userId)).thenReturn(mockUser);
        when(repository.save(any(User.class))).thenReturn(mockUser);

        User result = changeNicknameService.changeNickname(newNickname, userId);

        assertSame(mockUser, result);

        verify(validation).allValidation(newNickname, userId);
        verify(mockUser).setNickname(newNickname);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertSame(mockUser, capturedUser);
    }

    @Test
    @DisplayName("changeNickname: should throw exception and NOT save user if validation fails")
    void changeNickname_InvalidData_ShouldThrowAndNotSave() {
        String newNickname = null;
        Long userId = 1L;

        doThrow(new IllegalArgumentException("Nickname must be less than 26 character"))
                .when(validation).allValidation(newNickname, userId);

        assertThrows(IllegalArgumentException.class, () -> {
            changeNicknameService.changeNickname(newNickname, userId);
        });

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changeNickname: should throw exception if user is deleted")
    void changeNickname_DeletedUser_ShouldThrowAndNotSave() {
        String newNickname = "NewNickname";
        Long userId = 1L;

        when(validation.allValidation(newNickname, userId))
                .thenThrow(new IllegalStateException("User is deleted"));

        assertThrows(IllegalStateException.class, () -> {
            changeNicknameService.changeNickname(newNickname, userId);
        });

        verify(repository, never()).save(any(User.class));
    }
}