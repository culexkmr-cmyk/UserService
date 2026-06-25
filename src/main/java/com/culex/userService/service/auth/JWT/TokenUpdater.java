package com.culex.userService.service.auth.JWT;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.culex.userService.utilities.Test.findEntity;

@Service
public class TokenUpdater {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public TokenUpdater(JwtTokenGenerator jwtTokenGenerator, UserRepository userRepository,RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository=userRepository;
    }

    @Transactional
    public UpdateResponse updateToken(Long userId, String jti) {
        User user=findEntity(userRepository, userId);
        RefreshToken oldRefreshToken=findEntity(refreshTokenRepository, jti);

        String username=user.getUsername();

        RefreshTokenData refreshTokenData = jwtTokenGenerator.generateRefreshToken(username, userId);
        saveRefreshToken(refreshTokenData.jti(), refreshTokenData.expiresAt(), user, oldRefreshToken);

        String refreshToken=refreshTokenData.token();
        String accessToken = jwtTokenGenerator.generateAccessToken(username, userId);

        return new UpdateResponse(accessToken, refreshToken);
    }
    private void saveRefreshToken(String jti, Instant expiresAt, User user, RefreshToken oldRefreshToken) {
        refreshTokenRepository.delete(oldRefreshToken);
        RefreshToken newRefreshToken = new RefreshToken(jti, expiresAt, user);
        refreshTokenRepository.save(newRefreshToken);
    }
}