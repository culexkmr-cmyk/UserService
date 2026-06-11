package com.culex.userService.service.Auth.register;

import org.apache.commons.validator.routines.EmailValidator;
import com.culex.userService.DB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegValidation {
    private final UserRepository repository;

    @Autowired
    public RegValidation(UserRepository repository) {
        this.repository = repository;
    }

    public void usernameValidation(String username) {
        if (username == null || username.length() <= 3 || username.length() >= 32) {
            throw new IllegalArgumentException("Username must be longer than 3 and less than 32 characters");
        }
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User with this username already exists");
        }
        if (username.startsWith("deleted_")) {
            throw new IllegalArgumentException("Username cannot start with 'deleted_'");
        }
    }

    public void passwordValidation(String rawPassword) {
        if (rawPassword == null || rawPassword.length() <= 8 || rawPassword.length() >= 128) {
            throw new IllegalArgumentException("Password must be longer than 8 and less than 128 characters");
        }
    }

    public void nicknameValidation(String nickname) {
        if (nickname == null || nickname.length() > 50) {
            throw new IllegalArgumentException("Nickname must not exceed 50 characters");
        }
    }

    public void emailValidation(String email) {
        if (email == null || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (email.startsWith("deleted_")) {
            throw new IllegalArgumentException("Email cannot start with 'deleted_'");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public void allValidation(String nickname, String rawPassword, String username, String email) {
        nicknameValidation(nickname);
        passwordValidation(rawPassword);
        usernameValidation(username);
        emailValidation(email);
    }
}