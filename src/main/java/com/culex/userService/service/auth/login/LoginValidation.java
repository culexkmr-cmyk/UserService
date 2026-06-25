package com.culex.userService.service.auth.login;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginValidation {
    private final PasswordEncoder encoder;

    @Autowired
    private LoginValidation(PasswordEncoder encoder) {
        this.encoder = encoder;
    }
    private void passwordValidation(String password, User user) {
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + user.getUsername());
        }
    }
    private void isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User is deleted");
        }
    }

    public void allValidation(String password, User user){
        passwordValidation(password, user);
        isUserDelete(user);
    }
}
