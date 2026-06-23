package com.culex.userService.service.auth.JWT;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
public class TokenUpdater {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenUpdateValidation validation;

    @Autowired
    public TokenUpdater(JwtTokenGenerator jwtTokenGenerator, RefreshTokenRepository refreshTokenRepository, TokenUpdateValidation validation) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
        this.validation=validation;
    }

    @Transactional
    public UpdateResponse updateToken(Long userId, String jti) {
        TokenUpdateValidation.ValidationResponse response=validation.allValidation(jti, userId);
        User user=response.user();
        RefreshToken oldRefreshToken=response.refreshToken();
        String username=user.getUsername();
        JwtTokenGenerator.RefreshTokenData refreshTokenData = jwtTokenGenerator.generateRefreshToken(username, userId);
        saveRefreshToken(refreshTokenData.jti(), refreshTokenData.expiresAt(), user, oldRefreshToken);
        String accessToken = jwtTokenGenerator.generateAccessToken(username, userId);
        return new UpdateResponse(accessToken, refreshTokenData.token());
    }
    private void saveRefreshToken(String jti, Instant expiresAt, User user, RefreshToken oldRefreshToken) {
        refreshTokenRepository.delete(oldRefreshToken);
        RefreshToken newRefreshToken = new RefreshToken(jti, expiresAt, user);
        refreshTokenRepository.save(newRefreshToken);
    }
}