package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(validation).allValidation(newNickname, mockUser);
        when(repository.save(any(User.class))).thenReturn(mockUser);

        User result = changeNicknameService.changeNickname(newNickname, userId);

        assertSame(mockUser, result);

        verify(repository).findById(userId);
        verify(validation).allValidation(newNickname, mockUser);
        verify(mockUser).setNickname(newNickname);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertSame(mockUser, capturedUser);
    }

    @Test
    @DisplayName("changeNickname: should throw EntityNotFoundException if user not found")
    void changeNickname_UserNotFound_ShouldThrow() {
        String newNickname = "NewNickname";
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> changeNicknameService.changeNickname(newNickname, userId));

        verify(repository).findById(userId);
        verify(validation, never()).allValidation(anyString(), any());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changeNickname: should throw exception and NOT save user if validation fails")
    void changeNickname_InvalidData_ShouldThrowAndNotSave() {
        String newNickname = null;
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        doThrow(new IllegalArgumentException("Nickname must be less than 26 character"))
                .when(validation).allValidation(newNickname, mockUser);

        assertThrows(IllegalArgumentException.class, () -> changeNicknameService.changeNickname(newNickname, userId));

        verify(repository).findById(userId);
        verify(validation).allValidation(newNickname, mockUser);
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changeNickname: should throw exception if user is deleted (validation throws)")
    void changeNickname_DeletedUser_ShouldThrowAndNotSave() {
        String newNickname = "NewNickname";
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        doThrow(new IllegalStateException("User is deleted"))
                .when(validation).allValidation(newNickname, mockUser);

        assertThrows(IllegalStateException.class, () -> changeNicknameService.changeNickname(newNickname, userId));

        verify(repository).findById(userId);
        verify(validation).allValidation(newNickname, mockUser);
        verify(repository, never()).save(any(User.class));
    }
}