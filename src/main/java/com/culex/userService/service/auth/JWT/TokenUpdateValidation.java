package com.culex.userService.service.auth.JWT;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenUpdateValidation {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public TokenUpdateValidation(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository){
        this.userRepository=userRepository;
        this.refreshTokenRepository=refreshTokenRepository;
    }

    public User userIdValidation(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    public RefreshToken jtiValidation(String jti){
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new EntityNotFoundException("Token not found with jti: " + jti));
    }
    public ValidationResponse allValidation(String jti, Long userId){
        return new ValidationResponse( jtiValidation(jti), userIdValidation(userId));
    }
    public record ValidationResponse(RefreshToken refreshToken, User user){};
}