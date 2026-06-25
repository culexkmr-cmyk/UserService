package com.culex.userService.DB.repositories;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    void deleteAllByUser(User user);
    Optional<PasswordResetToken> findByToken(String token);
}
