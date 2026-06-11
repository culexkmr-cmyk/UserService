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
        private final RefreshTokenRepository refreshTokenRepository;
        private final LoginValidation loginValidation;
        @Autowired
        public LoginService(JwtTokenGenerator tokenGenerator, RefreshTokenRepository refreshTokenRepository, LoginValidation loginValidation) {
            this.tokenGenerator = tokenGenerator;
            this.loginValidation=loginValidation;
            this.refreshTokenRepository = refreshTokenRepository;
        }

        public LoginResponse login(String password, String username) {
            User user=loginValidation.allValidation(password, username);
            String accessToken = tokenGenerator.generateAccessToken(username, user.getId());
            JwtTokenGenerator.RefreshTokenData refreshTokenData = tokenGenerator.generateRefreshToken(user.getUsername(), user.getId());
            saveRefreshToken(refreshTokenData.jti(), refreshTokenData.expiresAt(), user);
            return new LoginResponse(accessToken, refreshTokenData.token( ));
        }
        @Transactional
        private void saveRefreshToken(String jti, Instant expiresAt, User user) {
            RefreshToken refreshToken = new RefreshToken(jti, expiresAt, user);
            refreshTokenRepository.save(refreshToken);
        }
    }