package com.culex.userService.service.user.changePassword;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.PasswordResetTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordValidation {



    private final int maxPasswordLength;
    private final int minPasswordLength;

    @Autowired
    public ChangePasswordValidation(
                                    @Value("${app.auth.register.password.length.max}") int maxPasswordLength,
                                    @Value("${app.auth.register.password.length.min}") int minPasswordLength) {
        this.maxPasswordLength=maxPasswordLength;
        this.minPasswordLength=minPasswordLength;
    }
    private void passwordValidation(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < minPasswordLength || rawPassword.length() > maxPasswordLength) {
            throw new IllegalArgumentException("Password must be longer than " + (minPasswordLength-1)+" and less than " + (maxPasswordLength-1)+ " character");
        }
    }
    private void checkTokenOwner(User user, PasswordResetToken token){
        if (!token.getUser().getId().equals(user.getId())) {
            throw new BadCredentialsException("Token ownership verification failed");}
    }
    public void allValidation(String rawPassword, User user, PasswordResetToken token){
        passwordValidation(rawPassword);
        checkTokenOwner(user, token);
    }
}
