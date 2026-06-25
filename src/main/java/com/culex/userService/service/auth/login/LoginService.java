    package com.culex.userService.service.auth.login;

    import com.culex.userService.DB.entities.RefreshToken;
    import com.culex.userService.DB.entities.User;
    import com.culex.userService.DB.repositories.RefreshTokenRepository;
    import com.culex.userService.DB.repositories.UserRepository;
    import com.culex.userService.service.auth.JWT.JwtTokenGenerator;
    import com.culex.userService.service.auth.JWT.RefreshTokenData;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;

    import java.time.Instant;

    @Service
    public class LoginService {
        private final JwtTokenGenerator tokenGenerator;
        private final RefreshTokenRepository refreshTokenRepository;
        private final LoginValidation loginValidation;
        private final UserRepository userRepository;

        @Autowired
        public LoginService(JwtTokenGenerator tokenGenerator, RefreshTokenRepository refreshTokenRepository, LoginValidation loginValidation,UserRepository userRepository) {
            this.tokenGenerator = tokenGenerator;
            this.loginValidation=loginValidation;
            this.refreshTokenRepository = refreshTokenRepository;
            this.userRepository=userRepository;
        }

        @Transactional
        public LoginResponse login(String password, String username) {
            User user=userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            loginValidation.allValidation(password, user);

            RefreshTokenData refreshTokenData = tokenGenerator.generateRefreshToken(user.getUsername(), user.getId());

            String accessToken = tokenGenerator.generateAccessToken(username, user.getId());
            String refreshToken=refreshTokenData.token();

            saveRefreshToken(refreshTokenData, user);

            return new LoginResponse(accessToken, refreshToken);
        }

        private void saveRefreshToken(RefreshTokenData refreshTokenData, User user) {
            String jti=refreshTokenData.jti();
            Instant expiresAt=refreshTokenData.expiresAt();

            RefreshToken refreshToken = new RefreshToken(jti, expiresAt, user);

            refreshTokenRepository.save(refreshToken);
        }
    }