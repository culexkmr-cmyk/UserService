package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class LogoutValidation {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public LogoutValidation(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository=refreshTokenRepository;
    }
    public RefreshToken jtiValidation(String jti){
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));
    }
    public void checkTokenOwner(Long userId, RefreshToken refreshToken){
        if (!refreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Token ownership verification failed");}
    }
    public void allValidation(Long userId, String jti){
        checkTokenOwner(userId, jtiValidation(jti));
    }
}
