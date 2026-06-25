package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DeleteServiceTest {

    @MockitoBean
    private DeleteValidation deleteValidation;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private DeleteService deleteService;

    @Test
    @DisplayName("deleteUser: should validate and anonymize user for valid userId")
    void deleteUser_ValidUserId_ShouldAnonymizeUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        deleteService.deleteUser(userId);

        verify(deleteValidation).allValidation(mockUser);
        verify(mockUser).anonymize();

        verifyNoMoreInteractions(deleteValidation, mockUser);
    }

    @Test
    @DisplayName("deleteUser: should throw exception and NOT anonymize if validation fails")
    void deleteUser_InvalidUserId_ShouldThrowAndNotAnonymize() {
        Long userId = 999L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        doThrow(new IllegalArgumentException("User already deleted"))
                .when(deleteValidation).allValidation(mockUser);

        assertThrows(IllegalArgumentException.class, () -> deleteService.deleteUser(userId));

        verify(deleteValidation).allValidation(mockUser);
        verify(mockUser, never()).anonymize();
    }

    @Test
    @DisplayName("deleteUser: should throw EntityNotFoundException if user not found")
    void deleteUser_UserNotFound_ShouldThrow() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deleteService.deleteUser(userId));

        verify(deleteValidation, never()).allValidation(any());
        verify(userRepository).findById(userId);
    }
}