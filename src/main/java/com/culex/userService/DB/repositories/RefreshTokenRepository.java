package com.culex.userService.DB.repositories;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUser(User user);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteAllByUser(User user);
}
