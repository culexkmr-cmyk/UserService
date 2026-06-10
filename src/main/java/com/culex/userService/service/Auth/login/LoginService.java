package com.culex.userService.service.Auth.login;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import com.culex.userService.service.Auth.JWT.JwtTokenGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginService {
    private final JwtTokenGenerator tokenGenerator;
    private final PasswordEncoder encoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(JwtTokenGenerator tokenGenerator, PasswordEncoder encoder,
                        UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.tokenGenerator = tokenGenerator;
        this.encoder = encoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoginResponse login(String password, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + username);
        }
        String accessToken = tokenGenerator.generateAccessToken(username, user.getId());
        JwtTokenGenerator.RefreshTokenData refreshTokenData = tokenGenerator.generateRefreshToken(user.getUsername(), user.getId());
        saveRefreshToken(refreshTokenData.jti(), refreshTokenData.expiresAt(), user);
        return new LoginResponse(accessToken, refreshTokenData.token());
    }

    private void saveRefreshToken(String jti, Instant expiresAt, User user) {
        RefreshToken refreshToken = new RefreshToken(jti, expiresAt, user);
        refreshTokenRepository.save(refreshToken);
    }
}