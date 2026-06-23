package com.culex.userService.service.user.getData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("GetDataValidation Unit Tests")
class GetDataValidationTest {

    @Autowired
    private GetDataValidation validation;

    @Test
    @DisplayName("allValidation(Long): should pass for valid userId")
    void allValidationById_ValidUserId_ShouldPass() {
        Long validUserId = 1L;
        assertDoesNotThrow(() -> validation.allValidation(validUserId));
    }

    @Test
    @DisplayName("allValidation(Long): should throw exception for null userId")
    void allValidationById_NullUserId_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation((Long) null));
        assertEquals("User ID must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation(String): should pass for valid username")
    void allValidationByUsername_ValidUsername_ShouldPass() {
        String validUsername = "validUser123";
        assertDoesNotThrow(() -> validation.allValidation(validUsername));
    }

    @Test
    @DisplayName("allValidation(String): should throw exception for null username")
    void allValidationByUsername_NullUsername_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation((String) null));
        assertEquals("Username must not be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation(String): should throw exception for blank username")
    void allValidationByUsername_BlankUsername_ShouldThrow() {
        String blankUsername = "   ";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(blankUsername));
        assertEquals("Username must not be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation(String): should throw exception for empty username")
    void allValidationByUsername_EmptyUsername_ShouldThrow() {
        String emptyUsername = "";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(emptyUsername));
        assertEquals("Username must not be null or blank", exception.getMessage());
    }
}