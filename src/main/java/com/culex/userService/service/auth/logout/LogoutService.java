package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.culex.userService.utilities.Test.findEntity;

@Transactional
@Service
public class LogoutService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutValidation logoutValidation;

    @Autowired
    public LogoutService(RefreshTokenRepository refreshTokenRepository, LogoutValidation logoutValidation,UserRepository userRepository){
        this.refreshTokenRepository=refreshTokenRepository;
        this.logoutValidation=logoutValidation;
        this.userRepository=userRepository;
    }
    public void logout(Long userId, String jti){
        User user = findEntity(userRepository, userId);
        RefreshToken refreshToken = findEntity(refreshTokenRepository, jti);
        logoutValidation.allValidation(user, refreshToken);
        refreshTokenRepository.deleteByJti(jti);
    }
}
