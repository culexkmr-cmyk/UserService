package com.culex.userService.service.user.getData;

import org.springframework.stereotype.Component;

@Component
public class GetDataValidation {

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be null or blank");
        }
    }
    public void allValidation(Long userId){
        validateUserId(userId);
    }
    public void allValidation(String username){
        validateUsername(username);
    }
}