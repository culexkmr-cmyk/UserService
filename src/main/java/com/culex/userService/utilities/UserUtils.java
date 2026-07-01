package com.culex.userService.utilities;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class UserUtils {

    public static User findUserByUsername(UserRepository userRepository, String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с username '%s' не найден", username)
                ));
    }
}