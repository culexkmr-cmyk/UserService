package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class LogoutValidation {

    private void checkTokenOwner(User user, RefreshToken refreshToken){
        if (!refreshToken.getUser().getId().equals(user.getId())) {
            throw new BadCredentialsException("Token ownership verification failed");}
    }
    public void allValidation(User user, RefreshToken refreshToken){
        checkTokenOwner(user,refreshToken);
    }
}
