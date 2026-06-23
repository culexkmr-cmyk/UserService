package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
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

    @Autowired
    private DeleteService deleteService;

    @Test
    @DisplayName("deleteUser: should validate and anonymize user for valid userId")
    void deleteUser_ValidUserId_ShouldAnonymizeUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        
        when(deleteValidation.allValidation(userId)).thenReturn(mockUser);

        deleteService.deleteUser(userId);

        verify(deleteValidation).allValidation(userId);

        verify(mockUser).anonymize();

        verifyNoMoreInteractions(deleteValidation, mockUser);
    }

    @Test
    @DisplayName("deleteUser: should throw exception and NOT anonymize user if validation fails")
    void deleteUser_InvalidUserId_ShouldThrowAndNotAnonymize() {
        Long invalidUserId = 999L;

        when(deleteValidation.allValidation(invalidUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        assertThrows(IllegalArgumentException.class, () -> {
            deleteService.deleteUser(invalidUserId);
        });

        verify(deleteValidation).allValidation(invalidUserId);
    }
}