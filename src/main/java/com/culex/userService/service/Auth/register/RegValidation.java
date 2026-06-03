package com.culex.userService.service.Auth.register;

import com.culex.userService.DB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class RegValidation {
    private final UserRepository repository;

    // Регулярное выражение для базовой проверки корректности email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Autowired
    public RegValidation(UserRepository repository) {
        this.repository = repository;
    }

    public void usernameValidation(String username) {
        if (username == null || username.length() <= 3 || username.length() >= 32) {
            throw new IllegalArgumentException("Имя пользователя должно быть длиннее 3 и меньше 32 символов");
        }
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
    }

    public void passwordValidation(String rawPassword) {
        if (rawPassword == null || rawPassword.length() <= 16 || rawPassword.length() >= 128) {
            throw new IllegalArgumentException("Пароль должен быть длиннее 16 и меньше 128 символов");
        }
    }

    public void nicknameValidation(String nickname) {
        if (nickname == null || nickname.length() > 50) {
            throw new IllegalArgumentException("Никнейм не должен превышать 50 символов");
        }
    }

    public void emailValidation(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
    }

    public void allValidation(String nickname, String rawPassword, String username, String email) {
        nicknameValidation(nickname);
        passwordValidation(rawPassword);
        usernameValidation(username);
        emailValidation(email);
    }
}
