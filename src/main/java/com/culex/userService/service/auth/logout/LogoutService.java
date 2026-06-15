package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Transactional
@Service
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutValidation logoutValidation;

    @Autowired
    public LogoutService(RefreshTokenRepository refreshTokenRepository, LogoutValidation logoutValidation){
        this.refreshTokenRepository=refreshTokenRepository;
        this.logoutValidation=logoutValidation;
    }
    public void logout(Long userId, String jti){
        logoutValidation.allValidation(userId, jti);
        refreshTokenRepository.deleteByJti(jti);
    }
}
