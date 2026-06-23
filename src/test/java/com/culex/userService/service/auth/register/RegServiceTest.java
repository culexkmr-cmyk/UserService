package com.culex.userService.service.auth.register;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RegServiceTest {
    @MockitoBean
    private RegValidation validation;
    @MockitoBean
    private UserRepository repository;
    @MockitoBean
    private PasswordEncoder encoder;
    @Autowired
    private RegService regService;

    @Test
    @DisplayName("createUser: should validate, hash password and save user for valid data")
    void createUser_ValidData_ShouldHashAndSave() {
        String rawPassword = "SecurePass123!";
        String hashedPassword = "HASHED_SECURE_PASS_123!";
        String username = "validUser";
        String nickname = "ValidNick";
        String email = "valid@example.com";

        when(encoder.encode(rawPassword)).thenReturn(hashedPassword);

        User savedUser = new User(hashedPassword, username, nickname, email);
        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = regService.createUser(rawPassword, username, nickname, email);

        assertSame(savedUser, result);

        verify(validation).allValidation(nickname, rawPassword, username, email);

        verify(encoder).encode(rawPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(hashedPassword, capturedUser.getPassword());
        assertEquals(username, capturedUser.getUsername());
        assertEquals(nickname, capturedUser.getNickname());
        assertEquals(email, capturedUser.getEmail());
    }

    @Test
    @DisplayName("createUser: should throw exception and NOT save user if validation fails")
    void createUser_InvalidData_ShouldThrowAndNotSave() {
        String rawPassword = "weakPass";
        String username = "user";
        String nickname = "nick";
        String email = "email";

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(validation).allValidation(nickname, rawPassword, username, email);

        assertThrows(IllegalArgumentException.class, () -> {
            regService.createUser(rawPassword, username, nickname, email);
        });

        verify(encoder, never()).encode(anyString());
        verify(repository, never()).save(any(User.class));
    }
}